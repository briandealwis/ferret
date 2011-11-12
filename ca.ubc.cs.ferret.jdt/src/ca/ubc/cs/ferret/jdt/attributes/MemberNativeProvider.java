/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.clustering.attrs.IClassifier;

/**
 * Extract the is-native attribute of the provided object.  Although flags are available
 * from any object, is-native is really only applicable to methods.
 * @author bsd
 */
public class MemberNativeProvider extends AbstractJavaAttributeValue<IMember,String> {

	protected static final String NATIVE = "native";
	protected static final String NONNATIVE = "non-native";

	public MemberNativeProvider() {
    }

    public String[] getCategories() {
    	return new String[] { NATIVE, NONNATIVE };
    }

    public String getCategory(IMember object) {
        try {
            return Flags.isNative(object.getFlags()) ? NATIVE : NONNATIVE;
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

	public String getCategoryText(String category) {
		return category;
	}
}
