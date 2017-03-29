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

import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractRelation implements IRelation {

	public int size() {
		return -1;
	}

	public Iterator<FerretObject> iterator() {
		return this;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return hasNext();
	}

	public Collection<FerretObject> asCollection() {
		Collection<FerretObject> c = new LinkedList<FerretObject>();
		for(FerretObject fo : this) {
			c.add(fo);
		}
		return c;
	}

	/**
	 * Return the results as a collection; provide the overall assessed-fidelity of the results
	 * in <code>assessedFidelity</code> (can be null).
	 */
	public <T> Collection<T> asCollection(Class<T> resultType,
			Collection<Fidelity> assessedFidelity) {
		Collection<T> c = new LinkedList<T>();
		Fidelity assessed = Fidelity.Exact;
		for(FerretObject fo : this) {
			ConversionResult<T> result = fo.convert(resultType, 1, Fidelity.Approximate);
			if(result == null) { continue; }
			assessed = assessed.least(result.getFidelity());
			c.addAll(result.getResults());
		}
		if(assessedFidelity != null) { 
			assessedFidelity.clear();
			assessedFidelity.add(assessed);
		}
		return c;
	}

	public <T> Collection<T> asCollection(Class<T> resultType) {
		return asCollection(resultType, null);
	}

	public <T> T next(Class<T> resultType, Collection<Fidelity> resultFidelity) {
		FerretObject fo = next();
		ConversionResult<T> result = fo.convert(resultType, 1, Fidelity.Approximate);
		if(resultFidelity != null) {
			resultFidelity.clear();
			resultFidelity.add(result.getFidelity());
		}
		return result.getSingleResult();
	}
	
	public <T> T next(Class<T> resultType) {
		return next(resultType, null);
	}
}
