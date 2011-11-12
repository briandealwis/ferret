package ca.ubc.cs.objhdl;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

public class JdtMapper implements IObjectMapper {
	public static final String HANDLE_TYPE_JAVA = "java";

	public String[] getHandleTypes() {
		return new String[] { HANDLE_TYPE_JAVA };
	}

	public String[] describe(Object object) {
		if(object instanceof IJavaElement) {
			return new String[] { HANDLE_TYPE_JAVA,
					((IJavaElement) object).getHandleIdentifier() };
		}
		return null;
	}

	public Object resolve(String handleType, String description) {
		return HANDLE_TYPE_JAVA.equals(handleType) ? JavaCore.create(description) : null;
	}

}
