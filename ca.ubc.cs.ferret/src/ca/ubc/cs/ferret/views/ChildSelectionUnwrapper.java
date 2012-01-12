/*
 * Copyright 2005 by X.
 * @author bsd
 */
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
