/*******************************************************************************
 * Copyright (c) 2004 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IType;

/**
 * Find Java methods that instantiate a particular type.
 */
public class MethodsInstantiatingType extends JavaIntersectionConceptualQuery<IType, FerretObject> {

	protected Clustering<Object> clusteringByType;
	protected Multimap<FerretObject, FerretObject> mapping = MultimapBuilder.hashKeys().hashSetValues().build();
	
	@Override
	protected String getSubDescription() {
        return "instantiators";
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		clusteringByType = new Clustering<Object>("type instantiated"); 
		super.internalRun(monitor);
		if (!solutions.isEmpty()) {
			addClustering(clusteringByType);
        }
	}

	@Override
	protected Collection<FerretObject> performQuery(IType it,
			IProgressMonitor monitor) {
		try {
			monitor.beginTask(getClass().getName(), 10);
			IRelation op = getSphere().resolve(new SubProgressMonitor(monitor, 4),
					ObjectOrientedRelations.OP_IMPLEMENTORS, it);
			Collection<FerretObject> implementors = op.asCollection();

			Collection<FerretObject> results = new HashSet<FerretObject>();
			IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 6);
			subMonitor.beginTask("Looking for implementors", implementors.size());
			for(FerretObject implementor : implementors) {
				IRelation instantiators = getSphere().resolve(new SubProgressMonitor(subMonitor, 1),
						ObjectOrientedRelations.OP_INSTANTIATORS,
						implementor);
				for(FerretObject instantiator : instantiators) {
					results.add(instantiator);
					mapping.put(instantiator, implementor);
				}
			}
			return results;
		} finally {
			monitor.done();
		}
	}

	@Override
	protected void processSolution(FerretObject e) {
        SimpleSolution s = new SimpleSolution(this, this);
        s.add("instantiator", e);
        s.setPrimaryEntityName("instantiator");
		for(FerretObject instantiatedType : mapping.get(e)) {
//			s.add(new StupidlySimpleRelation(e, "instantiates", instantiatedType));
			clusteringByType.findCluster(instantiatedType).add(s);
		}
        addSolution(s);
	}
}
