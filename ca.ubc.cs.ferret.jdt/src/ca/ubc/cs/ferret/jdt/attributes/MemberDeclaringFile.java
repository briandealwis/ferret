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
import org.eclipse.jdt.core.IMember;

// Could use ITypeRoot, but this was only introduced in 3.3
@SuppressWarnings("restriction")
public class MemberDeclaringFile extends AbstractJavaAttributeValue<IMember,IJavaElement> {

    public MemberDeclaringFile() {
    }

    public IJavaElement[] getCategories() { return null; }

    public IJavaElement getCategory(IMember object) {
        IJavaElement value = ((IJavaElement)object).getAncestor(IJavaElement.COMPILATION_UNIT);
        if(value != null) { return value; }
        return ((IJavaElement)object).getAncestor(IJavaElement.CLASS_FILE);
    }
    
	public String getCategoryText(IJavaElement category) {
		return category.getElementName();
	}
}
