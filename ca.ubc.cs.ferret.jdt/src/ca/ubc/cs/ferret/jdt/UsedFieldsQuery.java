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

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class UsedFieldsQuery extends 
	JavaIntersectionConceptualQuery<IJavaElement,FerretObject> {

    /**
     * The required public 0-argument constructor as per the extension-point.
     */
    public UsedFieldsQuery() {
    }

    public boolean validateParameter(IJavaElement je) {
        try {
            // After all, interfaces don't use fields...
            if(je instanceof IType) {
                return !((IType)je).isInterface();
            } else  if(je instanceof IMember && ((IMember)je).getDeclaringType().isInterface()) {
                return false;
            }
        } catch(JavaModelException e) {
            /* do nothing */
        }
        return !(je instanceof IField);
    }
        
    public String getSubDescription() {
        return "fields used";
    }

	@Override
	protected Collection<FerretObject> performQuery(IJavaElement it,
			IProgressMonitor monitor) {
        IRelation referenced = getSphere().resolve(new SubProgressMonitor(monitor, 10),
        		ObjectOrientedRelations.OP_FIELDS_USED, it);
		return referenced.asCollection();
	}

	@Override
	protected void processSolution(FerretObject field) {
        SimpleSolution s = new SimpleSolution(this, this);
        s.add("reads", field);
        addSolution(s);
}
}
