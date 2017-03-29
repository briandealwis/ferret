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

public class MemberStaticProvider extends AbstractJavaAttributeValue<IMember,String> {

	protected static final String STATIC = "static";
	protected static final String INSTANCE = "instance";

    public MemberStaticProvider() {
    }

    public String[] getCategories() { return new String[] { STATIC, INSTANCE }; }

    public String getCategory(IMember object) {
        try {
            return Flags.isStatic(object.getFlags()) ? STATIC : INSTANCE;
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

	public String getCategoryText(String category) {
		return category;
	}
}
