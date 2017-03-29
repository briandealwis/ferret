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
package ca.ubc.cs.ferret.pde.classifiers;

import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;

public class PluginObjectClassifier implements IClassifier<IPluginObject,IPluginModelBase>{

	public PluginObjectClassifier() {}

	public IPluginModelBase[] getCategories() {
		return null;
	}

	public IPluginModelBase getCategory(IPluginObject object) {
		return PdeModelHelper.getDefault().getDefiningModel(object);
	}

	public String getCategoryText(IPluginModelBase category) {
		return FerretPlugin.compactPrettyPrint(category);
	}

	public ImageDescriptor getCategoryImage(IPluginModelBase category) {
		return FerretPlugin.getImage(category);
	}
}
