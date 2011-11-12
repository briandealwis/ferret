package ca.ubc.cs.ferret.sphereconfig;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.DifferencingSphereCompositor;
import ca.ubc.cs.ferret.model.ISphereCompositor;

public class DifferenceSphereCompositorFactory extends AbstractSphereCompositorFactory {

	public DifferenceSphereCompositorFactory() {}

	@Override
	public String getDescription() {
		return "difference";
	}

	protected ISphereCompositor createCompositor() {
		return new DifferencingSphereCompositor();
	}

	@Override
	public IStatus canCreateCompositor() {
		if(sphereFactories.size() > 1) { return Status.OK_STATUS; }
		return new Status(IStatus.ERROR, FerretPlugin.pluginID, 
				FerretErrorConstants.VALIDATION_ERRORS,
				"Differencing requires at least two spherees", null);
	}

}
