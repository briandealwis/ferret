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

import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCLanguageElement;
import org.eclipse.hyades.models.trace.TRCMethod;

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class DeclaringTypeClassifier extends
		AbstractClassifier<TRCLanguageElement, TRCClass> {

	public TRCClass[] getCategories() {
		return null;
	}

	public TRCClass getCategory(TRCLanguageElement object) {
		if(object instanceof TRCMethod) {
			return ((TRCMethod)object).getDefiningClass();
		}
		if (object instanceof TRCClass) {
			return ((TRCClass)object).getEnclosedBy();
		}
		return null;
	}

}
