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
package ca.ubc.cs.ferret.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.ListenerList;

/**
 * This is a model of a list of items, bifurcated into those that have been selected,
 * and those that are not.
 * @param <T> the type of items
 * @param <L> the type of listeners
 */
public abstract class AbstractItemSelectionModel<T,L> {
	public AbstractItemSelectionModel() {}
	protected List<T> unselected = new ArrayList<T>();
	protected List<T> selected = new ArrayList<T>();
	protected ListenerList<L> listeners = new ListenerList<>();

	abstract protected void notifyListener(L listener);

	/**
	 * Does no checking that <code>f</code> hasn't already been added.
	 * @param item
	 */
	public void add(T item) {
		unselected.add(item);
	}

	public boolean isSelected(T item) {
		return selected.contains(item);
	}

	public boolean remove(T item) {
		boolean wasInUnselected = false;
		wasInUnselected = unselected.remove(item);
		return selected.remove(item) || wasInUnselected;
	}

	public void select(T item) {
		if(unselected.remove(item)) {
			insertSelected(item);
			notifyListeners();
		}
	}

	/**
	 * Insert the selected item into the <code>selected</code> list. 
	 * Intended to be overridden by subclasses.
	 * @param item item to be inserted into the <code>selected</code> list
	 */
	protected void insertSelected(T item) {
		selected.add(item);
	}

	/**
	 * Insert the provided item into the <code>unselected</code> list. 
	 * Intended to be overridden by subclasses.
	 * @param item to be inserted into the <code>unselected</code> list
	 */
	protected void insertUnselected(T item) {
		unselected.add(item);
	}

	public void unselect(T item) {
		if(selected.remove(item)) {
			insertUnselected(item);
			notifyListeners();
		}
	}

	public void selectAll() {
		if(unselected.isEmpty()) { return; }
		for(Iterator<T> it = unselected.iterator(); it.hasNext();) {
			selected.add(it.next());
			it.remove();
		}
		notifyListeners();
	}

	public void unselectAll() {
		if(selected.isEmpty()) { return; }
		for(Iterator<T> it = selected.iterator(); it.hasNext();) {
			unselected.add(it.next());
			it.remove();
		}
		notifyListeners();
	}

	public List<T> getSelected() {
		return selected;
	}

	public Collection<T> getUnselected() {
		return unselected;
	}

	public void moveUp(T item) {
		int index = selected.indexOf(item);
		if(index <= 0) { return; }
		T temp = selected.get(index - 1);
		selected.set(index - 1, item);
		selected.set(index, temp);
		notifyListeners();
	}

	public void moveDown(T item) {
		int index = selected.indexOf(item);
		if(index < 0 || index >= selected.size()) { return; }
		T temp = selected.get(index + 1);
		selected.set(index + 1, item);
		selected.set(index, temp);
		notifyListeners();
	}

	public void addListener(L listener) {
		listeners.add(listener);
	}
	
	public void removeListener(L listener) {
		listeners.remove(listener);
	}
	
	protected void notifyListeners() {
		for(Object listener : listeners.getListeners()) {
			notifyListener((L)listener);
		}
	}
}
