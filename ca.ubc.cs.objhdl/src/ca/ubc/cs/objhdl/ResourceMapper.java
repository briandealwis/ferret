/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
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
