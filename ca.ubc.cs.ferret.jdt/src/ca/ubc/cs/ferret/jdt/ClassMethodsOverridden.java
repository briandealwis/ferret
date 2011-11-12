package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

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
