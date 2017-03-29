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
