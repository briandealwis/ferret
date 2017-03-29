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
import ca.ubc.cs.clustering.IClusteringsFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttributeClusteringsFactory<T> implements IClusteringsFactory<T> {

	public static final String UNKNOWN = "<not applicable>";
	
	Map<String,Clustering<T>> clusterings = new HashMap<String,Clustering<T>>();
	
    public AttributeClusteringsFactory() {}

    public Collection<Clustering<T>> getAllClusterings() {
    	return clusterings.values();
    }

	public Collection<Clustering<T>> build(ClusterableCollection<T> objects) {
		for(String attrName : objects.getAttributeNames()) {
			for(T item : objects.getElements()) {
				IAttributeSource source = objects.getAttributeSource(item);
				Clustering<T> cl = clusterings.get(attrName);
				if(cl == null) {
					clusterings.put(attrName,  cl = new Clustering<T>(source.describe(attrName)));
				} else if(cl.getName() == null) {
					cl.setName(source.describe(attrName));	// somebody must know it
				}
				Object value = source.getAttribute(attrName, item);
				if(value == null) { 
					cl.add(UNKNOWN, item);
				} else if(value instanceof Collection) {
					for(Object v : (Collection<?>)value) {
						cl.add(v, item);
					}
				} else {
					cl.add(value, item);
				}
			}
		}
		return getAllClusterings();
	}

}
