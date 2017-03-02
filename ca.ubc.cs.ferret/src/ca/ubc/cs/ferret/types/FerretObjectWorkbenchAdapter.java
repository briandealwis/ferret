package ca.ubc.cs.ferret.types;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import ca.ubc.cs.ferret.FerretPlugin;

public class FerretObjectWorkbenchAdapter implements IWorkbenchAdapter {
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	protected static FerretObjectWorkbenchAdapter singleton = new FerretObjectWorkbenchAdapter();

	public static IWorkbenchAdapter getDefault() {
		return singleton;
	}

	private FerretObjectWorkbenchAdapter() {}
	
	public ImageDescriptor getImageDescriptor(Object o) {
		if(o instanceof FerretObject) {
			o = ((FerretObject)o).getPrimaryObject();
		}
		return FerretPlugin.getImage(o);
	}

	public String getLabel(Object o) {
		if(o instanceof FerretObject) {
			o = ((FerretObject)o).getPrimaryObject();
		}
		return FerretPlugin.prettyPrint(o);
	}

	public Object getParent(Object o) {
		if(!(o instanceof FerretObject)) { return null; }
		FerretObject fo = (FerretObject)o;
		IWorkbenchAdapter adapter = FerretPlugin.getAdapter(fo.getPrimaryObject(), IWorkbenchAdapter.class);
		return adapter == null ? null : adapter.getParent(fo.getPrimaryObject());
	}
	
	public Object[] getChildren(Object o) {
		if(!(o instanceof FerretObject)) { return EMPTY_OBJECT_ARRAY; }
		FerretObject fo = (FerretObject)o;
		IWorkbenchAdapter adapter = FerretPlugin.getAdapter(fo.getPrimaryObject(), IWorkbenchAdapter.class);
		return adapter == null ? EMPTY_OBJECT_ARRAY
				: adapter.getChildren(fo.getPrimaryObject());
	}
}
