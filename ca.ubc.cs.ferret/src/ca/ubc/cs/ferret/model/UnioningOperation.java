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
import java.util.NoSuchElementException;

/**
 * Return the union of results from the constituent operations.
 * Repeats are not removed -- one possibility is to use the following:
 * <blockquote>
 * 	   results = new HashSet(op.asCollection());
 * </blockquote>
 */
public class UnioningOperation extends RelationalFunction {

	public UnioningOperation() {
		super();
	}

	public UnioningOperation(IRelation... ops) {
		super(ops);
	}

	public boolean hasNext() {
		while(!operations.isEmpty()) {
			if(operations.getFirst().hasNext()) { return true; }
			operations.removeFirst();
		}
		return false; 
	}

	public FerretObject next() {
		if(!hasNext()) { throw new NoSuchElementException(); }
		return operations.getFirst().next();
	}

}
