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
package ca.ubc.cs.ferret.tptp.attributes;

import org.eclipse.hyades.models.trace.TRCMethod;

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class MethodReturnTypeProvider extends
		AbstractClassifier<TRCMethod, String> {

	public String[] getCategories() {
		return null;
	}

	public String getCategory(TRCMethod object) {
		String signature = object.getSignature();
		int rParenIndex = signature.lastIndexOf(')') + 1;
		signature = signature.substring(rParenIndex).trim(); 
		return signature.length() == 0 ? "(constructor)" : signature;
	}
	
}
