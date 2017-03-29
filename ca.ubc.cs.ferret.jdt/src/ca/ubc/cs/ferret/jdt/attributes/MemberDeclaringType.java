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

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;

public class MemberDeclaringType extends AbstractJavaAttributeValue<IMember,IType> {

    public MemberDeclaringType() {
    }

    public IType getCategory(IMember object) {
        return object.getDeclaringType();
    }

	public IType[] getCategories() {
		return null;
	}

	public String getCategoryText(IType category) {
		return category.getFullyQualifiedName();
	}

}
