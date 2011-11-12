package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IMember;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

// don't bother with projects, packages, or libraries
public class TypesReferencedRelation extends AbstractCollectionBasedRelation<IMember> {

	public TypesReferencedRelation() {}

	@Override
	protected Class<IMember> getInputType() {
		return IMember.class;
	}

	@Override
	protected Collection<?> realizeCollection(IMember input) {
		return JavaModelHelper.getDefault().getReferencedTypes(input, monitor);
	}
}
