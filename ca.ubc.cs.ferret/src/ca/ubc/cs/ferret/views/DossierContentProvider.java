/*
 * Copyright Sep 23, 2004  X
 * @author 2004
 */
package ca.ubc.cs.ferret.views;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.ICallback;
import ca.ubc.cs.ferret.display.DwConsultation;
import ca.ubc.cs.ferret.display.IDisplayObject;
import ca.ubc.cs.ferret.model.Consultation;

public class DossierContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	protected StructuredViewer viewer;
	protected IDisplayObject modelRoot;
	
    private ICallback<IDisplayObject> refreshCallback = new ICallback<IDisplayObject>() {
            public void run(final IDisplayObject argument) {
                viewer.getControl().getDisplay().asyncExec(new Runnable() {
                    public void run() { 
                        if(viewer == null) { return; }
                        viewer.refresh(argument == modelRoot ?
                                modelRoot.getObject() : argument, true);
                    }});
                }};

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		viewer = (StructuredViewer) v;
//		System.out.println("ViewContentProvider: inputChanged: ");
//		System.out.println("oldInput=" + oldInput);
//		System.out.println("newInput=" + newInput);
		if(modelRoot != null && modelRoot instanceof IDisplayObject) {
            modelRoot.removeRefreshCallback(refreshCallback);
		    modelRoot.dispose();      
        }
        if(oldInput != null && oldInput instanceof Consultation && oldInput != newInput) {
            ((Consultation)oldInput).cancel();
        }
        if(newInput instanceof Consultation) {
            modelRoot = new DwConsultation((Consultation)newInput);
            ((DwConsultation)modelRoot).registerRefresh();
        } else if(newInput == null || newInput instanceof IDisplayObject) {
            modelRoot = (IDisplayObject)newInput;
        } else {
            throw new IllegalArgumentException("invalid input type: " + newInput);
        }
        if(modelRoot !=  null) {
            modelRoot.addRefreshCallback(refreshCallback);
        }
	}

	public void dispose() {
        if(modelRoot == null) { return; }
        modelRoot.dispose();      
        modelRoot = null;
	}

	public Object[] getElements(Object parent) {
		if(modelRoot == null) { return new Object[0]; }
		return getChildren(parent);
	}

	public Object getParent(Object child) {
        if (child instanceof IDisplayObject) { return ((IDisplayObject)child).getParent(); }
		return null;
	}

	public Object[] getChildren(Object object) {
	    if(FerretPlugin.hasDebugOption("debug/showGetChildren")) {
	        System.out.println("getChildren(<" + object.getClass() + "> " + object + ")");
        }
        if(modelRoot.getObject() == object) {
            object = modelRoot;
        }
        if(object instanceof IDisplayObject) {
            return ((IDisplayObject)object).getChildren();
        }
        return new Object[0];
	}

    protected Object singleElement(Collection/*<?E>*/ collection) {
        if(collection.size() != 1) {
            throw new IllegalStateException("Collection must contain only a single element"); 
        }
        return collection.toArray()[0];
    }

	public boolean hasChildren(Object parent) {
        if(parent instanceof IDisplayObject) { return ((IDisplayObject)parent).hasChildren(); }
		return getChildren(parent).length > 0;
	}
}