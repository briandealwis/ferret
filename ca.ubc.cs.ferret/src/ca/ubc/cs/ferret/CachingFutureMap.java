package ca.ubc.cs.ferret;

import org.apache.commons.collections15.map.ReferenceMap;

/**
 * @since 0.5
 */
@SuppressWarnings("serial")
public class CachingFutureMap<K, V> extends ReferenceMap<K, EclipseFuture<V>> {
	public CachingFutureMap(int size) {
		super();
	}

//	@Override
//	protected boolean removeLRU(HashEntry<K, EclipseFuture<V>> entry) {
//		return entry.getValue().isDone() || entry.getValue().isCancelled();
//	}

	@Override
	public void clear() {
		for(EclipseFuture<V> f : values()) {
			if(!f.isDone()) { f.cancel(true); }
		}    			
		super.clear();
	}

	@Override
	protected void destroyEntry(HashEntry<K, EclipseFuture<V>> entry) {
		if(!entry.getValue().isDone()) { entry.getValue().cancel(true); }
		super.destroyEntry(entry);
	}
	
}
