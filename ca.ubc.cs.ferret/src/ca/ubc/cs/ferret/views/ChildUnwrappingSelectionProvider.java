/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.views;


import org.eclipse.jface.viewers.ISelectionProvider;

import ca.ubc.cs.clustering.Cluster;
import ca.ubc.cs.ferret.display.IDisplayObject;
import ca.ubc.cs.ferret.types.FerretObject;

public class ChildUnwrappingSelectionProvider extends
        UnwrappingSelectionProvider {

    public ChildUnwrappingSelectionProvider(ISelectionProvider _wrappedProvider) {
        super(_wrappedProvider);
    }

    protected Object unwrapObject(Object element) {
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
