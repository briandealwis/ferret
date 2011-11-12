package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class SuperclassRelation extends AbstractCollectionBasedRelation<IType> {
	
	@Override
	protected IType checkInput(IType input) {
		try {
			return input.isClass() ? input : null;
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return null;
		}
	}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		Collection<IType> results = new LinkedList<IType>();
		IType supercl = JavaModelHelper.getDefault().getSuperclass(input, monitor);
		if(supercl != null) { results.add(supercl); }
		return results;
	}
}
