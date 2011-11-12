package ca.ubc.cs.ferret.history;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class HistorySphereHelperFactory implements IExecutableExtensionFactory {

	public HistorySphereHelperFactory() {}

	public Object create() throws CoreException {
		return HistorySphereHelper.getDefault();
	}

}
