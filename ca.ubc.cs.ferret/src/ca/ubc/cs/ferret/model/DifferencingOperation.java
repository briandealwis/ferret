package ca.ubc.cs.ferret.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Iterators;

import ca.ubc.cs.ferret.types.FerretObject;

/**
 * Reports the elements of the first operation that are <EM>not</EM>
 * reported by any of the subsequent operations. 
 * @author Brian de Alwis
 */
public class DifferencingOperation extends RelationalFunction {
	protected Iterator<FerretObject> iterator;
	
	public DifferencingOperation() {	}

	public DifferencingOperation(IRelation... ops) {
		super(ops);
	}

	public boolean hasNext() {
		if(iterator == null) { computeDifference(); }
		return iterator.hasNext(); 
	}

	public FerretObject next() {
		return iterator.next();
	}

	protected void computeDifference() {
		if(operations.isEmpty()) {
			iterator = Iterators.forArray();
			return;
		}
		List<Collection<FerretObject>> results = new ArrayList<Collection<FerretObject>>(operations.size());
		for(IRelation op : operations) {
			results.add(op.asCollection());
		}
		Collection<FerretObject> difference = new LinkedHashSet<>(results.remove(0));
		for(Collection<FerretObject> coll : results) {
			difference.removeAll(coll);
		}
		iterator = difference.iterator();
	}

}
