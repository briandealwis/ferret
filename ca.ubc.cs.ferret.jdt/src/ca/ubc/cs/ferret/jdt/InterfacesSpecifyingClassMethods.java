package ca.ubc.cs.ferret.jdt;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;

public class InterfacesSpecifyingClassMethods extends JavaIntersectionConceptualQuery<IMethod,IType> {
	public InterfacesSpecifyingClassMethods() {}
	
	@Override
	protected boolean validateParameter(IMethod method) {
		try {
			return method.getDeclaringType().isClass() && 
				Flags.isPublic(method.getFlags());
		} catch(JavaModelException e) {
			FerretPlugin.log(e.getStatus());
			return false;
		}
	}
	
	@Override
	protected void processSolution(IType iface) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("specified-by", iface);
		addSolution(s);
	}
	
	@Override
	protected Collection<IType> performQuery(IMethod method, IProgressMonitor monitor) {
		IRelation specifications = getSphere().resolve(monitor,
				ObjectOrientedRelations.OP_SPECIFICATIONS, method);
		Collection<IType> result = new HashSet<IType>();
		for(IMethod spec : specifications.asCollection(IMethod.class)) {
			result.add(spec.getDeclaringType());
		}
		return result;
	}
	
	@Override
	public String getSubDescription() {
		return "interfaces specifying methods"; 
	}

}
