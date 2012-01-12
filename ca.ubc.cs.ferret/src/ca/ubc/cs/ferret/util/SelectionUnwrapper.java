package ca.ubc.cs.ferret.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public abstract class SelectionUnwrapper {
	/**
	 * Unwrap the provided selection.
	 * @param selection
	 */
	public abstract Object unwrapObject(Object element);

	/** Unwrap the objects in the provided selection into a new selection */
	public ISelection unwrapSelection(ISelection selection) {
		if(selection instanceof StructuredSelection) {
			StructuredSelection s = (StructuredSelection)selection;
			List<Object> unwrappedObjects = new ArrayList<Object>(s.size());
			boolean unwrappingOccurred = false;
			for(Iterator<?> iter = s.iterator(); iter.hasNext();) {
				Object element = iter.next();
				Object unwrapped = unwrapObject(element);
				unwrappedObjects.add(unwrapped);
				if(element != unwrapped) {
					unwrappingOccurred = true;
				}
			}
			if(unwrappingOccurred) {
				selection = new StructuredSelection(unwrappedObjects);
			}
		}
		return selection;
	}

}
