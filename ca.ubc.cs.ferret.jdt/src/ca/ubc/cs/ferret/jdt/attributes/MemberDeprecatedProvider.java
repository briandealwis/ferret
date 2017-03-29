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
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

public class MemberDeprecatedProvider 
		extends AbstractJavaAttributeValue<IMember,String> {
	protected final static String DEPRECATED = "deprecated";
	protected final static String ACTIVE = "non-deprecated";
	
    public MemberDeprecatedProvider() {
    }

    public String[] getCategories() { return new String[] { DEPRECATED, ACTIVE }; }

    public String getCategory(IMember object) {
        try {
            return Flags.isDeprecated(object.getFlags()) ? DEPRECATED : ACTIVE;
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

	public String getCategoryText(String category) {
		return category;
	}
}
