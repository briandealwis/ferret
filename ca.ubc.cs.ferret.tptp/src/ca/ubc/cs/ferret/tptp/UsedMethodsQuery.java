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
package ca.ubc.cs.ferret.tptp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class UsedMethodsQuery extends AbstractSingleParmConceptualQuery<FerretObject> {

	public UsedMethodsQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
        IRelation methodsExecuted = getSphere().resolve(new SubProgressMonitor(monitor, 10),
        		TptpSphereHelper.OP_USED_METHODS, parameter);
        for(FerretObject m : methodsExecuted) {
            SimpleSolution s = new SimpleSolution(this, this);
            s.add("executed", m);
            addSolution(s);
        }
	}

	public String getDescription() {
		return "used methods";
	}

	public boolean isValid() {
		return true;
	}
}
