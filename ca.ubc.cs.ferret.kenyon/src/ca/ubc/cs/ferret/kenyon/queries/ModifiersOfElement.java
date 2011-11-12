package ca.ubc.cs.ferret.kenyon.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class ModifiersOfElement extends 
		AbstractKenyonIntersectingConceptualQuery<Object,FerretObject> {

    public ModifiersOfElement() {}
    
	public boolean isValid() {
		return true;
	}

	@Override
	protected String getSubDescription() {
		return "modified on";
	}

	@Override
	protected Collection<FerretObject> performQuery(Object it,
			IProgressMonitor monitor) {
        IRelation modifiers = 
        	getSphere().resolve(monitor, KenyonSphereHelper.OP_MODIFICATIONS, it);
        return modifiers.asCollection();
	}

	@Override
	protected void processSolution(FerretObject tx) {
    	SimpleSolution s = new SimpleSolution(this, this);
		s.add("transaction", tx);
    	addSolution(s);
	}
}
