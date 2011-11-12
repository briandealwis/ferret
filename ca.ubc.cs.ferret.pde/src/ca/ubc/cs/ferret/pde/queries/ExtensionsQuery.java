package ca.ubc.cs.ferret.pde.queries;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;

import ca.ubc.cs.ferret.model.IConceptualQuery;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeSphereHelper;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class ExtensionsQuery extends PdeSingleParmConceptualQuery<FerretObject> 
		implements IConceptualQuery {

	public ExtensionsQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IRelation extensions = getSphere().resolve(monitor, PdeSphereHelper.OP_EXTENDED_BY, parameter);
		for(FerretObject result : extensions) {
			SimpleSolution s = new SimpleSolution(this, this);
			s.setPrimaryEntityName("extension");
			s.add("extension", result);
			addSolution(s);
		}
	}

	public String getDescription() {
		return "extensions";
	}

	@Override
	protected boolean validateParameter(FerretObject value) {
		return value.getAdapter(IPluginExtensionPoint.class, Fidelity.Approximate) != null;
	}
}
