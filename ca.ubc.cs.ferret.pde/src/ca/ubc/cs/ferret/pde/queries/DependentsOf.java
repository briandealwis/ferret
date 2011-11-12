package ca.ubc.cs.ferret.pde.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class DependentsOf extends AbstractIntersectionConceptualQuery<IPluginModelBase,IPluginModelBase>  {
	public DependentsOf() {	}

	@Override
	protected String getSubDescription() {
		return "dependent plug-ins";
	}

	@Override
	protected Collection<IPluginModelBase> performQuery(IPluginModelBase plugin,
			IProgressMonitor monitor) {
		return PdeModelHelper.getDefault().getDependents(plugin);
	}

	@Override
	protected void processSolution(IPluginModelBase dependent) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("dependent", dependent);
		s.setPrimaryEntityName("dependent");
		addSolution(s);
	}

	public boolean isValid() {
		return false;
	}

}
