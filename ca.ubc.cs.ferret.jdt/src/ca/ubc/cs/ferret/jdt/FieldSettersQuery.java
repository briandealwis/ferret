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

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

public class FieldSettersQuery extends JavaIntersectionConceptualQuery<IField,FerretObject> {

	@Override
	protected boolean validateParameter(IField field) {
        try {
        	// if final, then can't be changed, and hence doesn't have setters
        	return !Flags.isFinal(field.getFlags());
        } catch(JavaModelException e) {
        	FerretPlugin.log(e.getStatus());
        	return false;
        }
    }

	/**
     * The required public 0-argument constructor as per the extension-point.
     */
	public FieldSettersQuery() {
	}

    public String getSubDescription() {
        return "field setters";
    }

	@Override
	protected void processSolution(FerretObject member) {
    	SimpleSolution s = new SimpleSolution(this, this);
    	s.add("setter", member);
    	addSolution(s);
	}

	@Override
	protected Collection<FerretObject> performQuery(IField field, IProgressMonitor monitor) {
        IRelation setters = 
        	getSphere().resolve(monitor, ObjectOrientedRelations.OP_SETTERS, field);
		return setters.asCollection();
	}

}
