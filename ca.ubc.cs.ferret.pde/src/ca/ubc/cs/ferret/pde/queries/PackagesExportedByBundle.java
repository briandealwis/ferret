package ca.ubc.cs.ferret.pde.queries;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.pde.core.plugin.IPluginModelBase;

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;

public class PackagesExportedByBundle extends
		AbstractIntersectionConceptualQuery<IPluginModelBase, ExportPackageDescription> {

	@Override
	protected String getSubDescription() {
		return "exports packages";
	}

	@Override
	protected Collection<ExportPackageDescription> performQuery(IPluginModelBase bundle,
			IProgressMonitor monitor) {
		return Arrays.asList(bundle.getBundleDescription().getExportPackages());
	}

	@Override
	protected void processSolution(ExportPackageDescription dependent) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("exports", dependent);
		s.setPrimaryEntityName("exports");
		addSolution(s);
	}

	public boolean isValid() {
		return false;
	}

}