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
package ca.ubc.cs.ferret.tests;

import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import org.eclipse.core.runtime.IProgressMonitor;

public class GradSiblings extends AbstractSingleParmConceptualQuery<GraduateStudent> {
    
    public GradSiblings() {}

    @Override
    protected void internalRun(IProgressMonitor monitor) {
        Object schema = new Object();
        monitor.beginTask(getDescription(), 20);
        for (GraduateStudent fellowStudent : CategorizationTest.getPeople()) {
            if(!fellowStudent.equals(parameter) && fellowStudent.supervisor.equals(parameter.supervisor)) {
                SimpleSolution sol = new SimpleSolution(this, schema);
                sol.add("sibling", fellowStudent);
                addSolution(sol);
            }
        }
    }

    public String getDescription() {
        return "fellow students of " + parameter;
    }

	public boolean isValid() {
		return true;
	}

}
