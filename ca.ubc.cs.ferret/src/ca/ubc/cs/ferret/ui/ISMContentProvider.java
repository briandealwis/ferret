package ca.ubc.cs.ferret.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ISMContentProvider implements IStructuredContentProvider {
	public static final int SELECTED = 0;
	public static final int UNSELECTED = 1;
	
	protected AbstractItemSelectionModel<?,?> model;
	protected int chooseSelected;
	
	public ISMContentProvider(int chooseSelectedItems) {
		chooseSelected = chooseSelectedItems;
	}
	
	public Object[] getElements(Object inputElement) {
		return chooseSelected == SELECTED
				? model.getSelected().toArray()
				: model.getUnselected().toArray();
	}

	public void dispose() {
		model = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof AbstractItemSelectionModel) {
			model = (AbstractItemSelectionModel<?,?>)newInput;
		} else {
			model = null;
		}
	}

}
