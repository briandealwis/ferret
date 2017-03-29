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

import ca.ubc.cs.clustering.attrs.ClusterableCollection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SqueezerClusteringFactory<T> implements IClusteringsFactory<T> {
	public static final String SQUEEZER_THRESHOLD = "SQUEEZER_CLUSTERING_THRESHOLD";
	protected SqueezerClusterer<T> squeezer;
	protected Collection<Clustering<T>> clustering;
	protected float threshold;
	
	public SqueezerClusteringFactory() {
		threshold = getDefaultThreshold();
	}
	
	private float getDefaultThreshold() {
		return  ClusteringPlugin.getDefault().getPluginPreferences()
			.getFloat(SQUEEZER_THRESHOLD);
	}

	public Collection<Clustering<T>> build(ClusterableCollection<T> cc) {
		squeezer = new SqueezerClusterer<T>(cc, threshold);
		return getAllClusterings();
	}

	private float getThreshold() {
		return threshold;
	}

	public Collection<Clustering<T>> getAllClusterings() {
		if(clustering == null) {
			Clustering<T > sqzClustering = new Clustering<T>("Squeezer");
			int i = 0;
			for(List<T> sqc : squeezer.getClusters()) {
				Cluster<T> cluster = sqzClustering.createCluster("Cluster " + Integer.toString(++i));
				cluster.addAll(sqc);
			}
			clustering = new LinkedList<Clustering<T>>();
			clustering.add(sqzClustering);
		}
		return clustering;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

}
