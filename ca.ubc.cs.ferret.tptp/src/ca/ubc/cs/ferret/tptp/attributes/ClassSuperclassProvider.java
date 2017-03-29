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

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class ClassSuperclassProvider extends
		AbstractClassifier<TRCClass, TRCClass> {

	public TRCClass[] getCategories() {
		return null;
	}

	public TRCClass getCategory(TRCClass object) {
		for(Object o : object.getExtends()) {
			if(!((TRCClass)o).isInterface()) { return (TRCClass)o; }
		}
		return null;
	}

}
