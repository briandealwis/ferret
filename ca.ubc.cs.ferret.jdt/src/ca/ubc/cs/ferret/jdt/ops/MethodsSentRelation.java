package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class MethodsSentRelation extends AbstractCollectionBasedRelation<IJavaElement> {

	public MethodsSentRelation() {}

	@Override
	protected Class<IJavaElement> getInputType() {
		return IJavaElement.class;
	}

	@Override
	protected Collection<?> realizeCollection(IJavaElement input) {
		return JavaModelHelper.getDefault().getMethodsSent(input, monitor);
	}

}
