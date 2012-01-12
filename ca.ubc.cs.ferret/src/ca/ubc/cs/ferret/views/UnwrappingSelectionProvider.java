/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.views;

import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import ca.ubc.cs.ferret.util.SelectionUnwrapper;

public class UnwrappingSelectionProvider implements ISelectionProvider,
		ISelectionChangedListener {
    protected ISelectionProvider wrappedProvider;
	protected SelectionUnwrapper unwrapper;
    protected ListenerList selectionChangedList = new ListenerList();
    protected ISelection current = null;
    protected ISelection wrappedSelection = null;
    
	public UnwrappingSelectionProvider(ISelectionProvider wrappedProvider,
			SelectionUnwrapper unwrapper) {
		this.wrappedProvider = wrappedProvider;
		this.unwrapper = unwrapper;
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
		return current = unwrapper.unwrapSelection(wrappedSelection);
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
}
