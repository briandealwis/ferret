package ca.ubc.cs.ferret.sphereconfig;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.ubc.cs.ferret.model.ISphereCompositor;
import ca.ubc.cs.ferret.model.ISphereCompositorFactory;

public class SphereNetworkContentProvider implements ITreeContentProvider {
	protected Object root;
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		root = newInput;
	}

	public boolean hasChildren(Object parent) {
		Object children[] = getChildren(parent);
		return children != null && children.length > 0;
	}

	public Object[] getChildren(Object parent) {
		if(parent instanceof Collection) {
			return ((Collection<?>)parent).toArray();
		} else if(parent instanceof ISphereCompositorFactory) {
			return ((ISphereCompositorFactory)parent).getComposedSphereFactories().toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void dispose() {
	}

}