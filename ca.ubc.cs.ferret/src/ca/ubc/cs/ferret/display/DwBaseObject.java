/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.display;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.ICallback;

public abstract class DwBaseObject implements IDisplayObject {
	protected IDisplayObject parent;
    protected IDisplayObject children[] = null;
    protected List<ICallback<? super IDisplayObject>> refreshCallbacks = null;

    public DwBaseObject(IDisplayObject _parent) {
        parent = _parent;
    }

    public IDisplayObject getParent() {
        return parent;
    }
    
    /* Not intended for general use */
	public void setParent(IDisplayObject parent) {
		this.parent = parent; 
	}

    public IDisplayObject[] getChildren() {
        if(shouldRebuildChildren()) {
        	rebuildChildren();
        }
        return children;
    }

    public void rebuildChildren() {
        IDisplayObject oldChildren[] = children;
        buildChildren(oldChildren);
        if(children == null) { throw new AssertionError("children should not be null"); }
        if(oldChildren != null && oldChildren.length > 0) {
        	Set<IDisplayObject> newC = new HashSet<IDisplayObject>();
        	Collections.addAll(newC, children);
        	for(IDisplayObject oldChild : oldChildren) {
        		if(!newC.contains(oldChild)) {
        			oldChild.dispose();
        		}
        	}
        }		
	}

	public int getImportance() {
        return 200; 
    }
    
    /**
     * This object is no longer required; dispose of any resources.  Subclasses
     * may override this as necessary, while ensuring the super is called.
     */
    public void dispose() {
    	if(children != null) {
	        for(IDisplayObject child : children) {
	        	child.dispose();
	        }
	        children = null;
    	}
        parent = null;
    }

    /**
     * A predicate to determine if this object's <code>children</code> should
     * be regenerated.  Subclasses are expected to override this as required, while
     * ensuring the super is called. 
     * @return
     */
    protected boolean shouldRebuildChildren() {
        return children == null;
    }

    public boolean hasChildren() {
        return getChildren().length > 0;
    }
    
    public void removeChild(IDisplayObject child) {
    	getChildren();
    	for(int childIndex = 0; childIndex < children.length; childIndex++) {
    		if(children[childIndex] == child) {
    			children = FerretPlugin.arraycopyExcept(children,
    						new IDisplayObject[children.length - 1], childIndex);
    			refresh();
    			return;
    		}
    	}
    }
    
    public ImageDescriptor getImage() {
        ImageDescriptor id;
        if((id = FerretPlugin.getImage(getObject())) != null) { return id; }
        return PlatformUI.getWorkbench().getSharedImages()
            .getImageDescriptor(hasChildren() ?
                    ISharedImages.IMG_OBJ_FOLDER : ISharedImages.IMG_OBJ_ELEMENT);
    }
 
    public String getText() {
        return FerretPlugin.prettyPrint(getObject());
    }

    /**
     * Populate the <code>children</code> array appropriately.
     * Subclasses are expected to provide implementations of this method.  
     * Subclasses are encouraged to reuse the display objects from
     * <code>oldChildren</code> where possible; 
     * {@link #findDisplayObject(Object, IDisplayObject[])} is useful for
     * this purpose.
     * @param oldChildren TODO
     */
    protected abstract void buildChildren(IDisplayObject[] oldChildren);
    
    public String toString() {
        return getClass().getSimpleName() + "(" + getObject() + ")"; 
    }

    public void addRefreshCallback(ICallback<? super IDisplayObject> callback) {
        if(refreshCallbacks == null) { 
        	refreshCallbacks = new LinkedList<ICallback<? super IDisplayObject>>();
    	}
        refreshCallbacks.add(callback);
    }

    public void removeRefreshCallback(ICallback<? super IDisplayObject> callback) {
        if(refreshCallbacks == null) { return; }
        refreshCallbacks.remove(callback);
    }
    
    public void refresh() {
        refresh(this);
    }
    
    protected void refresh(IDisplayObject object) {
        // Question: should we just do the first refreshes found,
        // or all refreshes in the parent chain?
        if(refreshCallbacks == null || refreshCallbacks.isEmpty()) {
            if(getParent() != null && getParent() instanceof DwBaseObject) {
                ((DwBaseObject)getParent()).refresh(object);
            }
        } else {
            for(ICallback<? super IDisplayObject> c : refreshCallbacks) { c.run(object); }
        }
    }


    public IDisplayObject getDisplayObject(Object selected) {
    	return findDisplayObject(selected, getChildren());
    }
    
    protected IDisplayObject findDisplayObject(Object selected, IDisplayObject dobjs[]) {
    	if(dobjs == null) { return null; }
        for(IDisplayObject child : dobjs) {
            if(selected.equals(child.getObject())) {
                return child;
            }
        }
        return null;
    }
    
    public String getToolTip() {
    	return null;
    }

    @Override
	public boolean equals(Object obj) {
    	return this.getClass() == obj.getClass() && 
    		getObject().equals(((IDisplayObject)obj).getObject());
	}

	@Override
	public int hashCode() {
		return getObject().hashCode();
	}

}
