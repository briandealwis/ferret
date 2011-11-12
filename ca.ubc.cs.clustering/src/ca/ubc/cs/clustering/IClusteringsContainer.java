/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.clustering;

import org.apache.commons.collections15.MultiMap;


public interface IClusteringsContainer<T> {
	/**
     * Return the set of clusterings available from this container..
     * @return the clusterings, grouped by their provider
     */
    public MultiMap<IClusteringsProvider<T>, Clustering<T>> getAllClusterings();

    /**
     * Set the active clustering to be used, or <code>null</code> if no
     * cluster is should be used.
     */
    public void setActiveClustering(Clustering<T> cluster);

    /**
     * Return the active clustering currently used.  Return <code>null</code> if no
     * cluster is currently selected.
     * @return the active cluster, or <code>null</code> if none
     */
    public Clustering<T> getActiveClustering();

	public int getNumberClusterings();

	public int getNumberElements();

}
