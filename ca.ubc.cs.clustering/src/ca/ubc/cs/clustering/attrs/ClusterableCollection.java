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
package ca.ubc.cs.clustering.attrs;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.ClusteringPlugin;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterableCollection<T> {
    protected Set<T> elements = new HashSet<T>();
    protected Map<T,IAttributeSource> attrMap = 
    	new HashMap<T, IAttributeSource>();
    protected Map<String,Collection<Object>> domains =
    	new HashMap<String, Collection<Object>>();
    protected List<Clustering<T>> clusterings = new LinkedList<Clustering<T>>();
    
    public ClusterableCollection() {
    }
    
    public ClusterableCollection(Collection<? extends T> elmts) {
        for(T e : elmts) { add(e); }
    }
    
    public void add(T element) {
        elements.add(element);
        IAttributeSource source =  ClusteringPlugin.getDefault().getAttributeSourceManager().getAttributeSource(element);
        attrMap.put(element, source);
        for(String attrName : source.getAttributeNames()) {
            Collection<Object> knownDomain = domains.get(attrName);
            if(knownDomain == null) {
            	knownDomain =  new HashSet<Object>();
                domains.put(attrName, knownDomain);
            }
            Collection<?> domain = source.getAttributeDomain(attrName);
            if(domain == null) {
            	Object value = source.getAttribute(attrName, element);
            	if(value instanceof Collection) {
            		knownDomain.addAll((Collection<?>)value);
            	} else {
            		knownDomain.add(value);
            	}
            } else {
                Collections.addAll(knownDomain, domain);
            }
        }
    }
    
    public void addAll(Collection<? extends T> _elements) {
        for(T element : _elements) {
            add(element);
        }
    }

    public void addAll(T _elements[]) {
        for(T element : _elements) {
            add(element);
        }
    }

    public Collection<T> getElements() {
        return elements; 
    }
    
    public IAttributeSource getAttributeSource(T o) { 
        return attrMap.get(o);
    }
    
    public Collection<String> getAttributeNames() {
        return domains.keySet();
    }
    
    public Collection<?> getAttributeDomain(String name) {
        return domains.get(name);
    }

	public int size() {
		return elements.size();
	}
}
