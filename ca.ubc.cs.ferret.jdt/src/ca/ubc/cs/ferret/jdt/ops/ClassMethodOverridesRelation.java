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

public class ClassMethodOverridesRelation
		extends AbstractCollectionBasedRelation<FerretObject> {

	public ClassMethodOverridesRelation() {}
	
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
				ObjectOrientedRelations.OP_SUBTYPES,
				ObjectOrientedRelations.OP_IS_CLASS,
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


}
