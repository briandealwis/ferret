/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.clustering.attrs.IClassifier;

public class MemberFinalProvider extends AbstractJavaAttributeValue<IMember,String> {
	
	protected static final String FINAL = "final";
	protected static final String OVERRIDABLE = "overridable";
	
    public MemberFinalProvider() {
    }

    public String[] getCategories() { 
    	return new String[] { FINAL, OVERRIDABLE };
	}

    public String getCategory(IMember object) {
        try {
            return Flags.isFinal(object.getFlags()) ? FINAL : OVERRIDABLE;
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

	public String getCategoryText(String category) {
		return category;
	}
}
