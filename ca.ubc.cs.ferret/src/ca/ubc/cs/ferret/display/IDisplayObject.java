/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.display;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.ICallback;

public interface IDisplayObject {
    public IDisplayObject getParent();
    public IDisplayObject[] getChildren();
    public boolean hasChildren();
    public String getText();
    public ImageDescriptor getImage();
    public String getToolTip();
    
    /**
     * Return the wrapped object
     * @return
     */
    public Object getObject();

    /**
     * Release any resources held by this object and any of its children.
     */
    public void dispose();
    
    /**
     * Return the general importance of this element, as compared to other types
     * of IDisplayObject.  0 (or MIN_VALUE?) is the most important, Integer.MAX_VALUE is the least.
     * @return importance
     */
    public int getImportance();
    
    /**
     * Cause a callback to be registered for refresh events.
     * @param callback
     */
    public void addRefreshCallback(ICallback<? super IDisplayObject> callback);
    
    /**
     * Remove the provided refresh callback.
     * @param callback
     */
    public void removeRefreshCallback(ICallback<? super IDisplayObject> callback);

    /**
     * Return the child IDisplayObject corresponding to the provided object.
     * @param selected
     * @return corresponding IDisplayObject
     */
    public IDisplayObject getDisplayObject(Object selected);
	
    /**
     * Remove the child display object. Triggers refresh callbacks.
     * @param o
     */
    public void removeChild(IDisplayObject o);
    
	public void setParent(IDisplayObject parent);
}
