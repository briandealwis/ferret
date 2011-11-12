package ca.ubc.cs.clustering.attrs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.clustering.ClusteringPlugin;

public class DelegatingAttributeSource implements IAttributeSource {
	protected Map<Object,IAttributeSource> delegateMap = new HashMap<Object, IAttributeSource>();
	
	public DelegatingAttributeSource(Object... delegates) {
		for(Object o : delegates) {
			delegateMap.put(o, getAttributeSource(o));
		}
	}

	protected static IAttributeSource getAttributeSource(Object o) {
		return ClusteringPlugin.getDefault().getAttributeSourceManager().getAttributeSource(o);
	}
	
	public String describe(String attrName) {
		for(Object o : delegateMap.keySet()) {
			IAttributeSource s = delegateMap.get(o);
			String d = s.describe(attrName);
			if(d != null) { return d; }
		}
		return null;
	}

	public Object getAttribute(String attributeName, Object object) {
		// we assume object is our delegate
		for(Object o : delegateMap.keySet()) {
			IAttributeSource s = delegateMap.get(o);
			Object attr = s.getAttribute(attributeName, o);
			if(attr != null) { return attr; }
		}
		return null;
	}

	public Collection<?> getAttributeDomain(String attributeName) {
		Set<Object> domain = new HashSet<Object>();
		for(Object o : delegateMap.keySet()) {
			IAttributeSource s = delegateMap.get(o);
			Collection<?> d = s.getAttributeDomain(attributeName);
			if(d != null) { domain.add(d); }
		}
		return domain;
	}

	public ImageDescriptor getAttributeImage(String attributeName,
			Object attributeValue) {
		for(Object o : delegateMap.keySet()) {
			IAttributeSource s = delegateMap.get(o);
			ImageDescriptor id = s.getAttributeImage(attributeName, o);
			if(id != null) { return id; }
		}
		return null;	
	}

	public Collection<String> getAttributeNames() {
		Set<String> attrNames = new HashSet<String>();
		for(Object o : delegateMap.keySet()) {
			IAttributeSource s = delegateMap.get(o);
			attrNames.addAll(s.getAttributeNames());
		}
		return attrNames;	
	}

	public String getAttributeText(String attributeName, Object attributeValue) {
		for(Object o : delegateMap.keySet()) {
			IAttributeSource s = delegateMap.get(o);
			String t = s.getAttributeText(attributeName, attributeValue);
			if(t != null) { return t; }
		}
		return null;
	}

}
