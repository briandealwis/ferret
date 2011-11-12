/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;

public class JavaElementEnclosingPackageProvider 
		extends AbstractJavaAttributeValue<IJavaElement,IPackageFragment> {

    public JavaElementEnclosingPackageProvider() {
    }

    public IPackageFragment getCategory(IJavaElement object) {
        return (IPackageFragment)object.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
    }

	public IPackageFragment[] getCategories() {
		return null;
	}

	public String getCategoryText(IPackageFragment category) {
		return category.getElementName();
	}
}
