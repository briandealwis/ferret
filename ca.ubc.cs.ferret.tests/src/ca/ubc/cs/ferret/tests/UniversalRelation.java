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
package ca.ubc.cs.ferret.tests;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.FerretObject;

/**
 * The universal relation maps each element to itself.
 */
public class UniversalRelation extends AbstractToolRelation {

	protected FerretObject object;
	protected boolean retrieved = false;
	
	public UniversalRelation() {}

	public boolean hasNext() {
		return !retrieved;
	}

	public FerretObject next() {
		if(retrieved) { return null; }
		retrieved = true;
		return object;
	}

	protected boolean configure(FerretObject... arguments) {
		if(arguments.length != 1) { return false; }
		object = arguments[0];
		return true;
	}

}
