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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Clustering<T> {
    protected String fName;
    protected Map<Object,Cluster<T>> fGroups =
    	new HashMap<Object,Cluster<T>>();
    protected Set<IRelation> fRelations = new HashSet<IRelation>();

    public Clustering(String _name) {
        fName = _name;
    }

    public String toString() {
        return fName;
    }
    
    public String getName() {
    	return fName;
    }
    
	public void setName(String name) {
		fName = name;
	}

    public Map<Object,Cluster<T>> getClusters() {
        return fGroups;
    }

    /**
     * Find or create a cluster for the provided object.
     * @param g the defining object for the cluster
     * @return the associated cluster
     */
    public Cluster<T> findCluster(Object g) {
    	Cluster<T> result = fGroups.get(g);
    	if(result == null) {
    		result = createCluster(g);
    	}
    	return result;
    }

    public Cluster<T> getCluster(Object g) {
        return fGroups.get(g);
    }
    
    public Cluster<T> createSubcluster(Cluster<T> parent, Object g) {
        Cluster<T> sub = new Cluster<T>(this, g);
        parent.addSubcluster(sub);
        return sub;
    }
    
    public Cluster<T> createCluster(Object g) {
        Cluster<T> group = new Cluster<T>(this, g);
        fGroups.put(g, group);
        return group;
    }
    
    public Cluster<T> createCluster(Object g, IRelation relation) {
        addRelation(relation);
        return createCluster(g);
    }
    
    public void addRelation(IRelation relation) {
        fRelations.add(relation);
    }
    
    public void add(Object groupIndex, T element) {
    	Cluster<T> group = getCluster(groupIndex);
    	if(group == null) { group = createCluster(groupIndex); }
    	group.add(element);
    }
    
    public int numberClusters() {
    	return fGroups.keySet().size();
    }
    
    public int size() {
    	int n = 0;
    	for(Cluster<T> c : fGroups.values()) {
    		n += c.size();
    	}
    	return n;
    }

	public boolean isRelevant() {
		if(getClusters().size() > 1) { return true; }
		if(getClusters().isEmpty()) { return false; }
		Cluster<?> c = getClusters().values().iterator().next();
		return !c.getSubclusters().isEmpty();
	}

}
