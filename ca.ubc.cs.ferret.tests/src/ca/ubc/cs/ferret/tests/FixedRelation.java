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
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * An test operator that always returns the values provided to its constructor. 
 */
public class FixedRelation extends AbstractToolRelation {
	protected Collection<FerretObject> collection;
	protected Iterator<FerretObject> iterator;
	
	public FixedRelation(Collection<FerretObject> values) {
		collection = new ArrayList<FerretObject>(values);
	}

	public FixedRelation(FerretObject... values) {
		collection = new ArrayList<FerretObject>(values.length);
		Collections.addAll(collection, values);
	}

	public FixedRelation(ISphere tb, Object... values) {
		this(FerretObject.wrap(values, Fidelity.Exact, tb));
	}

	@Override
	protected boolean configure(FerretObject... arguments) {
		iterator = collection.iterator(); 
		return !collection.isEmpty();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public FerretObject next() {
		return iterator.next();
	}

}
