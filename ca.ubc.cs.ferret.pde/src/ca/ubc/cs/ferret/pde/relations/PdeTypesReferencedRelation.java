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
package ca.ubc.cs.ferret.pde.relations;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.IPluginParent;

public class PdeTypesReferencedRelation extends 
		AbstractCollectionBasedRelation<IPluginObject> {
	
	protected Class<IPluginObject> getInputType() {
		return IPluginObject.class;
	}

	@Override
	protected IPluginObject checkInput(IPluginObject input) {
		return (input instanceof IPluginExtensionPoint
			|| input instanceof IPluginExtension
			|| input instanceof IPluginAttribute
			|| input instanceof IPluginElement) ? input : null;
	}

	@Override
	protected Collection<?> realizeCollection(IPluginObject input) {
		Collection<IType> types = new HashSet<IType>();
		run(input, types);
		return types;
	}

	protected void run(IPluginObject po, Collection<IType> types) {
		if(po instanceof IPluginElement) {
			IPluginElement element = (IPluginElement)po;
			// Could have done: if(element.getAttribute("class") != null) { found.add(...); }
			IPluginAttribute attributes[] = element.getAttributes();
			for(int i = 0; i < attributes.length; i++) {
				run(attributes[i], types);
			}
		}
    	if(po instanceof IPluginAttribute) {
    		IPluginAttribute attr = (IPluginAttribute)po; 
    		IStatus status = JavaConventions.validateJavaTypeName(attr.getValue().trim());
    		if(status.getCode() != IStatus.ERROR) {
    			IType t = JavaModelHelper.getDefault().resolveType(attr.getValue());
    			if(t != null) { types.add(t); }
    		}
    	}
    	if(po instanceof IPluginParent) {
    		IPluginParent parent = (IPluginParent)po;
    		IPluginObject[] children = parent.getChildren();
    		for(int i = 0; i < children.length; i++) {
    			run(children[i], types);
    		}
    	}
    }

}
