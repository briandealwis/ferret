/*
 * Copyright Sep 23, 2004 X @author 2004
 */
package ca.ubc.cs.ferret.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

import ca.ubc.cs.clustering.Cluster;
import ca.ubc.cs.clustering.IClusteringsContainer;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.display.DwConceptualQuery;
import ca.ubc.cs.ferret.display.DwFactList;
import ca.ubc.cs.ferret.display.IDisplayObject;
import ca.ubc.cs.ferret.model.IConceptualQuery;
import ca.ubc.cs.ferret.model.SphereHelper;
import ca.ubc.cs.ferret.types.FerretObject;


public class DossierLabelProvider implements ITableLabelProvider, ILabelProvider {
    
	protected ResourceManager registry = new LocalResourceManager(JFaceResources.getResources(FerretPlugin.getDefault().getWorkbench().getDisplay()));
    
    public DossierLabelProvider() {
        super();
    }

    public String getObjectText(Object obj) {
        String label = null;
        if (obj instanceof IDisplayObject) {
            if((label = ((IDisplayObject)obj).getText()) != null) { return label; }
            obj = ((IDisplayObject)obj).getObject();
        }
        if(obj == null) { return "<<null>>"; }

        if(obj instanceof IConceptualQuery) {
        	return ((IConceptualQuery)obj).getDescription();
        }
        
        for(SphereHelper sphere : FerretPlugin.getSphereHelpers()) {
            label = sphere.getLabel(obj);
            if(label != null && label.length() > 0) { return label; }
        }
        if(obj instanceof IWorkbenchAdapter) {
            return ((IWorkbenchAdapter)obj).getLabel(obj);
        }
        if(obj instanceof IAdaptable) {
            IWorkbenchAdapter wa = (IWorkbenchAdapter)((IAdaptable)obj).getAdapter(IWorkbenchAdapter.class);
            if(wa != null) { return wa.getLabel(obj); }
        }
        return obj.toString();
    }

    public Image getObjectImage(Object obj) {
        if (obj instanceof IDisplayObject) {
            ImageDescriptor id = null;
            if((id =  ((IDisplayObject)obj).getImage()) != null) {
            	return registry.createImage(id);
            }
            obj = ((IDisplayObject)obj).getObject();
        } 
        if(obj instanceof Cluster) {
            obj = ((Cluster<?>)obj).getIndex();
        }
        ImageDescriptor id = FerretPlugin.getImage(obj);  
        if(id != null) {
        	return registry.createImage(id);
        }
        return null;
    }

    public void dispose() {
    	if(registry != null) { registry.dispose(); }
    	registry = null;
    }

	public Image getColumnImage(Object element, int columnIndex) {
		if(columnIndex == DossierConstants.COLUMN_DESCRIPTION) {
			return getObjectImage(element);
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if(element == null) { return null; }
		switch(columnIndex) {
		case DossierConstants.COLUMN_DESCRIPTION:
			if(element instanceof DwConceptualQuery) {
				DwConceptualQuery dwcq = (DwConceptualQuery)element;
				IConceptualQuery icq = (IConceptualQuery)dwcq.getObject();
				return icq.getDescription();
			}
			return getObjectText(element);

		case DossierConstants.COLUMN_CATEGORY:
			if(element instanceof DwConceptualQuery) {
				DwConceptualQuery dwcq = (DwConceptualQuery)element;
				IConceptualQuery icq = (IConceptualQuery)dwcq.getObject();
				return icq.getCategory();
			}
			break;

		case DossierConstants.COLUMN_ELEMENT_COUNT:
			if(element instanceof DwConceptualQuery) {
				DwConceptualQuery dwcq = (DwConceptualQuery)element;
				IConceptualQuery icq = (IConceptualQuery)dwcq.getObject();
				return icq.isDone() ? Integer.toString(icq.getSolutions().size())
						: "-";
			} else if(element instanceof DwFactList) {
				return Integer.toString(((IDisplayObject)element).getChildren().length);
			} else if(element instanceof IClusteringsContainer) {
				return Integer.toString(((IClusteringsContainer<?>)element).getNumberElements());
			}
			break;
			
		case DossierConstants.COLUMN_CLUSTERING:
			if(element instanceof IClusteringsContainer) {
				IClusteringsContainer<?> container = (IClusteringsContainer<?>)element;
				if(container.getActiveClustering() == null) {
					return container.getAllClusterings() != null && container.getAllClusterings().size() > 0
							? "(" + Integer.toString(container.getNumberClusterings()) + " clusterings)"
							: "";
				} else {
					return container.getActiveClustering().toString();				
				}
			} else if(element instanceof IDisplayObject
					&& ((IDisplayObject)element).getObject() instanceof FerretObject) {
				return ((FerretObject)((IDisplayObject)element).getObject()).getPrimaryFidelity().toString();
			}
			break;
		}
		return "";
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getImage(Object element) {
		return getObjectImage(element);
	}

	public String getText(Object element) {
		return getObjectText(element);
	}
}