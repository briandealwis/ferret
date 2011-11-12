/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.tests;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;

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
