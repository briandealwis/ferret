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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.hyades.models.trace.TRCMethod;

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class MethodArgumentsProvider extends
		AbstractClassifier<TRCMethod, Collection<String>> {

	public Collection<String>[] getCategories() {
		return null;
	}

	public Collection<String> getCategory(TRCMethod object) {
		Set<String> results = new HashSet<String>();
		String signature = object.getSignature();
		int lParentIndex = signature.indexOf('(');
		int rParenIndex = signature.lastIndexOf(')');
		if(rParenIndex < 0) { return null; }
		signature = signature.substring(lParentIndex + 1, rParenIndex);
		if(signature.length() == 0) {
			results.add("(none)");
			return results;
		}
		for(String arg : signature.split(",")) {
			results.add(arg.trim());
		}
		return results;
	}

}
