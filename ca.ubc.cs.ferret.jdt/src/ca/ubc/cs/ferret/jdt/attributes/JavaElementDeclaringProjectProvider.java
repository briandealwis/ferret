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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public class JavaElementDeclaringProjectProvider 
		extends AbstractJavaAttributeValue<IJavaElement,IJavaElement> {

    public JavaElementDeclaringProjectProvider() {}

    public IJavaElement[] getCategories() { return null; }

    public IJavaElement getCategory(IJavaElement element) {
    	while (element != null && 
    			!(element instanceof IPackageFragmentRoot)) {
    		element = element.getParent();
    	}
//    	if(element.getParent() instanceof IJavaProject) {
//    		return element.getParent();
//    	}
    	return element;
    }

	public String getCategoryText(IJavaElement category) {
		return category.getElementName();
	}
}
