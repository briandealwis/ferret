package ca.ubc.cs.ferret.kenyon.queries;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;

public abstract class AbstractKenyonIntersectingConceptualQuery<IT,OT> extends
		AbstractIntersectionConceptualQuery<IT,OT> {

	public AbstractKenyonIntersectingConceptualQuery() {
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		super.internalRun(monitor);
		KenyonSphereHelper.getDefault().closeSession();
	}
	
}
