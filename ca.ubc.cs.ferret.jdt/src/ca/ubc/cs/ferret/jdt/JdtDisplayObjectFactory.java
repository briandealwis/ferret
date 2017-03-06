package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;

import ca.ubc.cs.ferret.display.DwObject;
import ca.ubc.cs.ferret.display.IDisplayObject;

public class JdtDisplayObjectFactory implements IAdapterFactory {

	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == IDisplayObject.class) {
			return adapterType.cast(new DwObject<Object>(adaptableObject));
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IJavaElement.class };
	}

}
