package ca.ubc.cs.ferret.tptp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class TptpSphereHelperFactory implements IExecutableExtensionFactory {

	public TptpSphereHelperFactory() {}

	public Object create() throws CoreException {
		return TptpSphereHelper.getDefault();
	}

}
