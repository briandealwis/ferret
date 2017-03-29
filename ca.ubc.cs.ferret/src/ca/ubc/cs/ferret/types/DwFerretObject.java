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
package ca.ubc.cs.ferret.types;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.display.DwObject;
import ca.ubc.cs.ferret.display.IPrettyPrinter;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import org.eclipse.jface.resource.ImageDescriptor;

public class DwFerretObject extends DwObject<FerretObject> {
	IPrettyPrinter printer;
	
	public DwFerretObject(FerretObject _object) {
		super(_object, _object.getSphere());
		printer = _object.getAdapter(IPrettyPrinter.class, Fidelity.Approximate);
	}

	@Override
	public ImageDescriptor getImage() {
		if(printer != null) { return printer.getImage(); }
		return FerretPlugin.getImage(object.getPrimaryObject());
	}

	@Override
	public String getText() {
		if(printer != null) { return printer.getText(); }
		return FerretPlugin.prettyPrint(object.getPrimaryObject());
	}
}
