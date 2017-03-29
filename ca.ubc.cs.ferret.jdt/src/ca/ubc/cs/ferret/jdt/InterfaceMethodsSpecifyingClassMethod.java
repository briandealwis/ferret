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
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author bsd
 */
public class InterfaceMethodsSpecifyingClassMethod extends JavaRelatedConceptualQuery<IMethod> {

    public InterfaceMethodsSpecifyingClassMethod() {}

    protected boolean validateParameter(IMethod method) {
		try {
            return method.getDeclaringType().isClass()  && 
            	Flags.isPublic(method.getFlags());
        } catch(JavaModelException e) {
            return false;
        }
    }

    protected void internalRun(IProgressMonitor monitor) {
        monitor.beginTask(getDescription(), 10);
        IRelation specifications = getSphere().resolve(monitor,
        		ObjectOrientedRelations.OP_SPECIFICATIONS, parameter);
        for (FerretObject m : specifications) {
        	SimpleSolution s = new SimpleSolution(this, m);
        	s.add("specifies", m);
        	addSolution(s);
        }
        monitor.worked(4);
        // This really isn't that useful, it it?
        //        if (getSolutions().isEmpty()) {
        //            addFact(new Fact(this,
        //                    "There are no interfaces specifying this method"));
        //        }
        monitor.done();
    }

    public String getDescription() {
        return "interfaces specifying method"; 
    }

}
