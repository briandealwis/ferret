package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class ReferencesTypeRelation extends AbstractCollectionBasedRelation<IType> {

	public ReferencesTypeRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return JavaModelHelper.getDefault().getReferences(input, monitor);
	}

}
