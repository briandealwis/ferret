package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

/**
 * Provide the classes that implement the input type.  An abstract class' implementors are all 
 * its concrete subclasses. An interface's implementors are the concrete implementations.
 * @author Brian de Alwis
 */
public class ClassesImplementingInterfaceRelation extends
		AbstractCollectionBasedRelation<IType> {

	public ClassesImplementingInterfaceRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return JavaModelHelper.getDefault().getImplementingClasses(input, monitor);
	}
}
