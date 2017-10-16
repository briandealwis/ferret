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
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Absorb all and release nothing.  Useful for testing, especially NamedJoinRelation's hasNext().
 */
public class NullRelation extends AbstractToolRelation {

	public NullRelation() {}

	public boolean hasNext() {
		return false;
	}

	public FerretObject next() {
		throw new NoSuchElementException();
	}

	public IRelation configure(IProgressMonitor monitor,
			ISphere sphere, FerretObject... arguments) {
		return this;
	}
	
	protected boolean configure(FerretObject... arguments) {
		return true;
	}

}
