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

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContentProviderWrapper implements ITreeContentProvider {
	protected ITreeContentProvider wrapped;
	
	public ContentProviderWrapper(ITreeContentProvider toBeWrapped) {
		wrapped = toBeWrapped;
	}

	public void dispose() {
		wrapped.dispose();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		wrapped.inputChanged(viewer, oldInput, newInput);
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Object[]) {
			return (Object[])parentElement;
		} else if(parentElement instanceof Collection) {
			return ((Collection<?>)parentElement).toArray();
		}
		return wrapped.getChildren(parentElement);
	}

	public Object getParent(Object element) {
		return wrapped.getParent(element);
	}

	public boolean hasChildren(Object element) {
		return wrapped.hasChildren(element);
	}

}
