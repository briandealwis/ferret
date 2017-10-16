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

import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.display.IPrettyPrinter;
import ca.ubc.cs.ferret.types.IEquivalenceTesting;

/**
 * A wrapper for TRCMethods
 */
public class TptpMethod implements IEquivalenceTesting, IPrettyPrinter {
	protected String packageName;
	protected String className;
	protected String methodName;
	protected String methodSignature;
	protected TRCMethod exemplar;
	
	public TptpMethod(TRCMethod m) {
		packageName = m.getDefiningClass().getPackage().getName();
		className = m.getDefiningClass().getName();
		methodName = m.getName();
		methodSignature = m.getSignature();
		exemplar = m;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof TptpMethod
				&& packageName.equals(((TptpMethod) obj).packageName)
				&& className.equals(((TptpMethod) obj).className)
				&& methodName.equals(((TptpMethod) obj).methodName)
				&& methodSignature.equals(((TptpMethod) obj).methodSignature);
	}

	@Override
	public int hashCode() {
		return 257 * methodSignature.hashCode() + 91 * methodName.hashCode()
				+ 37 * className.hashCode() + packageName.hashCode();
	}

	public String toString() {
		return getClass().getSimpleName() + "{" + getText() + "}";
	}
	
	public ImageDescriptor getImage() {
		return null;
	}

	public String getText() {
		int start = methodSignature.indexOf('(');
		int stop = methodSignature.indexOf(')');		
		boolean hasArgs = stop - start > 1;

		return className + "." + methodName + (hasArgs ? "(...)" : "()");
	}

	public TRCMethod getExemplar() {
		return exemplar;
	}

}
