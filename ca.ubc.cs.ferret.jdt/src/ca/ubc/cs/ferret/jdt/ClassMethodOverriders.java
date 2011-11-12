/*
 * Copyright 2004  X
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

/**
 * @author bsd
 */
public class ClassMethodOverriders extends JavaRelatedConceptualQuery<IMethod> {
    
    public boolean validateParameter(IMethod method) {
        try {
            if(!method.getDeclaringType().isClass()) { return false; }
        } catch(JavaModelException e) { return false; }
        return true;
    }

    public ClassMethodOverriders() {}

    protected void internalRun(IProgressMonitor monitor) {
    	IRelation overridden = getSphere().resolve(monitor, 
    			ObjectOrientedRelations.OP_METHOD_OVERRIDERS, parameter);
    	for (FerretObject fo : overridden) {
    		SimpleSolution s = new SimpleSolution(this, fo);
    		s.add("alternative", fo);
    		addSolution(s);
    	}
    }

    public String getDescription() {
        return "overridden by";
    }

}
