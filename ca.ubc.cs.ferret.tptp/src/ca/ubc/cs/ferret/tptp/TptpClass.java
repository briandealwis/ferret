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
package ca.ubc.cs.ferret.tptp;

import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.display.IPrettyPrinter;
import ca.ubc.cs.ferret.types.IEquivalenceTesting;

public class TptpClass implements IEquivalenceTesting, IPrettyPrinter {
	protected String packageName;
	protected String className;
	protected TRCClass exemplar;

	public TptpClass(TRCClass cl) {
		packageName = cl.getPackage().getName();
		className = cl.getName();
		exemplar = cl;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TptpClass
				&& packageName.equals(((TptpClass) obj).packageName)
				&& className.equals(((TptpClass) obj).className);
	}

	@Override
	public int hashCode() {
		return 37 * className.hashCode() + packageName.hashCode();
	}

	public String toString() {
		return getClass().getSimpleName() + "{" + getText() + "}";
	}

	public ImageDescriptor getImage() {
		return null;
	}

	public String getText() {
		return className + " - " + packageName;
	}
	
	public TRCClass getExemplar() {
		return exemplar;
	}

}
