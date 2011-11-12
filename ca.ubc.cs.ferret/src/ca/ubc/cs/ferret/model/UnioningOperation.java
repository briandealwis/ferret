package ca.ubc.cs.ferret.model;

import java.util.NoSuchElementException;

import ca.ubc.cs.ferret.types.FerretObject;

/**
 * Return the union of results from the constituent operations.
 * Repeats are not removed -- one possibility is to use the following:
 * <blockquote>
 * 	   results = new HashSet(op.asCollection());
 * </blockquote>
 * @author Brian de Alwis
 */
public class UnioningOperation extends RelationalFunction {

	public UnioningOperation() {
		super();
	}

	public UnioningOperation(IRelation... ops) {
		super(ops);
	}

	public boolean hasNext() {
		while(!operations.isEmpty()) {
			if(operations.getFirst().hasNext()) { return true; }
			operations.removeFirst();
		}
		return false; 
	}

	public FerretObject next() {
		if(!hasNext()) { throw new NoSuchElementException(); }
		return operations.getFirst().next();
	}

}
