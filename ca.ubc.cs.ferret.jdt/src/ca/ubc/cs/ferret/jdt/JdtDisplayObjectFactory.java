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
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.display.DwObject;
import ca.ubc.cs.ferret.display.IDisplayObject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;

public class JdtDisplayObjectFactory implements IAdapterFactory {

	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == IDisplayObject.class) {
			return adapterType.cast(new DwObject<Object>(adaptableObject));
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IJavaElement.class };
	}

}
