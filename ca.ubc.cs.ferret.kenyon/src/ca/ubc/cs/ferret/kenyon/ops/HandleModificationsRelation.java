package ca.ubc.cs.ferret.kenyon.ops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.lang.math.IntRange;

import ca.ubc.cs.clustering.utils.TimedCachePolicy;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.MultiHashSetMap;
import ca.ubc.cs.ferret.kenyon.IKHandlesSource;
import ca.ubc.cs.ferret.kenyon.KTransaction;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import edu.se.evolution.kenyon.KenyonException;
import edu.se.evolution.kenyon.graph.Node;
import edu.se.evolution.kenyon.graph.schemas.JavaSchema;
import edu.se.evolution.kenyon.scm.CommitData;
import edu.se.evolution.kenyon.scm.ConfigDelta;
import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;
import edu.se.evolution.kenyon.scm.SCMTransaction;

/**
 * Return the transactions in which the element described by the
 * provided handle was modified.  Figuring this out was *painful*!!!
 * 
 * Note: there are two types of the filenames used, and this must be kept in
 * sync with the SimpleJavaFactExtractor.  The first type are KENYON-style
 * file names, which were the file names when extracting the data.  These
 * file names are relative to the working-space; for CVS, these include the
 * module name as a prefix.  The second type are Eclipse-style rewritten
 * filenames.  These have been rewritten to have the project-identifier as
 * a prefix.  For example:
 * <UL>
 * <LI> Kenyon-style: <TT>AminoCombinator/src/org/manumission/chem/AminoAcid.java</TT>
 * 		where <TT>AminoCombinator</TT> is the CVS module name (other
 *		SCMs may not have this).  This should match the content
 *		of Revision.filename.
 * <LI> Eclipse-style: <TT>ac-cvs-head/src/org/manumission/chem/AminoAcid.java</TT>
 * 		where <TT>ac-cvs-head</TT> is the project identifier.
 * </UL>
 * 
 * Kenyon-style names are essentially opaque tokens to index against
 * Revision.filename to identify those nodes that may have changed as part of
 * a transaction.  (FIXME: apparently only the CVS-SCM stores individual
 * Revisions; perhaps we could use CommitData's instead?) 
 */ 
public class HandleModificationsRelation extends AbstractCollectionBasedRelation<IKHandlesSource> {

	public HandleModificationsRelation() {}

	@Override
	protected Class<IKHandlesSource> getInputType() {
		return IKHandlesSource.class;
	}

	@Override
	protected Fidelity getResultsFidelity() {
		// FIXME: could try to figure out the transaction corresponding to the
		// source currently loaded... 
		return Fidelity.Approximate;
	}

	@Override
	protected Collection<?> realizeCollection(IKHandlesSource input) {
		List<KTransaction> transactions = getCachedTransactions().get(input);
		if(transactions != null) { return transactions; }
		try {
			Session s = KenyonSphereHelper.getDefault().getSession();
			MultiMap<String,String> nodes = resolveNodesAndDefiningFiles(s, input);
			if(nodes == null || nodes.isEmpty()) { return Collections.EMPTY_SET; }

			transactions = findModifyingTransactions(s, nodes);
			getCachedTransactions().insert(input, transactions);
			return transactions;
		} catch (HibernateException e) {
			FerretPlugin.log(e);
			return Collections.EMPTY_SET;
		} catch (KenyonException e) {
			FerretPlugin.log(e);
			return Collections.EMPTY_SET;
		}
	}

	protected TimedCachePolicy<Object, List<KTransaction>> getCachedTransactions() {
		return KenyonSphereHelper.getDefault().getCachedTransactions();
	}


	protected MultiMap<String, String> resolveNodesAndDefiningFiles(Session s,
			IKHandlesSource source) throws HibernateException {

		for(int stage = 0; stage < source.getNumberStages(); stage++) {
			Query q = s.createQuery(
					"select distinct node, elements(attrs.hibernateMe) "
					+ "from edu.se.evolution.kenyon.graph.Node node, AttributeSet attrs "
					+ "where " 
					+ source.asSQL(stage, "node.myName")
					+ "    and attrs = node.attributes.attributes['filename']");
			q.setCacheable(true);
			List<Object[]> results = q.list();
			if(results != null && !results.isEmpty()) {
				MultiMap<String,String> mapping = new MultiHashSetMap<String, String>();
				for(Object[] result : results) {
					mapping.put(((Node)result[0]).getName(), (String)result[1]);
				}
				return mapping;
			}
		}
		return null;
	}

	protected List<KTransaction> findModifyingTransactions(Session s,
			MultiMap<String, String> nodes) throws KenyonException, HibernateException {
		Query q = s.createQuery(
				"select node, spec "
				+ "from Node node, SCMReposConfigSpec spec "
				+ "    inner join spec.completingTransactions as transaction "
				+ "    inner join transaction.revisions as revision, "
				+ "  ConfigData configData, ConfigGraph graph "
				+ "order by spec.time "
				+ "where "
				// FIXME: would be nice to use CommitData here instead
				+ describeNodes("node.myName", "revision.filename", nodes)
				+ "  and node in elements(graph.nodes) "
				+ "  and graph in elements(configData.configGraphs) "
				+ "  and spec = configData.spec");
		q.setCacheable(true);
		List<Object[]> changes = q.list();
		List<KTransaction> solutions = new ArrayList<KTransaction>();
		if(!changes.isEmpty()) {
			// always include first recorded set of transactions: there's no previous transactions,
			// so this must be the first time the element was committed
			Object[] solution = changes.get(0);
			Node node = (Node)solution[0];
			SCMReposConfigSpec spec = (SCMReposConfigSpec)solution[1];
			for(SCMTransaction tx : (Collection<SCMTransaction>)spec.getCompletingTransactions()) {
				solutions.add(new KTransaction(spec, tx));
			}
			for(int i = 1; i < changes.size(); i++) {
				solution = changes.get(i);
				node = (Node)solution[0];
				IntRange nr = new IntRange(
						JavaSchema.getDefault().getFirstLine(node),
						JavaSchema.getDefault().getLastLine(node));
				spec = (SCMReposConfigSpec)solution[1];
				SCMReposConfigSpec prevSpec = spec.getPrevPersistedSpec(s);
				ConfigDelta delta = ConfigDelta.getConfigDeltaBetween(s, prevSpec, spec);
				Collection<String> nodeFiles = nodes.get(node.getName());
				for(Object o : delta.getCommitData()) {
					CommitData cd = (CommitData)o;
					// Note: can't use ConfigGraph.getNodesFromCommit* as they use
					// ConfigData.getLineNodes(), which depends on nodes corresponding to
					// individual lines, which we don't do in our JavaSchema 
					if(!nodeFiles.contains(cd.getFilename())) { continue; }
					IntRange diffRange = new IntRange(cd.getNewRevStartLine(), cd.getNewRevEndLine());
					if(nr.overlapsRange(diffRange)) {
						// This isn't quite right: CommitData are to the configData/spec,
						// not to the individual transactions.
						for(SCMTransaction tx : (Collection<SCMTransaction>)spec.getCompletingTransactions()) {
							if(tx.getFiles().contains(cd.getFilename())) {
								solutions.add(new KTransaction(spec, tx));
							}
						}
						break;
					}
				}
			}
		}
		return solutions;
	}

	protected String describeNodes(String nodeNameVar, String fileVar,
			MultiMap<String, String> nodes) {
		StringBuffer result = new StringBuffer();
		if(nodes.size() > 1) { result.append('('); }
		int nodeCount = 0;
		for(String nodeName : nodes.keySet()) {
			result.append('('); result.append(nodeNameVar); 
			result.append(" = '");
			result.append(nodeName); 
			result.append("' and ");
			result.append(fileVar); 
			Collection<String> files = nodes.get(nodeName);
			result.append(files.size() == 1 ? " = " : " in (");
			int fileCount = 0;
			for(String fileName : files) {
				result.append('\'');
				result.append(fileName);
				result.append('\'');
				if(++fileCount < files.size()) { result.append(", "); }
			}
			result.append(')');
			if(++nodeCount < nodes.size()) {
				result.append(" and ");
			}
		}
		if(nodes.size() > 1) { result.append(')'); }
		return result.toString();
	}
}