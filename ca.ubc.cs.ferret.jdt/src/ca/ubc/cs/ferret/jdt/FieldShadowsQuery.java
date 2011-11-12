package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class FieldShadowsQuery extends JavaRelatedConceptualQuery<FerretObject> {

	public FieldShadowsQuery() {}

	public String getDescription() {
		return "shadows";
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IRelation shadows = getSphere().resolve(monitor, ObjectOrientedRelations.OP_SHADOWS, parameter);
		for(FerretObject s : shadows) {
			SimpleSolution sol = new SimpleSolution(this, this);
			sol.setPrimaryEntityName("shadowed");
			sol.add("shadowed", s);
		}
	}
}
