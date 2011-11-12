package ca.ubc.cs.objhdl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

public class ResourceMapper implements IObjectMapper {
	public static final String HANDLE_TYPE_RESOURCE = "resource";

	public String[] getHandleTypes() {
		return new String[] { HANDLE_TYPE_RESOURCE };
	}

	public String[] describe(Object object) {
		if(object instanceof IResource) {
			// FIXME: I'm sure we can do better, like a project-specific mapping,
			// but this will do for now
			return new String[] { HANDLE_TYPE_RESOURCE,
					((IResource)object).getFullPath().toPortableString() };
		}
		return null;
	}

	public Object resolve(String handleType, String description) {
		if(HANDLE_TYPE_RESOURCE.equals(description)) {
			return ResourcesPlugin.getWorkspace().getRoot().findMember(description);
		}
		return null;
	}

}
