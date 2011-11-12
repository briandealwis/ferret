package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;

public class FieldTypeProvider extends AbstractJavaAttributeValue<IField,Object> {

	public FieldTypeProvider() {
	}

	public Object[] getCategories() {
		return null;
	}

	public Object getCategory(IField field) {
		try {
			IType type = JavaModelHelper.getDefault().resolveSignature(field.getTypeSignature(), field);
			return type != null ? type : "(none)";
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return "(unknown)";
		}
	}

}
