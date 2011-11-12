package ca.ubc.cs.ferret.pde;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class PdeSphereHelperFactory implements IExecutableExtensionFactory {

	public PdeSphereHelperFactory() {
	}

	public Object create() throws CoreException {
		return PdeSphereHelper.getDefault();
	}

}
