package ca.ubc.cs.ferret.pde.queries;

import java.util.HashSet;
import java.util.Set;

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
import ca.ubc.cs.ferret.model.NamedJoinRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.AdaptationSpecification;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import ca.ubc.cs.ferret.pde.PdeSphereHelper;
import ca.ubc.cs.ferret.references.AbstractReference;

public class AdaptableToQuery extends PdeSingleParmConceptualQuery<IType> {

	public AdaptableToQuery() {
	}

	public boolean validateParameter(IType type) {
		IProject project = type.getJavaProject().getProject();
		try {
			return project.hasNature(PDE.PLUGIN_NATURE)
					|| project.hasNature(PDE.FEATURE_NATURE);
		} catch (CoreException e) {
			FerretPlugin.log(e.getStatus());
			return false;
		}
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		monitor.beginTask("Looking for possible adapters for " + parameter, 10);
		try {
			Set<AdaptationSpecification> adaptationSpecs = new HashSet<AdaptationSpecification>();
			IRelation results = getSphere().resolve(new SubProgressMonitor(monitor, 5),
					PdeSphereHelper.OP_ADAPTABLE_TO, parameter);
			adaptationSpecs.addAll(results.asCollection(AdaptationSpecification.class));
			
			results = new NamedJoinRelation(ObjectOrientedRelations.OP_SUPERTYPES,
					PdeSphereHelper.OP_ADAPTABLE_TO)
				.resolve(new SubProgressMonitor(monitor, 5), getSphere(), parameter);
			adaptationSpecs.addAll(results.asCollection(AdaptationSpecification.class));
			
			for(AdaptationSpecification spec : adaptationSpecs) { processResult(spec); }
		} finally {
			monitor.done();
		}
	}

	private void processResult(AdaptationSpecification spec) {
        SimpleSolution s = new SimpleSolution(this, null);
        Object result = attemptTypeResolution(spec.getDesiredType());
        s.add("adaptable to", result);
        s.setPrimaryEntityName("adaptable to");

        AbstractReference ref = PdeModelHelper.getDefault().generateReference(spec.getSpecifyingPlugin());
    	ref.setText(spec.getDestinationXPath());
        s.add(new StupidlySimpleRelation(result, "specified by", ref));
        s.add(new StupidlySimpleRelation(result, "factory", attemptTypeResolution(spec.getAdapterType())));
        addSolution(s);		
	}

	protected Object attemptTypeResolution(String typeName) {
		IType t = JavaModelHelper.getDefault().resolveType(typeName);
		return t != null ? t : typeName;
	}

	public String getDescription() {
		return "adaptable to";
	}
}
