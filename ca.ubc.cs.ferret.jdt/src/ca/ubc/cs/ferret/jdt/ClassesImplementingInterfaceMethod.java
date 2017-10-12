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
import ca.ubc.cs.ferret.model.NamedJoinRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;

/**
 * Find methods that implement the provided interface method.
 */
public class ClassesImplementingInterfaceMethod extends
        JavaRelatedConceptualQuery<IMethod> {
    /**
     * The required public 0-argument constructor as per the extension-point.
     */
    public ClassesImplementingInterfaceMethod() {
    }

    protected void internalRun(IProgressMonitor monitor) {
        monitor.beginTask(getDescription(), 20);
		IRelation impls = new NamedJoinRelation(ObjectOrientedRelations.OP_IMPLEMENTORS,
				ObjectOrientedRelations.OP_DECLARING_TYPE).resolve(monitor, getSphere(), parameter);
        for(FerretObject m : impls) {
        	SimpleSolution s = new SimpleSolution(this, this);
        	s.add("implementation", m);
        	addSolution(s);
        }
    }

    public String getDescription() {
        return "classes implementing method";
    }

}
