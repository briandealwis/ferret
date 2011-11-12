package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

public class FieldSettersQuery extends JavaIntersectionConceptualQuery<IField,FerretObject> {

	@Override
	protected boolean validateParameter(IField field) {
        try {
        	// if final, then can't be changed, and hence doesn't have setters
        	return !Flags.isFinal(field.getFlags());
        } catch(JavaModelException e) {
        	FerretPlugin.log(e.getStatus());
        	return false;
        }
    }

	/**
     * The required public 0-argument constructor as per the extension-point.
     */
	public FieldSettersQuery() {
	}

    /*
     * (non-Javadoc)
     * 
     * @see ca.ubc.cs.queryguru.IConceptualQuery#getDescription() @author bsd
     * @since X
     */
    public String getSubDescription() {
        return "field setters";
    }

	@Override
	protected void processSolution(FerretObject member) {
    	SimpleSolution s = new SimpleSolution(this, this);
    	s.add("setter", member);
    	addSolution(s);
	}

	@Override
	protected Collection<FerretObject> performQuery(IField field, IProgressMonitor monitor) {
        IRelation setters = 
        	getSphere().resolve(monitor, ObjectOrientedRelations.OP_SETTERS, field);
		return setters.asCollection();
	}

}
