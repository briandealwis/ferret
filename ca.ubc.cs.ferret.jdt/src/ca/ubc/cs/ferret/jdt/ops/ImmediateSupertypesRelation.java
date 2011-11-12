package ca.ubc.cs.ferret.jdt.ops;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class ImmediateSupertypesRelation extends AbstractCollectionBasedRelation<IType> {

	public ImmediateSupertypesRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return Arrays.asList(JavaModelHelper.getDefault().getSupertypes(input, monitor));
	}

}
