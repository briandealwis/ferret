/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;

public class MethodReturnTypeProvider extends AbstractJavaAttributeValue<IMethod,Object> {

    public MethodReturnTypeProvider() {
    }

    public Object getCategory(IMethod object) {
    	Object result = JavaModelHelper.getDefault().getReturnType(object);
    	return result == null ?  IClassifier.UNDETERMINED_ATTRIBUTE_VALUE : result;
    }

	public Object[] getCategories() {
		return null;
	}

	public String getCategoryText(Object category) {
		if(category instanceof IType) {
			return ((IType)category).getFullyQualifiedName();
		} else if(category instanceof String) {
			return (String)category;
		}
		return category.toString();
	}
}
