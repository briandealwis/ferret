package ca.ubc.cs.ferret.sphereconfig;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.DisjunctingSphereCompositor;
import ca.ubc.cs.ferret.model.ISphereCompositor;

public class DisjunctingSphereCompositorFactory extends
		AbstractSphereCompositorFactory {

	public DisjunctingSphereCompositorFactory() {}

	@Override
	public String getDescription() {
		return "disjunction";
	}

	@Override
	public IStatus canCreateCompositor() {
		if(sphereFactories.size() > 1) { return Status.OK_STATUS; }
		return new Status(IStatus.ERROR, FerretPlugin.pluginID, 
				FerretErrorConstants.VALIDATION_ERRORS,
				"Disjunction requires at least two spherees", null);
	}

	protected ISphereCompositor createCompositor() {
		return new DisjunctingSphereCompositor();
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
