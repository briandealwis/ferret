/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
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
