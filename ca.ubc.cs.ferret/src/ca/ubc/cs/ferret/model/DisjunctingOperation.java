package ca.ubc.cs.ferret.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import ca.ubc.cs.ferret.types.FerretObject;

/**
 * Reports the elements of that are reported by only a single of the
 * constituent operations. 
 * @author Brian de Alwis
 */

public class DisjunctingOperation extends RelationalFunction {

	protected Iterator<FerretObject> iterator;

	public DisjunctingOperation() {}

	public DisjunctingOperation(IRelation... ops) {
		super(ops);
	}

	public boolean hasNext() {
		if(iterator == null) { computeDisjunction(); }
		return iterator.hasNext(); 
	}

	public FerretObject next() {
		return iterator.next();
	}

	protected void computeDisjunction() {
		if(operations.isEmpty()) {
			iterator = Iterators.forArray();
			return;
		}
		List<Collection<FerretObject>> results = new ArrayList<Collection<FerretObject>>(operations.size());
		for(IRelation op : operations) {
			results.add(op.asCollection());
		}
		Collections.sort(results, (o1, o2) -> o1.size() - o2.size());
		Set<FerretObject> disjunction = new LinkedHashSet<>(results.remove(0));
		for(Collection<FerretObject> coll : results) {
			disjunction = Sets.symmetricDifference(disjunction, new LinkedHashSet<>(coll));
		}
		iterator = disjunction.iterator();
	}
}
