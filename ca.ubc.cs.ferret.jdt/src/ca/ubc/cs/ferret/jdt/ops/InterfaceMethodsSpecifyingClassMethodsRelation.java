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
package ca.ubc.cs.ferret.jdt.ops;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.NamedJoinRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class InterfaceMethodsSpecifyingClassMethodsRelation 
		extends AbstractCollectionBasedRelation<FerretObject> {

	public InterfaceMethodsSpecifyingClassMethodsRelation() {}
	
	@Override
	protected Class<FerretObject> getInputType() {
		return FerretObject.class;
	}

	@Override
	protected FerretObject checkInput(FerretObject input) {
		try {
			return input.resolve(ObjectOrientedRelations.OP_IS_METHOD) == null
			? null : input;
        } catch(UnsupportedOperationException e) {
            return null;	// this isn't an error
		}
	}
	
	@Override
	protected Collection<?> realizeCollection(FerretObject input) {
		FerretObject sig = null;
		for(FerretObject s : input.resolve(ObjectOrientedRelations.OP_SIGNATURE)) {
			sig = s;
		}
		if(sig == null) { return Collections.EMPTY_LIST; }
		IRelation methods = new NamedJoinRelation(
				ObjectOrientedRelations.OP_DECLARING_TYPE,
				ObjectOrientedRelations.OP_IS_CLASS,
				ObjectOrientedRelations.OP_SUPERTYPES,
				ObjectOrientedRelations.OP_IS_INTERFACE,
				ObjectOrientedRelations.OP_DECLARED_METHODS)
		.resolve(monitor, input.getSphere(), input);
		Collection<FerretObject> results = new HashSet<FerretObject>();
		for(FerretObject m : methods) {
			try {
				for(FerretObject s : m.resolve(ObjectOrientedRelations.OP_SIGNATURE)) {
					if(sig.equals(s)) {
						results.add(m);
						break;
					}
				}
			} catch(UnsupportedOperationException e) {
				// ignore
			}		
		}
		return results;
	}


//	@Override
//	protected Collection<?> realizeCollection(FerretObject input) {
//		monitor.beginTask("Finding interface methods specifying " + input, 10);
//		IRelation supers = resolver.topPerform(new SubProgressMonitor(monitor, 4), 
//				ObjectOrientedRelations.OP_SUPERINTERFACES, 
//				new FerretObject(input.getDeclaringType(), getResultsFidelity(), resolver.getRootSphere()));
//		Set<IMethod> results = new HashSet<IMethod>();
//        for (FerretObject fo : supers) {
//        	ConversionResult<IType> cr = fo.convert(IType.class, 1, Fidelity.Approximate);
//            try {
//                if(cr == null || !cr.getSingleResult().isInterface()) { continue; }
//            } catch(JavaModelException e) { continue; }
//            resultingFidelity = resultingFidelity.least(cr.getFidelity());
//            IMethod matches[] = cr.getSingleResult().findMethods(input);
//            if(matches == null) { continue; }
//            for(IMethod m : matches) {
//            	if(m.exists()) { results.add(m); }
//            }
//        }
//		return results;
//	}
}
