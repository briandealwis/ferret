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
package ca.ubc.cs.ferret.display;

import ca.ubc.cs.ferret.Consultancy;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.Consultation;
import ca.ubc.cs.ferret.model.ErrorRaisingSphere;
import ca.ubc.cs.ferret.model.ISphere;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * This is a display wrapper for a random object; it is not the same as the abstract 
 * base display object.
 * @author bsd
 */
public class DwObject<T> extends DwConsultation {
    protected T object;
    
    public static IDisplayObject forObject(Object _object, IDisplayObject parent) {
    	IDisplayObject ido = FerretPlugin.getAdapter(_object, IDisplayObject.class);
    	if(ido != null) {
    		ido.setParent(parent);
    		return ido;
		}
    	return new DwObject<Object>(_object, parent);
    }

    /**
     * The caller is expected to provide the parent with setParent() before doing anything further.
     * @param _object
     */
    public DwObject(T _object) {
    	super();
    	object = _object;
    }
    
    public DwObject(T _object, IDisplayObject parent) {
    	this(_object, findSphere(parent));
    	setParent(parent);
    }
    
    public DwObject(T _object, ISphere sphere) {
    	super(Consultancy.getDefault().getConsultation(
        			new Object[] { _object }, sphere), null);
    	object = _object;
    }

	private static ISphere findSphere(IDisplayObject parent) {
		while(parent != null && !(parent.getObject() instanceof Consultation)) {
			parent = parent.getParent();
		}
		if(parent != null) {
			return ((Consultation)parent.getObject()).getSphere();
		}
		return new ErrorRaisingSphere("error: spoofed sphere for DwObject.findSphere()");
	}

	public String getText() {
        return FerretPlugin.prettyPrint(getObject());
    }

    public Object getObject() {
        return object;
    }

	@Override
	public ImageDescriptor getImage() {
		return FerretPlugin.getImage(getObject());
	}

	@Override
	public boolean hasChildren() {
		return super.hasChildren();
	}

	@Override
	public void setParent(IDisplayObject parent) {
		if(parent != null && consultation == null) {
			consultation  = Consultancy.getDefault().getConsultation(
        			new Object[] { object }, findSphere(parent));
		}
		super.setParent(parent);
	}
}
