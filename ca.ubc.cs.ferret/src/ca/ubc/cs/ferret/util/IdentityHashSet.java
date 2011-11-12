/* copied from org.eclipse.hyades.models.util.internal.IdentityHashSet */
/**********************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 * Brian de Alwis - adapted to generics
 *
 * $Id: IdentityHashSet.java,v 1.1 2007/11/08 21:36:18 bsd Exp $
 **********************************************************************/
package ca.ubc.cs.ferret.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Marius Slavescu (slavescu@ca.ibm.com)
 * @since 4.2
 * 
 */
public class IdentityHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable {
	protected transient IdentityHashMap<E,Object> map;
	private static final Object PRESENT = new Object();

	public IdentityHashSet() {
		map = new IdentityHashMap<E, Object>();
	}

	public IdentityHashSet(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	/**
	 * Returns an iterator over the elements in this set. The elements are
	 * returned in no particular order.
	 * 
	 * @return an Iterator over the elements in this set.
	 * @see ConcurrentModificationException
	 */
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 * 
	 * @return the number of elements in this set (its cardinality).
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 * 
	 * @return <tt>true</tt> if this set contains no elements.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element.
	 * 
	 * @param o
	 *            element whose presence in this set is to be tested.
	 * @return <tt>true</tt> if this set contains the specified element.
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param o
	 *            element to be added to this set.
	 * @return <tt>true</tt> if the set did not already contain the
	 *         specified element.
	 */
	public boolean add(E o) {
		return map.put(o, PRESENT) == null;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 * 
	 * @param o
	 *            object to be removed from this set, if present.
	 * @return <tt>true</tt> if the set contained the specified element.
	 */
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	/**
	 * Removes all of the elements from this set.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <tt>HashSet</tt> instance: the
	 * elements themselves are not cloned.
	 * 
	 * @return a shallow copy of this set.
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			IdentityHashSet<E> newSet = (IdentityHashSet<E>) super.clone();
			newSet.map = (IdentityHashMap<E,Object>)map.clone();
			return newSet;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

}
