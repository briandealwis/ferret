/*
 * Copyright 2004  X
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

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

    /* (non-Javadoc)
     * @see ca.ubc.cs.queryguru.IConceptualQuery#run(org.eclipse.core.runtime.IProgressMonitor)
     * @author bsd
     * @since X
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see ca.ubc.cs.queryguru.IConceptualQuery#getDescription()
     * @author bsd
     * @since X
     */
    public String getDescription() {
        return "interfaces specifying method"; 
    }

}
