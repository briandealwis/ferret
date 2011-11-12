package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;

import ca.ubc.cs.ferret.display.DwObject;

public class JdtDisplayObjectFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		return new DwObject(adaptableObject);
	}

	public Class[] getAdapterList() {
		return new Class<?>[] { IJavaElement.class };
	}

}
