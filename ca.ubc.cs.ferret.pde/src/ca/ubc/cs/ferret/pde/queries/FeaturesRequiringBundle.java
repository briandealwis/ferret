package ca.ubc.cs.ferret.pde.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class FeaturesRequiringBundle extends
		AbstractIntersectionConceptualQuery<IPluginModelBase, IFeatureModel> {

	@Override
	protected String getSubDescription() {
		return "required by features";
	}

	@Override
	protected Collection<IFeatureModel> performQuery(IPluginModelBase plugin,
			IProgressMonitor monitor) {
		return PdeModelHelper.getDefault().getRequiringFeatures(plugin);
	}

	@Override
	protected void processSolution(IFeatureModel dependent) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("requires", dependent);
		s.setPrimaryEntityName("requires");
		addSolution(s);
	}

	public boolean isValid() {
		return false;
	}

}
