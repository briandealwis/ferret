package ca.ubc.cs.ferret.pde.queries;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeSphereHelper;
import ca.ubc.cs.ferret.types.FerretObject;

public class DeclaredExtensionsQuery extends
		PdeSingleParmConceptualQuery<FerretObject> {

	public DeclaredExtensionsQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		monitor.beginTask("Finding extensions of "
				+ FerretPlugin.prettyPrint(parameter), 10);
		monitor.subTask("Querying extensions");
		IRelation exts = getSphere().resolve(monitor, 
				PdeSphereHelper.OP_DECLARED_EXTENSIONS, parameter);
		for(FerretObject extension : exts) {
            SimpleSolution s = new SimpleSolution(this, null);
            s.add("extension", extension);
            s.add(new StupidlySimpleRelation(parameter, "defines-extension", extension));
            s.setPrimaryEntityName("extension");
            addSolution(s);
		}
	}

	public String getDescription() {
		return "declared extensions";
	}


}
