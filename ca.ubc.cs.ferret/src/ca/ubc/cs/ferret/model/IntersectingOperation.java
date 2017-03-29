/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.model;

import ca.ubc.cs.ferret.types.FerretObject;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Reports only those elements reported by all of the constituent operations. 
 * @author Brian de Alwis
 */
public class IntersectingOperation extends RelationalFunction {
	protected Iterator<FerretObject> iterator;

	public IntersectingOperation() {}

	public IntersectingOperation(IRelation... ops) {
		super(ops);
	}

	public boolean hasNext() {
		if(iterator == null) { computeIntersection(); }
		return iterator.hasNext(); 
	}

	public FerretObject next() {
		return iterator.next();
	}

	protected void computeIntersection() {
		if(operations.isEmpty()) {
			iterator = Iterators.forArray();
			return;
		}
		List<Collection<FerretObject>> results = new ArrayList<Collection<FerretObject>>(operations.size());
		for(IRelation op : operations) {
			results.add(op.asCollection());
		}
		Collections.sort(results, new Comparator<Collection<FerretObject>>() {
			public int compare(Collection<FerretObject> o1, Collection<FerretObject> o2) {
				return o1.size() - o2.size();
			}});
		Set<FerretObject> intersection = new LinkedHashSet<>(results.remove(0));
		for(Collection<FerretObject> coll : results) {
			intersection = Sets.intersection(intersection, new LinkedHashSet<>(coll));
		}
		iterator = intersection.iterator();
	}

}
