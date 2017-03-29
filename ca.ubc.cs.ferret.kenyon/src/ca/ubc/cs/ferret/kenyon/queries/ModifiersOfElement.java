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

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class ModifiersOfElement extends 
		AbstractKenyonIntersectingConceptualQuery<Object,FerretObject> {

    public ModifiersOfElement() {}
    
	public boolean isValid() {
		return true;
	}

	@Override
	protected String getSubDescription() {
		return "modified on";
	}

	@Override
	protected Collection<FerretObject> performQuery(Object it,
			IProgressMonitor monitor) {
        IRelation modifiers = 
        	getSphere().resolve(monitor, KenyonSphereHelper.OP_MODIFICATIONS, it);
        return modifiers.asCollection();
	}

	@Override
	protected void processSolution(FerretObject tx) {
    	SimpleSolution s = new SimpleSolution(this, this);
		s.add("transaction", tx);
    	addSolution(s);
	}
}
