package ca.ubc.cs.ferret.jdt;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class CommonImplementations extends JavaIntersectionConceptualQuery<FerretObject, FerretObject> {

	public CommonImplementations() {
	}

	@Override
	protected String getSubDescription() {
		return "implementations";
	}

	@Override
	protected Collection<FerretObject> performQuery(FerretObject it, IProgressMonitor monitor) {
		IRelation supertypes = getSphere().resolve(monitor, ObjectOrientedRelations.OP_SUPERCLASSES, it);
		Collection<FerretObject> results = new ArrayList<FerretObject>();
		results.add(it);	// add ourselves in case we're the supertype of all the others
		results.addAll(supertypes.asCollection());
		return results;
	}

	@Override
	protected void processSolution(FerretObject e) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("supertype", e);
		addSolution(s);
	}

}
