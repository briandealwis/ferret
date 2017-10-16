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
package ca.ubc.cs.ferret.model;

import ca.ubc.cs.ferret.types.FerretObject;
import java.util.LinkedList;

/**
 * An identity relation (one that maps any object to itself), optionally providing the object is of
 * the required type. 
 */
public class IdentityRelation extends AbstractToolRelation {
	protected Class<?> clazz;
	protected LinkedList<FerretObject> list;
	
	public IdentityRelation() {}
	
	public IdentityRelation(Class<?> cl) {
		clazz = cl;
	}

	@Override
	protected void init() {
		list = null;
		super.init();
	}

	@Override
	protected boolean configure(FerretObject... arguments) {
		list = new LinkedList<FerretObject>();
		for(FerretObject fo : arguments) {
			if(clazz == null || fo.getAdapter(clazz) != null) {
				list.add(fo);
			}
		}
		return !list.isEmpty();
	}

	public boolean hasNext() {
		return !list.isEmpty();
	}

	public FerretObject next() {
		return list.removeFirst();
	}

}
