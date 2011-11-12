package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class ImplementorsQuery extends JavaIntersectionConceptualQuery<FerretObject,FerretObject> {
	public String getSubDescription() {
		return "implementors";
	}

	@Override
	protected Collection<FerretObject> performQuery(FerretObject it, IProgressMonitor monitor) {
		IRelation op =
			getSphere().resolve(monitor,
					ObjectOrientedRelations.OP_IMPLEMENTORS, it);
		Collection<FerretObject> impls = op.asCollection();
		impls.remove(it);	// implementors on a concrete Java class returns itself
		return impls;
	}

	@Override
	protected void processSolution(FerretObject clazz) {
		SimpleSolution s = new SimpleSolution(this, clazz);
		s.add("implementor", clazz);
		addSolution(s);
	}

}
