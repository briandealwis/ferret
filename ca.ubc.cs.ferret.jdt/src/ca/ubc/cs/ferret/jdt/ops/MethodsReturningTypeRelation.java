package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class MethodsReturningTypeRelation extends AbstractCollectionBasedRelation<IType> {

	public MethodsReturningTypeRelation() {}
	
	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return JavaModelHelper.getDefault().getMethodsReturningExactType(input, monitor);
	}
}
