package ca.ubc.cs.ferret.pde.queries;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;

import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeSphereHelper;
import ca.ubc.cs.ferret.types.FerretObject;

public class DeclaredExtensionPointsQuery extends
		PdeSingleParmConceptualQuery<FerretObject> {

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		monitor.beginTask("Finding extensions of "
				+ FerretPlugin.prettyPrint(parameter), 10);
		monitor.subTask("Querying extensions");
		IRelation extpts = getSphere().resolve(monitor, 
				PdeSphereHelper.OP_DECLARED_EXTENSION_POINTS, parameter);
		for(FerretObject xp : extpts) {
            SimpleSolution s = new SimpleSolution(this, null);
            s.add("extension-point", xp);
            s.add(new StupidlySimpleRelation(parameter, "defines", xp));
            s.setPrimaryEntityName("extension-point");
            addSolution(s);
		}
	}

	public String getDescription() {
		return "declared extension points";
	}
}
