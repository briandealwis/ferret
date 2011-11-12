/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

public class MemberSynchronizedProvider extends AbstractJavaAttributeValue<IMember,String> {

	protected static final String SYNCHRONIZED = "synchronized";
	protected static final String NONSYNCHRONIZED = "non-synchronized";

    public MemberSynchronizedProvider() {
    }

    public String[] getCategories() { return new String[] { SYNCHRONIZED, NONSYNCHRONIZED }; }

    public String getCategory(IMember object) {
        try {
            return Flags.isSynchronized(object.getFlags()) ? SYNCHRONIZED : NONSYNCHRONIZED;
        } catch(JavaModelException e) {
            log(e);
            return null;
        }
    }

	public String getCategoryText(String category) {
		return category;
	}
}
