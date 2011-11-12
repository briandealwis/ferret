package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class TestsInstanceof extends
		JavaIntersectionConceptualQuery<IType,FerretObject> {

    public String getSubDescription() {
        return "tested for instanceof";
    }

	@Override
	protected Collection<FerretObject> performQuery(IType it, IProgressMonitor monitor) {
        IRelation testers = 
        	getSphere().resolve(monitor, ObjectOrientedRelations.OP_INSTANCEOF, it);
		return testers.asCollection();
	}

	@Override
	protected void processSolution(FerretObject member) {
        SimpleSolution s = new SimpleSolution(this, this);
        s.add("tests-instanceof", member);
        addSolution(s);
	}

}
