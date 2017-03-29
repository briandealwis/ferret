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
package ca.ubc.cs.ferret.sphereconfig;

import ca.ubc.cs.ferret.model.ISphereCompositorFactory;
import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SphereNetworkContentProvider implements ITreeContentProvider {
	protected Object root;
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		root = newInput;
	}

	public boolean hasChildren(Object parent) {
		Object children[] = getChildren(parent);
		return children != null && children.length > 0;
	}

	public Object[] getChildren(Object parent) {
		if(parent instanceof Collection) {
			return ((Collection<?>)parent).toArray();
		} else if(parent instanceof ISphereCompositorFactory) {
			return ((ISphereCompositorFactory)parent).getComposedSphereFactories().toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void dispose() {
	}

}
