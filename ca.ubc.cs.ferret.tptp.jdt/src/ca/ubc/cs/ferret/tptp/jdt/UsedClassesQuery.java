package ca.ubc.cs.ferret.tptp.jdt;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.tptp.TptpSphereHelper;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class UsedClassesQuery extends AbstractSingleParmConceptualQuery<IPackageFragment> {

	public UsedClassesQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
        IRelation types = getSphere().resolve(new SubProgressMonitor(monitor, 10),
        		ObjectOrientedRelations.OP_PROVIDED_TYPES, parameter);
        for(FerretObject t : types) {
            IRelation instantiated = getSphere().resolve(new SubProgressMonitor(monitor, 10),
            		TptpSphereHelper.OP_WAS_INSTANTIATED, t);
            for(FerretObject i : instantiated) {
    		        SimpleSolution s = new SimpleSolution(this, this);
    		        s.add("used", i);
    		        addSolution(s);
    			}
        }
	}

	public String getDescription() {
		return "used classes";
	}

	public boolean isValid() {
		return false;
	}

}
