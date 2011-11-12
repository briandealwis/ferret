/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.clustering.attrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;

public class AttributeSource implements IAttributeSource {
    
    protected Map<String, IClassifier> map = new HashMap<String, IClassifier>();
    protected Map<String, String> descriptions = new HashMap<String, String>();
    
    public AttributeSource() {
    }

    public int hashCode() {
        return map.hashCode();
    }
    
    public boolean equals(Object o) {
        return map.equals(((AttributeSource)o).map);
    }
    
    public void add(IClassifier source, String name, String desc) {
    	map.put(name, source);
    	descriptions.put(name, desc);
    }
    
    public Collection<String> getAttributeNames() {
        return map.keySet();
    }

    public Object getAttribute(String attributeName, Object object) {
    	IClassifier source = map.get(attributeName);
    	if(source == null) { return null; }
    	return source.getCategory(object);
    }

    public Collection<?> getAttributeDomain(String attributeName) {
    	IClassifier source = map.get(attributeName);
    	if(source == null) { return null; }
    	Object[] categories = source.getCategories();
    	if(categories == null) { return null; }
    	ArrayList<Object> cats = new ArrayList<Object>(categories.length);
    	Collections.addAll(cats, categories);
    	return cats;
    }

	public ImageDescriptor getAttributeImage(String attributeName,
			Object attributeValue) {
    	IClassifier source = map.get(attributeName);
    	if(source == null) { return null; }
    	return source.getCategoryImage(attributeValue);
	}

	public String getAttributeText(String attributeName, Object attributeValue) {
    	IClassifier source = map.get(attributeName);
    	if(source == null) { return null; }
    	return source.getCategoryText(attributeValue);
	}

	public String describe(String attrName) {
    	IClassifier source = map.get(attrName);
    	if(source == null) { return "(unknown)"; }		
		return descriptions.get(attrName);
	}

}
