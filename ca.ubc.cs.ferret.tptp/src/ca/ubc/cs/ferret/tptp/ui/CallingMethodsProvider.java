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
package ca.ubc.cs.ferret.tptp.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class CallingMethodsProvider implements ITreeContentProvider {
	protected TreeViewer viewer;
	protected Object root = null;
	
	public CallingMethodsProvider(TreeViewer _viewer) {
		viewer = _viewer;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		root = newInput;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement == root && root instanceof Object[]) {
			return (Object[])root;
		}
		return null;
	}

	public Object getParent(Object element) {
		return root;
	}

	public boolean hasChildren(Object element) {
		return true;
	}

}
