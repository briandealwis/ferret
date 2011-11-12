package ca.ubc.cs.clustering;

import java.util.Collection;

public interface IClusteringsProvider<T> {
	/**
     * Return the set of clusterings available from this provider..
     * @return the clusterings
     */
    public Collection<Clustering<T>> getAllClusterings();
}
