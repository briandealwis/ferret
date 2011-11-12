package ca.ubc.cs.ferret.sphereconfig;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ca.ubc.cs.ferret.model.ISphereCompositor;
import ca.ubc.cs.ferret.model.UnioningSphereCompositor;

public class UnioningSphereFactory extends
		AbstractSphereCompositorFactory {

	public UnioningSphereFactory() {}

	@Override
	public String getDescription() {
		return "union";
	}

	@Override
	public IStatus canCreateCompositor() {
		// we allow a union with only a single element
		return Status.OK_STATUS;
	}

	protected ISphereCompositor createCompositor() {
		return new UnioningSphereCompositor();
	}

}
