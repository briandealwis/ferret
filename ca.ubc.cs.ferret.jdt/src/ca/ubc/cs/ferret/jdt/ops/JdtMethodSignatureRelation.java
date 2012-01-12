package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class JdtMethodSignatureRelation extends AbstractCollectionBasedRelation<IMethod> {

	public JdtMethodSignatureRelation() {}

	@Override
	protected Class<IMethod> getInputType() {
		return IMethod.class;
	}

	@Override
	protected Collection<?> realizeCollection(IMethod input) {
		try {
			Collection<String> result = new LinkedList<String>();
			result.add(input.getElementName() + JavaModelHelper.getDefault().resolvedMethodSignature(input));
			return result;
		} catch(JavaModelException e) {
			JavaModelHelper.logJME(e);
			return Collections.emptyList();
		}
	}
}
