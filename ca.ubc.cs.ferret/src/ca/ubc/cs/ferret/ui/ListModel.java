package ca.ubc.cs.ferret.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.util.ListenerList;

public class ListModel<T> {
	protected List<T> elements;
	protected ListenerList listeners = new ListenerList();

	public ListModel() {
		clear();
	}

	public void clear() {
		elements = new ArrayList<T>(); 
		notifyModelCleared();
	}
	
	public T add(T element) {
		elements.add(element);
		notifyElementAdded(element);
		return element;
	}
	
	public boolean remove(T element) {
		boolean value = elements.remove(element);
		notifyElementRemoved(element);
		return value;
	}

	public void setElements(List<T> contents) {
		elements = new ArrayList<T>(contents);
		notifyElementsChanged();
	}

	public void setElements(T[] contents) {
		elements = new ArrayList<T>(contents.length);
		Collections.addAll(elements, contents);
		notifyElementsChanged();
	}

	public boolean contains(T element) {
		return elements.contains(element);
	}
	
	public List<T> getElements() {
		return elements;
	}
	
	public void addListener(IModelListener<T> l) {
		listeners.add(l);
	}

	public void removeListener(IModelListener<T> l) {
		listeners.remove(l);
	}


	protected void notifyModelCleared() {
		for(Object l : listeners.getListeners()) {
			((IModelListener<T>)l).modelCleared(this);
		}
	}

	protected void notifyElementsChanged() {
		for(Object l : listeners.getListeners()) {
			((IModelListener<T>)l).modelElementsChanged(this);
		}
	}

	protected void notifyElementAdded(T element) {
		for(Object l : listeners.getListeners()) {
			((IModelListener<T>)l).modelElementAdded(this, element);
		}
	}

	protected void notifyElementRemoved(T element) {
		for(Object l : listeners.getListeners()) {
			((IModelListener<T>)l).modelElementRemoved(this, element);
		}
	}

	public int size() {
		return elements.size();
	}

	public T get(int index) {
		return elements.get(index);
	}

	public T last() {
		if(elements.size() == 0) { return null; }
		return elements.get(elements.size() - 1);
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

}
