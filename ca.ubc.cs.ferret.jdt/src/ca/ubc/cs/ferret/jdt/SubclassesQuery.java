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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class SubclassesQuery  extends
		JavaRelatedConceptualQuery<IType> {

	public SubclassesQuery() {}

	public String getDescription() {
		return "subclasses";
	}
	
	@Override
	public boolean validateParameter(IType clazz) {
		try {
			return clazz.isClass();
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return false;
		}
	}


	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IRelation op =
			getSphere().resolve(monitor, ObjectOrientedRelations.OP_SUBCLASSES, parameter);
		for(FerretObject subclass : op) {
			SimpleSolution s = new SimpleSolution(this, subclass);
			s.setPrimaryEntityName("subclass");
			s.add("subclass", subclass);
			addSolution(s);
		}
	}

}
