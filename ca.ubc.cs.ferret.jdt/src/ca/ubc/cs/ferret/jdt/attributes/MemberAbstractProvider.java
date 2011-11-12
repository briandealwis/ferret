/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.clustering.attrs.IClassifier;

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
