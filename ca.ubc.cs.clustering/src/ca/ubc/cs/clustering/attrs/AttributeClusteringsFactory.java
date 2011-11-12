/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.clustering.attrs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.IClusteringsFactory;

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
