/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.clustering;

import java.util.Collection;

import ca.ubc.cs.clustering.attrs.ClusterableCollection;

public interface IClusteringsFactory<T> extends IClusteringsProvider<T> {
	
	public Collection<Clustering<T>> build(ClusterableCollection<T> objects);
}
