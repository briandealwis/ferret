package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class DeclaringTypeRelation extends AbstractCollectionBasedRelation<IMember>{

	@Override
	protected Class<IMember> getInputType() {
		return IMember.class;
	}

	@Override
	protected Collection<?> realizeCollection(IMember input) {
		LinkedList<IType> declaringType = new LinkedList<IType>();
		declaringType.add(input.getDeclaringType());
		return declaringType;
	}
}
