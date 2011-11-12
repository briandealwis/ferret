package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;

public class ClassSuperclassProvider extends AbstractJavaAttributeValue<IType, Object> {

	public ClassSuperclassProvider() {	}

	public Object[] getCategories() {
		return null;
	}

	public Object getCategory(IType clazz) {
		try {
			if(clazz.isClass()) {
				IType sup = JavaModelHelper.getDefault().getSuperclass(clazz, new NullProgressMonitor());
				return sup != null ? sup : "(none)";
			}
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return "(unknown)";
		}
		return "(non-class)";
	}
}
