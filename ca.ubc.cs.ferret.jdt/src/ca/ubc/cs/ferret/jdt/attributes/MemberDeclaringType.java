/*
 * Copyright 2005 by X.
 * @author bsd
 */
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
