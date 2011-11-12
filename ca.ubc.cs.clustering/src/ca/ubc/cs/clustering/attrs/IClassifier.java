/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.clustering.attrs;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * A classifier is responsible for classifying an object amongst a set of
 * (possibly uncountable) categories.  For example, a Java protection classifier
 * will classify a method as being public, private, protected, or package protected.
 * @author Brian de Alwis
 * @param <T> the object types
 * @param<C> the category types
 */
public interface IClassifier<T,C> {
    /**
     * Return the category for the provided object.
     * @param object the object to be queried
     * @return the category
     */
    public C getCategory(T object);

    /**
     * Return the domain used by this classifier.  Returning null indicates that
     * the domain cannot be enumerated.
     * @return the domain of possible categories
     */
    public C[] getCategories();

	String UNDETERMINED_ATTRIBUTE_VALUE = "undetermined";

    /**
     * Return a text description of this particular category, suitable for display.
     * @param category the category of the object
     * @return the text
     */
    public String getCategoryText(C category);

    /**
     * Return an image for this particular category.
     * @param category the category of the object
     * @return the image
     */
    public ImageDescriptor getCategoryImage(C category);

}
