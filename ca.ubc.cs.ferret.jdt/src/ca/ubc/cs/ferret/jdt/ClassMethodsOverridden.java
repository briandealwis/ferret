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
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class ClassMethodsOverridden extends JavaRelatedConceptualQuery<IMethod> {

	public ClassMethodsOverridden() {}

    public boolean validateParameter(IMethod method) {
        try {
            if(!method.getDeclaringType().isClass()) { return false; }
        } catch(JavaModelException e) { return false; }
        return true;
    }
    
    protected void internalRun(IProgressMonitor monitor) {
        IRelation overridden = getSphere().resolve(monitor, 
        		ObjectOrientedRelations.OP_METHODS_OVERRIDDEN, parameter);
        for (FerretObject fo : overridden) {
            SimpleSolution s = new SimpleSolution(this, this);
            s.add("alternative", fo);
            addSolution(s);
        }
    }

    public String getDescription() {
        return "overrides";
    }

}
