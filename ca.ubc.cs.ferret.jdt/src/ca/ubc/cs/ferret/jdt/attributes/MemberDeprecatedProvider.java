/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.clustering.attrs.IClassifier;

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
