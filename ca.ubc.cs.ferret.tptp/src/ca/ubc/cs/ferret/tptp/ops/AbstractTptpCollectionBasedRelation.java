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
package ca.ubc.cs.ferret.tptp.ops;

import org.eclipse.hyades.models.trace.TRCLanguageElement;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.tptp.TptpSphereHelper;

public abstract class AbstractTptpCollectionBasedRelation<T extends TRCLanguageElement> extends
		AbstractCollectionBasedRelation<T> {

	public AbstractTptpCollectionBasedRelation() {}

	protected T checkInput(T input) {
		// re-resolve the instance in the TPTP sources defined for this sphere.
		return TptpSphereHelper.getDefault().reresolve(input, resolver.getSphere());
	}
}
