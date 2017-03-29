/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.kenyon.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.commons.lang.math.IntRange;
import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.kenyon.KTransaction;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.objhdl.ResourceMapper;
import edu.se.evolution.kenyon.ConfigData;
import edu.se.evolution.kenyon.ConfigGraph;
import edu.se.evolution.kenyon.KenyonException;
import edu.se.evolution.kenyon.graph.Node;
import edu.se.evolution.kenyon.scm.CommitData;
import edu.se.evolution.kenyon.scm.ConfigDelta;
import edu.se.evolution.kenyon.scm.Revision;
import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;

public class TxCochanged
		extends AbstractKenyonIntersectingConceptualQuery<KTransaction,FerretObject>  {

	// Must be kept in sync with schemas
	protected static final String ALTERNATE_IDENTIFIERS = "ALTERNATE_IDENTIFIERS";

	public TxCochanged() {}

	@Override
	protected String getSubDescription() {
		return "co-changes";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<FerretObject> performQuery(KTransaction ktx,
			IProgressMonitor monitor) {
		try {
			Session s = KenyonSphereHelper.getDefault().getSession();
			SCMReposConfigSpec spec = ktx.getConfigSpec();
			ConfigData configData = ConfigData.findConfigDataBySpec(s, spec);
			ConfigDelta delta = 
				(ConfigDelta) s.createQuery("from ConfigDelta cd where cd.destSpec.id = ?")
					.setLong(0, spec.getId().longValue())
					.setMaxResults(1)
					.uniqueResult();
			MultiMap<String,CommitData> changes = new MultiHashMap<String, CommitData>();
			if(delta != null) {	// if null then ktx is the first transaction for this project
				for(CommitData cd : (Collection<CommitData>)delta.getCommitData()) {
					changes.put(cd.getFilename(), cd);
				}
			}

			Collection<Object> results = new HashSet<Object>();
			// New files committed as part of a transaction don't have any commitdata, apparently
			for(Revision rev : ktx.getRevisions()) {
				String filename = rev.getFilename();
				boolean contentsFound = false;
				if(changes.containsKey(filename)) {
					for(ConfigGraph graph : (Collection<ConfigGraph>)configData.getConfigGraphs()) {
						Collection<Node> coNodes = getNodesFromCommitResults(graph, filename, changes.get(filename));
						if(coNodes != null) {
							// if coNodes != null but is empty, then the file has content,
							// but there were no meaningful changes
							contentsFound = true;
							results.addAll(coNodes);
						}
					}
				}
				if(!contentsFound) {
					// if there were no graphs providing a further description of the change,
					// then just add the file
					results.add(rev);
//					IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
//					path = path.append(filename);
//					IFile resources[] = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
//					if(resources != null) {
//						Collections.addAll(files, resources);
//					}
				}
			}
			// Now need some way to map the identifier back to real objects...
			return FerretObject.wrap(results, Fidelity.Approximate, getSphere());
		} catch(HibernateException e) {
			FerretPlugin.log(e);
		}
		return null;
	}

	/**
	 * Reimplement ConfigGraph.getNodesfromCommitResult() as it relies
	 * on ConfigGraph.getLineNodes() which doesn't use getNodeContaining()
	 */
	protected Collection<Node> getNodesFromCommitResults(ConfigGraph graph, 
			String filename, Collection<CommitData> cdata) {
		Set<Node> results = new HashSet<Node>();
		try {
			Node file = graph.getNode(ResourceMapper.HANDLE_TYPE_RESOURCE + ":" + filename);
			if(file == null) { return null; }
			List<IntRange> changes = new ArrayList<IntRange>(cdata.size());
			for(CommitData cd : cdata) {
				changes.add(new IntRange(cd.getNewRevStartLine(), cd.getNewRevEndLine()));
			}
			findContainedNodes(graph, file, changes, results);
		} catch(Exception e) {
			FerretPlugin.log(e);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	protected void findContainedNodes(ConfigGraph graph, Node fnode,
			Collection<IntRange> changes, Set<Node> contained) {
		Set<Node> children = graph.getChildrenNodes(fnode);
		if(children != null && !children.isEmpty()) {
			for(Node child : children) {
				findContainedNodes(graph, child, changes, contained);
			}
		} else {
			for(IntRange cd : changes) {
				try {
					if(cd.overlapsRange(new IntRange(graph.getSchema().getFirstLine(fnode),
							graph.getSchema().getLastLine(fnode)))) {
						contained.add(fnode);
					}
				} catch(KenyonException e) {
					// ignore
				}
			}
		}
		
	}

	@Override
	protected void processSolution(FerretObject e) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("node", e);
		addSolution(s);
	}

	public boolean isValid() {
		return true;
	}

}
