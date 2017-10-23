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
package ca.ubc.cs.ferret.pde;

import java.io.File;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.ui.IStorageEditorInput;

/**
 * Adapts jar files to an IPackageFragmentRoot via the PDE search management.
 */
public class JarFileAdapterFactory implements IAdapterFactory {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof IStorageEditorInput) {
			IStorageEditorInput input = (IStorageEditorInput) adaptableObject;
			File file = input.getAdapter(File.class);
			if (file != null) {
				Object adapted = PDECore.getDefault().getSearchablePluginsManager().createAdapterChild(null, file);
				if (adapterType.isInstance(adapted)) {
					return adapterType.cast(adapted);
				}
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IJavaElement.class, IPackageFragmentRoot.class };
	}

}
