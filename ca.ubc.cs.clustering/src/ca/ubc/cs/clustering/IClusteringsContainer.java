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
package ca.ubc.cs.clustering;

import com.google.common.collect.Multimap;


public interface IClusteringsContainer<T> {
	/**
     * Return the set of clusterings available from this container..
     * @return the clusterings, grouped by their provider
     */
    public Multimap<IClusteringsProvider<T>, Clustering<T>> getAllClusterings();

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
