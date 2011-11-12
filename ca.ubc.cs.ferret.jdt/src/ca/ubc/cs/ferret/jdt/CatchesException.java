package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class CatchesException extends
		JavaIntersectionConceptualQuery<IType,FerretObject> {

	public CatchesException() {}
	
    @Override
	protected boolean validateParameter(IType type) {
        return JavaModelHelper.getDefault().isThrowable(type, new NullProgressMonitor());
	}

    public String getSubDescription() {
        return "exception catchers";
    }

	@Override
	protected Collection<FerretObject> performQuery(IType it, IProgressMonitor monitor) {
        IRelation setters = 
        	getSphere().resolve(monitor, ObjectOrientedRelations.OP_CATCHES_EXCEPTION, it);
		return setters.asCollection();
	}

	@Override
	protected void processSolution(FerretObject member) {
        SimpleSolution s = new SimpleSolution(this, this);
        s.add("catches", member);
        addSolution(s);
	}


}
