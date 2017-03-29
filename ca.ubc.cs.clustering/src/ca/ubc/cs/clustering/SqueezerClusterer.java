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
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Algorithm taken from
 * Z He et al. (2003). Discovering cluster-based local outliers.
 * Pattern Recognition Letters 24:1641--1650. 
 * @author Brian de Alwis
 * @param <T> the type of the elements to cluster
 */
public class SqueezerClusterer<T> {
	protected ClusterableCollection<T> cc = null;
	protected float threshold;
	protected List<List<T>> clusters = new LinkedList<List<T>>();
	
//	public static SqueezerClusterer<K> cluster(ClusterableCollection<K> coll) {
//		SqueezerClusterer<K> sc = new SqueezerClusterer<K>();
//		sc.build(coll);
//		return sc;
//	}
	
	public SqueezerClusterer(ClusterableCollection<T> _cc, float _threshold) {
		cc = _cc;
		threshold = _threshold;
		build();
	}
	
	public List<List<T>> getClusters() {
		return clusters;
	}
	
	/**
	 * Build the clusters for the provided collection.
	 * @param coll
	 */
	protected void build() {
		for(T tuple : cc.getElements()) {
			if(clusters.isEmpty()) {
				List<T> cluster = new LinkedList<T>();
				cluster.add(tuple);
				clusters.add(cluster);
			} else {
				float maxSimilarity = Integer.MIN_VALUE;
				List<T> maxCluster = null;
				for(List<T> c : clusters) {
					float similarity = calculateSimilarity(c, tuple);
					if(maxSimilarity < similarity) {
						maxCluster = c;
						maxSimilarity = similarity;
					}
				}
				Preconditions.checkNotNull(maxCluster);
				if(maxSimilarity >= threshold) {
					maxCluster.add(tuple);
				} else {
					List<T> cluster = new LinkedList<T>();
					cluster.add(tuple);
					clusters.add(cluster);
				}
			}
		}
	}

	private float calculateSimilarity(List<T> c, T tuple) {
		float similarity = 0;
		for(String attrName : cc.getAttributeNames()) {
			float denom = 0;
			for(Object attrValue : values(c, attrName)) {
				denom += support(c, attrName, attrValue);
			}
			similarity += (float)support(c, attrName, 
					cc.getAttributeSource(tuple).getAttribute(attrName, tuple)) / denom;
		}
		return similarity;
	}

	private int support(List<T> c, String name, Object attribute) {
		int count = 0;
		for(T t : c) {
			// FIXME: what should happen if getAttribute() returns a Collection?
			if(attribute == cc.getAttributeSource(t).getAttribute(name, t)) {
				count++;
			}
		}
		return count;
	}

	private Collection<Object> values(List<T> c, String attrName) {
		Set<Object> vals = new HashSet<Object>();
		for(T t : c) {
			Object v = cc.getAttributeSource(t).getAttribute(attrName, t);
			if(v instanceof Collection) {
				vals.addAll((Collection<?>)v);
			} else {
				vals.add(v);
			}
		}
		return vals;
	}
}
