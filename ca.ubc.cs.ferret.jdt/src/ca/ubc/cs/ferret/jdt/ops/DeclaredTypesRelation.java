package ca.ubc.cs.ferret.jdt.ops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaVariable;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class DeclaredTypesRelation extends AbstractCollectionBasedRelation<Object> {

	public DeclaredTypesRelation() {	}

	@Override
	protected Class<Object> getInputType() {
		return Object.class;
	}

	@Override
	protected Collection<?> realizeCollection(Object input) {
		String typeSignature = null;
		IMember member = null;
		try {
			if(input instanceof IField) {
				typeSignature = ((IField)input).getTypeSignature();
				member = (IField)input;
			} else if(input instanceof ILocalVariable) {
				typeSignature = ((ILocalVariable)input).getTypeSignature();
				member = (IMember)((ILocalVariable)input).getParent();
			} else if(input instanceof IJavaVariable) {
				IJavaType t = ((IJavaVariable)input).getJavaType();
				typeSignature = t.getSignature();
			}
		} catch(JavaModelException e) {
			JavaModelHelper.logJME(e);
			return Collections.EMPTY_LIST;
		} catch(DebugException e) {
			return Collections.EMPTY_LIST;
		}
		if(typeSignature == null || 
				Signature.getTypeSignatureKind(typeSignature) == Signature.BASE_TYPE_SIGNATURE) {
			return Collections.EMPTY_LIST;
		}
		Collection<IType> results = new ArrayList<IType>(1);
		IType type = JavaModelHelper.getDefault().resolveSignature(typeSignature, member);
		if(type != null) { results.add(type); }
		for(String typeArg : Signature.getTypeArguments(typeSignature)) {
			if((type = JavaModelHelper.getDefault().resolveSignature(typeArg, member)) != null) {
				results.add(type);
			}
		}
		return results;
	}

	@Override
	protected Object checkInput(Object input) {
		return (input instanceof IField || input instanceof ILocalVariable
				|| input instanceof IJavaVariable) ? input : null;
	}

}
