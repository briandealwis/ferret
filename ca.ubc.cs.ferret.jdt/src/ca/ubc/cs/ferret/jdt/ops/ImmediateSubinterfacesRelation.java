package ca.ubc.cs.ferret.jdt.ops;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class ImmediateSubinterfacesRelation extends AbstractCollectionBasedRelation<IType> {

	public ImmediateSubinterfacesRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return Arrays.asList(JavaModelHelper.getDefault().getSubinterfaces(input, monitor));
	}

	@Override
	protected IType checkInput(IType input) {
		try {
			return input.isInterface() ? input : null; 
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
		    return null;
		}   
	}
}
