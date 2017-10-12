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

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;


/**
 * Find methods that have the specified return type.
 */
public class MethodsReturningType extends JavaRelatedConceptualQuery<IType> {
    public MethodsReturningType() {
	    super();
	}
	
    public boolean validateParameter(IType t) {
        return true;
    }

	protected void internalRun(IProgressMonitor monitor) {
        try {
            monitor.beginTask(getDescription(), 10);
            IRelation methods = getSphere().resolve(monitor,
            		ObjectOrientedRelations.OP_METHOD_RETURNS_TYPE, parameter);

            for(FerretObject m : methods) {
            	SimpleSolution s = new SimpleSolution(this, m);
            	s.add("returns-instance", m);
            	addSolution(s);
            }

            if (solutions.isEmpty()) {
                return;
            }

//            Cluster<ISolution> typeGroup = new Cluster<ISolution>(this, "defining type");
//            for (IType keyType : results.keySet()) {
//                ClusterGroup<ISolution> grouping = typeGroup.getGrouping(keyType);
//                if (grouping == null) {
//                    if (keyType != type) {
//                        typeGroup.addRelation(
//                                new StupidlySimpleRelation(type, "java subtype of", keyType));
//                    }
//                    grouping = typeGroup.createGrouping(keyType);
//                }
//                
//                for(IMethod m : results.get(keyType)) {
//                    SimpleSolution s = new SimpleSolution(this, m);
//                    s.add("method", m);
//                    s.add(new StupidlySimpleRelation(m, "returns", keyType));
//                    addSolution(s);
//                    grouping.add(s);
//                }
//            }
//            addCluster(typeGroup);
        } finally {
            monitor.done();
        }
	}
	
//	protected SearchPattern createReferencesPattern(IType type, IType[] supertypes) {
//	    SearchPattern root = SearchPattern.createPattern(type,
//				IJavaSearchConstants.REFERENCES);
//	    for(int i = 0; i < supertypes.length; i++) {
//	        root = SearchPattern.createOrPattern(root,
//	                	SearchPattern.createPattern(supertypes[i], IJavaSearchConstants.REFERENCES));
//	    }
//	    return root;
//	}
//
	public String getDescription() {
	    return "methods returning type";
	}
}
