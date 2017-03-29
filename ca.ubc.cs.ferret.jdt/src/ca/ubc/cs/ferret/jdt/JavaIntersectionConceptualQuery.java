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
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;

public abstract class JavaIntersectionConceptualQuery<IT,OT> extends AbstractIntersectionConceptualQuery<IT,OT> {
	protected int javaModelCounter = -1;
    
    public JavaIntersectionConceptualQuery() {}
    
    protected void completed() {
    	javaModelCounter = JavaModelHelper.getDefault().getJavaModelCounter();
    	super.completed();
    }
    
    public boolean isValid() {
    	return javaModelCounter == JavaModelHelper.getDefault().getJavaModelCounter();
    }

}
