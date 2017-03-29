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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Cluster<T> {
    protected Clustering<? extends T> clustering;
    protected Object groupIndex;
    protected Collection<Cluster<T>> subclusters;
    protected Set<T> elements = new HashSet<T>();
    
    public Cluster(Clustering<? extends T> _cluster, Object _groupIndex) {
        clustering = _cluster;
        groupIndex = _groupIndex;
    }

    public Cluster(Object _groupIndex) {
    	this(null, _groupIndex);
    }
    
    public Clustering<? extends T> getClustering() {
        return clustering;
    }
    
    public void add(T s) {
        elements.add(s);
    }
    
    public void addAll(Collection<? extends T> coll) {
    	elements.addAll(coll);
    }
    
    public Collection<T> getElements() {
        return elements;
    }
    
    @SuppressWarnings("unchecked")
	public Collection<Cluster<T>> getSubclusters() {
        if (subclusters == null) {
            return Collections.emptyList();
        }
    	return subclusters;
    }

    public void addSubcluster(Cluster<T> subcl) {
    	if(subclusters == null) {
    		subclusters = new HashSet<Cluster<T>>();
    	}
    	subclusters.add(subcl);
    }
    
//    public String toString() {
//        return FerretPlugin.prettyPrint(groupIndex);
//    }

    // FIXME: this is not a great name
    public Object getIndex() {
        return groupIndex;
    }
    
    public int size() {
    	return elements.size();
    }
}
