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

import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * IOperations represent the fundamental building blocks for the constituent
 * pieces of the Ferret query system.  They adhere to the Iterator interface;
 * IRelation objects only work once.
 * 
 * @author bsd
 *
 */
public interface IRelation extends Iterable<FerretObject>, Iterator<FerretObject> {
	
	/**
	 * @return the number of elements expected from this operation, or
	 *     -1 if unknown
	 */
	public int size();

	/**
     * Returns the next element in the iteration.  Calling this method
     * repeatedly until the {@link #hasNext()} method returns false will
     * return each element in the underlying collection exactly once.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
	 * @param <T> convert the value to type <T>  
	 */
	public <T> T next(Class<T> resultType);

	/**
     * Returns the next element in the iteration.  The element's fidelity
     * is placed in <code>resultFidelity</code>. Calling this method
     * repeatedly until the {@link #hasNext()} method returns false will
     * return each element in the underlying collection exactly once.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
	 * @param <T> convert the value to type <T>  
	 * @param resultFidelity the fidelity of the result  
	 */
	public <T> T next(Class<T> resultType, Collection<Fidelity> resultFidelity);
	
	public Collection<FerretObject> asCollection();
	public <T> Collection<T> asCollection(Class<T> resultType);
	public <T> Collection<T> asCollection(Class<T> resultType, Collection<Fidelity> fidelity);

}
