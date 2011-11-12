package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class InstantiatingClassRelation extends AbstractCollectionBasedRelation<IType> {

	public InstantiatingClassRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected IType checkInput(IType input) {
		try {
			return input.isClass() ? input : null; 
		} catch(JavaModelException e) {
			JavaModelHelper.logJME(e);
			return null;
		}
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return JavaModelHelper.getDefault().getMethodsInstantiatingClass(input, monitor);
	}
}
