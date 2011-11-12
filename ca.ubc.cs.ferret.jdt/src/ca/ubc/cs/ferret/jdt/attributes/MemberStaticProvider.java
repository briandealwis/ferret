/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.clustering.attrs.IClassifier;

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
