package ca.ubc.cs.ferret.pde.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.text.bundle.PackageObject;

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class BundlesExportingPackage extends
		AbstractIntersectionConceptualQuery<PackageObject, IPluginModelBase> {

	@Override
	protected String getSubDescription() {
		return "exported by";
	}

	@Override
	protected Collection<IPluginModelBase> performQuery(PackageObject pkg,
			IProgressMonitor monitor) {
		return PdeModelHelper.getDefault().getBundlesExporting(pkg);
	}

	@Override
	protected void processSolution(IPluginModelBase dependent) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("exported by", dependent);
		s.setPrimaryEntityName("exported by");
		addSolution(s);
	}

	public boolean isValid() {
		return false;
	}

}