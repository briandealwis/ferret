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
package ca.ubc.cs.ferret.views;


import ca.ubc.cs.clustering.Cluster;
import ca.ubc.cs.ferret.display.IDisplayObject;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.util.SelectionUnwrapper;

public class ChildSelectionUnwrapper extends SelectionUnwrapper {

	public Object unwrapObject(Object element) {
    	if(element instanceof FerretObject) {	// FIXME: is this really the right thing to do?
    		return unwrapObject(((FerretObject)element).getPrimaryObject());
    	}
    	if(element instanceof Cluster<?>) {
    		return unwrapObject(((Cluster<?>)element).getIndex());
    	}
        if(element instanceof IDisplayObject) {
            return unwrapObject(((IDisplayObject)element).getObject());
        }
        return element;
	}

}
