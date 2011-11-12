/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.clustering.attrs;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An IAttributeSource provides access to the attributes for a particular object.
 * It acts much like Eclipse's IPropertySource interface.
 * @author bsd
 * @see org.eclipse.ui.views.properties.IPropertySource
 */
public interface IAttributeSource {

    /**
     * Return the list of attributes available.  This mirrors IClassifier.
     * @return
     */
    public Collection<String> getAttributeNames();

    /**
     * Fetch the value of the specified attribute.  Return null if no such attribute value
     * was found.  
     * Note that the only manner of distinguishing between a non-existing attribute and an
     * attribute whose value is null is whether <code>name</code> is
     * listed as an available attribute name.
     * @param name the desired attribute's name
     * @param object the object for which to fetch from
     * @return the value of the provided attribute
     */
    public Object getAttribute(String attributeName, Object object);
    
    public String getAttributeText(String attributeName, Object attributeValue);
    
    public ImageDescriptor getAttributeImage(String attributeName, Object attributeValue);

    /**
     * Return the possible domain of the attribute.  Returning null indicates that
     * the domain cannot be enumerated.
     * @return possible attribute values
     */
    public Collection<?> getAttributeDomain(String attributeName);

	public String describe(String attrName);

}
