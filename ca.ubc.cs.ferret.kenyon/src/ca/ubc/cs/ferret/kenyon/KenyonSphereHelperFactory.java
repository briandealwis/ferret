package ca.ubc.cs.ferret.kenyon;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class KenyonSphereHelperFactory implements IExecutableExtensionFactory {

	public KenyonSphereHelperFactory() {
		super();
	}

	public Object create() throws CoreException {
		return KenyonSphereHelper.getDefault();
	}

}