package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class FieldGettersQuery extends
		JavaIntersectionConceptualQuery<IField,FerretObject> {

	/**
	 * The required public 0-argument constructor as per the extension-point.
	 */
	public FieldGettersQuery() {
	}

	public String getSubDescription() {
		return "field getters";
	}

	@Override
	protected void processSolution(FerretObject member) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("getter", member);
		addSolution(s);
	}

	@Override
	protected Collection<FerretObject> performQuery(IField field,
			IProgressMonitor monitor) {
		IRelation getters = getSphere().resolve(monitor,
				ObjectOrientedRelations.OP_GETTERS, field);
		return getters.asCollection();
	}
}
