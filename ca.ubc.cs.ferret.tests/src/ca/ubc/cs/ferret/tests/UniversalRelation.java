package ca.ubc.cs.ferret.tests;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.FerretObject;

/**
 * The universal relation maps each element to itself.
 * @author Brian de Alwis
 */
public class UniversalRelation extends AbstractToolRelation {

	protected FerretObject object;
	protected boolean retrieved = false;
	
	public UniversalRelation() {}

	public boolean hasNext() {
		return !retrieved;
	}

	public FerretObject next() {
		if(retrieved) { return null; }
		retrieved = true;
		return object;
	}

	protected boolean configure(FerretObject... arguments) {
		if(arguments.length != 1) { return false; }
		object = arguments[0];
		return true;
	}

}
