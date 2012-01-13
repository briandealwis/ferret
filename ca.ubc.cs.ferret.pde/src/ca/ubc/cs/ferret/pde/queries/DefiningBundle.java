package ca.ubc.cs.ferret.pde.queries;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.natures.PDE;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class DefiningBundle extends PdeSingleParmConceptualQuery<IType> {

	public boolean validateParameter(IType type) {
		IProject project = type.getJavaProject().getProject();
		try {
			return project.hasNature(PDE.PLUGIN_NATURE)
					|| project.hasNature(PDE.FEATURE_NATURE);
		} catch(CoreException e) {
			FerretPlugin.log(e.getStatus());
			return false;
		}
	}

	public String getDescription() {
		return "defined in";
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IProject project = parameter.getJavaProject().getProject();
		IPluginModelBase plugin =
				PdeModelHelper.getDefault().findPluginModel((IProject)project);

		SimpleSolution s = new SimpleSolution(this, null);
		s.add("defined", plugin);
		s.setPrimaryEntityName("defined");
		addSolution(s);
	}
}
