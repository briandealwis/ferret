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
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;

public class MemberAbstractProvider
		extends AbstractJavaAttributeValue<IMember,String> {

	protected final static String ABSTRACT = "abstract";
	protected final static String CONCRETE = "concrete"; 
	
    public MemberAbstractProvider() {
    }

    public String[] getCategories() { return new String[] { ABSTRACT, CONCRETE }; }

    public String getCategory(IMember object) {
        try {
            return Flags.isAbstract(((IMember)object).getFlags()) ? ABSTRACT : CONCRETE;
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

	@SuppressWarnings("restriction")
	public ImageDescriptor getCategoryImage(String category) {
		return category == ABSTRACT ? JavaPluginImages.DESC_OVR_ABSTRACT : null;
	}

	public String getCategoryText(String category) {
		return category;
	}
}
