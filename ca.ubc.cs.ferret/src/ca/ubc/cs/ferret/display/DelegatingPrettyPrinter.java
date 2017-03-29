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
package ca.ubc.cs.ferret.display;

import ca.ubc.cs.ferret.FerretPlugin;
import org.eclipse.jface.resource.ImageDescriptor;

public class DelegatingPrettyPrinter implements IPrettyPrinter {
	protected Object object;
	
	public DelegatingPrettyPrinter(Object o) {
		object = o;
	}

	public ImageDescriptor getImage() {
		return FerretPlugin.getImage(object);
	}

	public String getText() {
		return FerretPlugin.prettyPrint(object);
	}
}
