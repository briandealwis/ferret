package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class VariableDeclaredTypeQuery extends JavaRelatedConceptualQuery<FerretObject> {

	public VariableDeclaredTypeQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IRelation dt = getSphere().resolve(monitor, ObjectOrientedRelations.OP_DECLARED_TYPES, parameter);
		for(FerretObject t : dt) {
			SimpleSolution s = new SimpleSolution(this, this);
			s.add("declared-type", t);
			addSolution(s);
		}
	}

	public String getDescription() {
		return "declared type";
	}

}
