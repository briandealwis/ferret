package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class SubinterfacesQuery extends
		JavaIntersectionConceptualQuery<IType,FerretObject> {

	public SubinterfacesQuery() {}

	public String getSubDescription() {
		return "subinterfaces";
	}

	@Override
	protected boolean validateParameter(IType value) {
		try {
			return value.isInterface();
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return false;
		}
	}

	@Override
	protected Collection<FerretObject> performQuery(IType iface, IProgressMonitor monitor) {
		IRelation op =
			getSphere().resolve(monitor,ObjectOrientedRelations.OP_SUBINTERFACES, iface);
		return op.asCollection();
	}

	@Override
	protected void processSolution(FerretObject subiface) {
		SimpleSolution s = new SimpleSolution(this, subiface);
		s.add("subinterface", subiface);
		addSolution(s);		
	}


}
