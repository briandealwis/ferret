package ca.ubc.cs.ferret.ui;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import ca.ubc.cs.ferret.FerretPlugin;

public class ListModelContentProvider<T> implements IStructuredContentProvider, ILazyContentProvider,
		IModelListener<T >{
	protected ListModel<T> model;
	protected StructuredViewer viewer;
	
	public ListModelContentProvider() {
		super();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(viewer instanceof StructuredViewer) {
			this.viewer = (StructuredViewer)viewer;
		}
		if(model != null) {
			model.removeListener(this);
		}
		if(newInput instanceof ListModel<?>) {
			model = (ListModel<T>)newInput;
			model.addListener(this);
		}
	}

	public Object[] getElements(Object inputElement) {
		if(inputElement == model) {
			return model.getElements().toArray();
		}
		return null;
	}

	public void updateElement(int index) {
		if(viewer != null && model != null && index < model.size()) {
			if(viewer instanceof TableViewer) {
				((TableViewer)viewer).replace(model.get(index), index);
			}
		}
	}

	protected void refreshViewer() {
		FerretPlugin.getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}});
	}
	
	public void modelCleared(ListModel<T> model) {
		refreshViewer();
	}

	public void modelElementAdded(ListModel<T> model, T element) {
		refreshViewer();
	}

	public void modelElementRemoved(ListModel<T> model, T element) {
		refreshViewer();
	}

	public void modelElementsChanged(ListModel<T> model) {
		refreshViewer();
	}
}
