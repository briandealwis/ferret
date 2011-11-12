/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

public class WorkbenchLabel extends WorkbenchAdapter {
    protected String label;
    protected ImageDescriptor id;
    
    public WorkbenchLabel(String _label, ImageDescriptor _id) {
        label = _label;
        id = _id;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return id;
    }

    @Override
    public String getLabel(Object object) {
        return label;
    }

    public String toString() {
    	return label;
    }
}
