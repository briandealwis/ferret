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
package ca.ubc.cs.ferret.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public abstract class SelectionUnwrapper {
	/**
	 * Unwrap the provided selection.
	 * @param selection
	 */
	public abstract Object unwrapObject(Object element);

	/** Unwrap the objects in the provided selection into a new selection */
	public ISelection unwrapSelection(ISelection selection) {
		if(selection instanceof StructuredSelection) {
			StructuredSelection s = (StructuredSelection)selection;
			List<Object> unwrappedObjects = new ArrayList<Object>(s.size());
			boolean unwrappingOccurred = false;
			for(Iterator<?> iter = s.iterator(); iter.hasNext();) {
				Object element = iter.next();
				Object unwrapped = unwrapObject(element);
				unwrappedObjects.add(unwrapped);
				if(element != unwrapped) {
					unwrappingOccurred = true;
				}
			}
			if(unwrappingOccurred) {
				selection = new StructuredSelection(unwrappedObjects);
			}
		}
		return selection;
	}

}
