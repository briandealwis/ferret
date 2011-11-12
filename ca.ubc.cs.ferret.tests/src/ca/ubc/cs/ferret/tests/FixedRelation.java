package ca.ubc.cs.ferret.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

/**
 * An test operator that always returns the values provided to its constructor. 
 * @author Brian de Alwis
 */
public class FixedRelation extends AbstractToolRelation {
	protected Collection<FerretObject> collection;
	protected Iterator<FerretObject> iterator;
	
	public FixedRelation(Collection<FerretObject> values) {
		collection = new ArrayList<FerretObject>(values);
	}

	public FixedRelation(FerretObject... values) {
		collection = new ArrayList<FerretObject>(values.length);
		Collections.addAll(collection, values);
	}

	public FixedRelation(ISphere tb, Object... values) {
		this(FerretObject.wrap(values, Fidelity.Exact, tb));
	}

	@Override
	protected boolean configure(FerretObject... arguments) {
		iterator = collection.iterator(); 
		return !collection.isEmpty();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public FerretObject next() {
		return iterator.next();
	}

}
