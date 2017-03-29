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

@SuppressWarnings("restriction")
public class MemberAccessProtectectionProvider
		extends AbstractJavaAttributeValue<IMember,String> {

	protected static final String PUBLIC = "public";
	protected static final String PROTECTED = "protected";
	protected static final String PACKAGE = "package";
	protected static final String PRIVATE = "private";
	
    public MemberAccessProtectectionProvider() {
    }

    public String[] getCategories() {
        return new String[] { PUBLIC, PROTECTED, PACKAGE, PRIVATE };
    }

    public String getCategory(IMember object) {
        try {
            int flags = object.getFlags();
            if(Flags.isPublic(flags)) { return PUBLIC; }
            if(Flags.isProtected(flags)) { return PROTECTED; }
            if(Flags.isPrivate(flags)) { return PRIVATE; }
            return PACKAGE;   // default: package protection
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

	public ImageDescriptor getCategoryImage(String category) {
		if(category == PUBLIC) {
			return JavaPluginImages.DESC_MISC_PRIVATE;
		} else if(category == PROTECTED) {
			return JavaPluginImages.DESC_MISC_PROTECTED;
		} else if(category == PACKAGE) {
			return JavaPluginImages.DESC_MISC_DEFAULT;
		} else if(category == PRIVATE) {
			return JavaPluginImages.DESC_MISC_PRIVATE;
		}
		return null;
	}

	public String getCategoryText(String category) {
		return category;
	}
}
