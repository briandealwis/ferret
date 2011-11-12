/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public abstract class UnwrappingSelectionProvider implements ISelectionProvider, ISelectionChangedListener {
    protected ISelectionProvider wrappedProvider;
    protected ListenerList selectionChangedList = new ListenerList();
    protected ISelection current = null;
    protected ISelection wrappedSelection = null;
    
    public UnwrappingSelectionProvider(ISelectionProvider _wrappedProvider) {
        wrappedProvider = _wrappedProvider;
    }

    public void enableSelectionChangedNotification() {
        if(wrappedProvider instanceof IPostSelectionProvider) {
            ((IPostSelectionProvider)wrappedProvider).addPostSelectionChangedListener(this);
        } else {
            wrappedProvider.addSelectionChangedListener(this);
        }
    }
    
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
//        System.out.println("USP: addSelectionChangedListener: " + listener);
        selectionChangedList.add(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
//        System.out.println("USP: removeSelectionChangedListener: " + listener);
        selectionChangedList.remove(listener);
    }

    public  ISelection getSelection() {
        ISelection newWrappedSelection = wrappedProvider.getSelection();
        if(newWrappedSelection.equals(wrappedSelection)) { return current; }
        wrappedSelection = newWrappedSelection;
        return current = unwrapSelection(wrappedSelection);
//        System.out.println("UnwrappingSelectionProvider: getSelection() = {" + FerretPlugin.debugPrint(current) + "}");
//        return current;
    }
        
    public void setSelection(ISelection selection) {
//        System.out.println("USP: setSelection: " + FerretPlugin.debugPrint(selection));
        // Ignore this for now: are we supposed to notify the wrappedProvider?
        // How do we figure out how to wrap the elements?
        // notifySelectionChanged();
    }

    public void selectionChanged(SelectionChangedEvent event) {
        // Sadly, this method is pretty useless, in that it often comes too late.
//        System.out.println("USP: selectionChanged: source=" + event.getSource() +
//                " selection=" + FerretPlugin.debugPrint(event.getSelection()));

//        ISelection newSelection = unwrapSelection(event.getSelection());
        notifySelectionChanged();
    }

    protected void notifySelectionChanged() {
        Object listeners[] = selectionChangedList.getListeners();
        SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
        for(int i = 0; i < listeners.length; i++) {
            ((ISelectionChangedListener)listeners[i]).selectionChanged(event);
        }
    }

    /**
     * Unwrap the provided selection.
     * @param selection
     */
    protected abstract Object unwrapObject(Object element);

	protected ISelection unwrapSelection(ISelection selection) {
	    if(selection instanceof StructuredSelection) {
	        StructuredSelection s = (StructuredSelection)selection;
	        List<Object> unwrappedObjects = new ArrayList<Object>(s.size());
	        boolean unwrappingOccurred = false;
	        for(Iterator iter = s.iterator(); iter.hasNext();) {
	            Object element = iter.next();
	            Object unwrapped = unwrapObject(element);
	            unwrappedObjects.add(unwrapped);
	            if(element != unwrapped) { unwrappingOccurred = true; }
	        }
	        if(unwrappingOccurred) {
	            selection = new StructuredSelection(unwrappedObjects);
	        }
	    }
	    return selection;
	}
}
