package ca.ubc.cs.ferret.jdt.ops;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class ImmediateSubclassesRelation extends AbstractCollectionBasedRelation<IType> {
	
	public ImmediateSubclassesRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return Arrays.asList(JavaModelHelper.getDefault().getSubclasses(input, monitor));
	}

}
