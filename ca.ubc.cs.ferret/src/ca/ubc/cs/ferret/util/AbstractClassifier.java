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

import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.FerretPlugin;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class AbstractClassifier<T,C> implements IClassifier<T,C> {

	public String getCategoryText(C category) {
		return FerretPlugin.compactPrettyPrint(category);
	}

	public ImageDescriptor getCategoryImage(C category) {
		return FerretPlugin.getImage(category);
	}
}
