package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class ReferencesToType extends JavaIntersectionConceptualQuery<IType,FerretObject> {
    
    public ReferencesToType() {
    }
    
    protected String getReferencesOperation() {
		return ObjectOrientedRelations.OP_TYPE_REFERENCES;
	}

	/* (non-Javadoc)
     * @see ca.ubc.cs.queryguru.cq.IConceptualQuery#getDescription()
     * @author bsd
     * @since X
     */
    public String getSubDescription() {
        return "references to type";
        //+ FerretPlugin.prettyPrint(method) + " (incl through interfaces and subtypes)";
    }

	@Override
	protected Collection<FerretObject> performQuery(IType it,
			IProgressMonitor monitor) {
    	IRelation op = getSphere().resolve(monitor,
        		getReferencesOperation(), it);
    	return op.asCollection();
	}

	@Override
	protected void processSolution(FerretObject e) {
    	SimpleSolution s = new SimpleSolution(this, e);
        s.add("item", e);
        addSolution(s);		
	}

}
