package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class CastsToType extends JavaIntersectionConceptualQuery<IType,FerretObject> {

	public CastsToType() {}

	@Override
	protected String getSubDescription() {
		return "casts to type";
	}

	@Override
	protected Collection<FerretObject> performQuery(IType it, IProgressMonitor monitor) {
        IRelation setters = 
        	getSphere().resolve(monitor, ObjectOrientedRelations.OP_CASTS_TO_TYPE, it);
		return setters.asCollection();
	}

	@Override
	protected void processSolution(FerretObject member) {
        SimpleSolution s = new SimpleSolution(this, this);
        s.add("caster", member);
        addSolution(s);
	}

}
