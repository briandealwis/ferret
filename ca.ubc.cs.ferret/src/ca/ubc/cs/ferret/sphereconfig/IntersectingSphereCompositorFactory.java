package ca.ubc.cs.ferret.sphereconfig;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphereCompositor;
import ca.ubc.cs.ferret.model.IntersectingSphereCompositor;

public class IntersectingSphereCompositorFactory extends
		AbstractSphereCompositorFactory {

	public IntersectingSphereCompositorFactory() {}

	@Override
	public String getDescription() {
		return "intersection";
	}

	@Override
	public IStatus canCreateCompositor() {
		if(sphereFactories.size() > 1) { return Status.OK_STATUS; }
		return new Status(IStatus.ERROR, FerretPlugin.pluginID, 
				FerretErrorConstants.VALIDATION_ERRORS,
				"Intersection requires at least two spherees", null);
	}

	protected ISphereCompositor createCompositor() {
		return new IntersectingSphereCompositor();
	}
}
