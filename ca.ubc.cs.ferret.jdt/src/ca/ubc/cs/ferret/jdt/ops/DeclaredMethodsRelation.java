package ca.ubc.cs.ferret.jdt.ops;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class DeclaredMethodsRelation extends AbstractCollectionBasedRelation<IType> {

	public DeclaredMethodsRelation() {	}

		@Override
		protected Collection<IMethod> realizeCollection(IType input) {
			try {
				return Arrays.asList(input.getMethods());
			} catch(JavaModelException e) {
				JavaModelHelper.logJME(e);
				return Collections.EMPTY_LIST;
			}
		}

		@Override
		protected Class<IType> getInputType() {
			return IType.class;
		}
}
