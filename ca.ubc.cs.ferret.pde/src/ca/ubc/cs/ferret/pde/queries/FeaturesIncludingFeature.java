package ca.ubc.cs.ferret.pde.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class FeaturesIncludingFeature extends
		AbstractIntersectionConceptualQuery<IFeatureModel, IFeatureModel> {

	@Override
	protected String getSubDescription() {
		return "included by";
	}

	@Override
	protected Collection<IFeatureModel> performQuery(IFeatureModel feature,
			IProgressMonitor monitor) {
		return PdeModelHelper.getDefault().getFeaturesIncluding(feature);
	}

	@Override
	protected void processSolution(IFeatureModel dependent) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("included by", dependent);
		s.setPrimaryEntityName("included by");
		addSolution(s);
	}

	public boolean isValid() {
		return false;
	}

}
