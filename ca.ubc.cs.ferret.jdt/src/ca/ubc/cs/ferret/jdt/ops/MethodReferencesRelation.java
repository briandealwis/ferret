package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IMethod;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class MethodReferencesRelation extends AbstractCollectionBasedRelation<IMethod> {
	
	public MethodReferencesRelation() {}

	@Override
	protected Class<IMethod> getInputType() {
		return IMethod.class;
	}

	@Override
	protected Collection<?> realizeCollection(IMethod input) {
		return JavaModelHelper.getDefault().getReferences(input, monitor);
	}
}
