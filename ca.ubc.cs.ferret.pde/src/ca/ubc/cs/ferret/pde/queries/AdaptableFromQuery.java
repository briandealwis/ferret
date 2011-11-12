package ca.ubc.cs.ferret.pde.queries;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.pde.internal.core.natures.PDE;

import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.AdaptationSpecification;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import ca.ubc.cs.ferret.pde.PdeSphereHelper;
import ca.ubc.cs.ferret.references.AbstractReference;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class AdaptableFromQuery extends PdeSingleParmConceptualQuery<IType> {

	public AdaptableFromQuery() {
	}

	public String getDescription() {
		return "adaptable from";
	}
	
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
	@Override
	protected void internalRun(IProgressMonitor monitor) {
		monitor.beginTask("Looking for possible adapters for " + parameter, 10);
		try {
			IRelation results = getSphere().resolve(new SubProgressMonitor(monitor, 5),
					PdeSphereHelper.OP_ADAPTABLE_FROM, parameter);
			for(FerretObject result : results) {
				AdaptationSpecification spec = result.getAdapter(AdaptationSpecification.class, Fidelity.Approximate);
				if(spec == null) { continue; }	// this is not expected though
		        SimpleSolution s = new SimpleSolution(this, null);
		        Object answer = attemptTypeResolution(spec.getProvidedType());
		        s.add("adaptable from", answer);
		        s.setPrimaryEntityName("adaptable from");

		        AbstractReference ref = PdeModelHelper.getDefault().generateReference(spec.getSpecifyingPlugin());
	        	ref.setText(spec.getSourceXPath());
	            s.add(new StupidlySimpleRelation(answer, "specified by", ref));
		        s.add(new StupidlySimpleRelation(answer, "factory", attemptTypeResolution(spec.getAdapterType())));
		        addSolution(s);
			}
		} finally {
			monitor.done();
		}
	}

	protected Object attemptTypeResolution(String typeName) {
		IType t = JavaModelHelper.getDefault().resolveType(typeName);
		return t != null ? t : typeName;
	}
}
