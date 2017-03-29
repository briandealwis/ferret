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
package ca.ubc.cs.ferret.types;

import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import java.util.ArrayList;
import java.util.Collection;

public class ConversionResult<T> {
	protected Fidelity fidelity;
	protected Collection<T> results = new ArrayList<T>();
	protected Class<T> clazz;
	
	public static <T> ConversionResult<T> forObject(T object) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ConversionResult<T> r = new ConversionResult(object.getClass(), Fidelity.Exact);
		r.addResult(object);
		return r;
	}

	public ConversionResult(Class<T> cls, Fidelity f) {
		clazz = cls;
		fidelity = f;
	}
	
	public ConversionResult(T adapted, Class<T> clz, Fidelity fidelity) {
		this(clz, fidelity);
		addResult(adapted);
	}

	public Fidelity getFidelity() {
		return fidelity;
	}
	
	public boolean wasSuccessful() {
		return !results.isEmpty();
	}

	public boolean hasSingleResult() {
		return results.size() == 1;
	}
	
	public Collection<T> getResults() {
		return results;
	}
	
	public T getSingleResult() {
		return results.iterator().next();
	}

	public Class<T> getResultClass() {
		return clazz;
	}
	
	public void addResult(T result) {
		assert clazz.isInstance(result);
		results.add(result);
	}
	
	public void addResults(Collection<T> r) {
		results.addAll(r);
	}

	public String toString() {
		return "ConversionResult: " + results.size() + " elmts of type=" + clazz.getName(); 
	}

	public void setFidelity(Fidelity resultingFidelity) {
		fidelity = resultingFidelity;
	}

}
