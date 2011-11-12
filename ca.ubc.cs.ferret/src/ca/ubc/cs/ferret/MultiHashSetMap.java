package ca.ubc.cs.ferret;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.collections15.multimap.MultiHashMap;

@SuppressWarnings("serial")
public class MultiHashSetMap<T1, T2> extends MultiHashMap<T1,T2> {

	public MultiHashSetMap() {
		super();
	}

	public MultiHashSetMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public MultiHashSetMap(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	protected Collection<T2> createCollection(Collection<? extends T2> coll) {
		return coll == null ? new HashSet<T2>() : new HashSet<T2>(coll);
	}

}
