package ca.ubc.cs.ferret.model;

import java.util.LinkedList;

/**
 * This type hierarchy is used for implementing functional relations.
 * @author Brian de Alwis
 */
public abstract class RelationalFunction extends AbstractRelation {
	protected LinkedList<IRelation> operations = new LinkedList<IRelation>();

	public RelationalFunction() {}

	public RelationalFunction(IRelation... ops) {
		for(IRelation op : ops) {
			add(op);
		}
	}

	/**
	 * Add the provided operation to the pile.
	 * @param op another operation
	 */
	public void add(IRelation op) {
		operations.addLast(op);
	}
	
	public boolean isEmpty() {
		return operations.isEmpty();
	}
}
