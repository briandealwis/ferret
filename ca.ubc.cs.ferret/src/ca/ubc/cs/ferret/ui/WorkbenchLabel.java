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
