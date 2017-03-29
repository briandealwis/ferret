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

public class NullObjectMapper implements IObjectMapper {
	public static final String HANDLE_TYPE_UNKNOWN = "unknown";

	public String[] describe(Object object) {
		return new String[] { HANDLE_TYPE_UNKNOWN,
				object.toString() };
	}

	public String[] getHandleTypes() {
		return new String[] { HANDLE_TYPE_UNKNOWN };
	}

	public Object resolve(String handleType, String description) {
		return description;
	}

}
