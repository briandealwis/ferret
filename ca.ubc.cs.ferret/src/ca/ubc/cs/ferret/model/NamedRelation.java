package ca.ubc.cs.ferret.model;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class NamedRelation implements IRelationFactory {
	protected String relationName;

	public NamedRelation(String name) {
		relationName = name;
	}
	
	public IRelation configure(IProgressMonitor monitor,
			IRelationResolver resolver, FerretObject... arguments) {
		IRelation top;
		try {
			top = resolver.topPerform(monitor, relationName, arguments);
		} catch(UnsupportedOperationException e) { return null; }
		return top;
	}

	public IRelation configure(IProgressMonitor monitor,
			IRelationResolver resolver, Object... arguments) {
		return configure(monitor, resolver, 
				FerretObject.wrap(arguments, Fidelity.Exact, resolver.getRootSphere()));
	}
	
}
