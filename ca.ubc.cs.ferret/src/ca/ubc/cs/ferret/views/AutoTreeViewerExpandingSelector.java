/*
 * Copyright 2004  X
 * @author bsd
 */
package ca.ubc.cs.ferret.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * @author bsd
 */
public class AutoTreeViewerExpandingSelector implements
        ISelectionChangedListener {

    public AutoTreeViewerExpandingSelector() {
        super();
    }

    /*
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     *          @author bsd
     * @since X
     */
    public void selectionChanged(SelectionChangedEvent event) {
        if (!(event.getSelection() instanceof IStructuredSelection)) {
            return;
        }
        if (!(event.getSelectionProvider() instanceof TreeViewer)) {
            return; // should possibly be an assertion
        }
        IStructuredSelection sel = (IStructuredSelection) event.getSelection();
        TreeViewer viewer = (TreeViewer) event.getSelectionProvider();
        // FIXME: if some items had been previously auto-expanded,
        // unexpand them to their previous setting.
        if (sel.size() == 1) {
            viewer.expandToLevel(sel.getFirstElement(), 1);
        }
    }

}
