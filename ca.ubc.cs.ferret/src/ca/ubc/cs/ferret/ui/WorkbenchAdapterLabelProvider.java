package ca.ubc.cs.ferret.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

import ca.ubc.cs.ferret.FerretPlugin;

public class WorkbenchAdapterLabelProvider extends LabelProvider {
	protected ResourceManager registry = new LocalResourceManager(JFaceResources.getResources(FerretPlugin.getDefault().getWorkbench().getDisplay()));
	
	@Override
	public void dispose() {
		super.dispose();
		if(registry != null) { registry.dispose(); }
		registry = null;
	}

	@Override
	public Image getImage(Object element) {
        IWorkbenchAdapter adapter = FerretPlugin.getAdapter(element, IWorkbenchAdapter.class);
        if (adapter != null) {
	        ImageDescriptor descriptor = adapter.getImageDescriptor(element);
	        if (descriptor != null) {
	            return registry.createImage(descriptor);
	        }
        }
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
        IWorkbenchAdapter adapter = FerretPlugin.getAdapter(element, IWorkbenchAdapter.class);
        if (adapter != null) {
	        return adapter.getLabel(element);
        }
		return super.getText(element);
	}

}
