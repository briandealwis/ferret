/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.jdt.attributes;

import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

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
