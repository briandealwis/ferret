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

import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.ui.WorkbenchAdapterLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class IOPFLabelProvider extends WorkbenchAdapterLabelProvider {
	
	public IOPFLabelProvider() {
		super();
	}

	@Override
	public String getText(Object element) {
		if(element instanceof ISphereFactory) {
			return ((ISphereFactory)element).getDescription();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if(element instanceof ImageDescriptor) {
			return registry.createImage((ImageDescriptor)element);
		}
		if(element instanceof ISphereFactory) {
			ImageDescriptor id = ((ISphereFactory)element).getImageDescriptor();
			if(id != null) { return registry.createImage(id); }
		}
		return super.getImage(element);
	}

}
