/*
 * Copyright 2004  X
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;

import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

/**
 * @author bsd
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
        IRelation impls = getSphere().resolve(monitor,
        		ObjectOrientedRelations.OP_IMPLEMENTORS, parameter);  
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
