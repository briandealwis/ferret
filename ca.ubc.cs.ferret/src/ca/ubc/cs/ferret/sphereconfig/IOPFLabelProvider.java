package ca.ubc.cs.ferret.sphereconfig;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.ui.WorkbenchAdapterLabelProvider;

public class IOPFLabelProvider extends WorkbenchAdapterLabelProvider {
	
	public IOPFLabelProvider() {
		super();
	}

	@Override
	public String getText(Object element) {
		if(element instanceof ISphereFactory) {
			return ((ISphereFactory)element).getDescription();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if(element instanceof ImageDescriptor) {
			return registry.createImage((ImageDescriptor)element);
		}
		if(element instanceof ISphereFactory) {
			ImageDescriptor id = ((ISphereFactory)element).getImageDescriptor();
			if(id != null) { return registry.createImage(id); }
		}
		return super.getImage(element);
	}

}
