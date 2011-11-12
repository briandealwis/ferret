package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class CatchesExceptionRelation extends AbstractCollectionBasedRelation<IType> {

	public CatchesExceptionRelation() {}

	@Override
	protected IType checkInput(IType input) {
		return JavaModelHelper.getDefault().isThrowable(input, new NullProgressMonitor()) ? input : null;
	}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return JavaModelHelper.getDefault().getCatchLocations(input, monitor);
    }

}