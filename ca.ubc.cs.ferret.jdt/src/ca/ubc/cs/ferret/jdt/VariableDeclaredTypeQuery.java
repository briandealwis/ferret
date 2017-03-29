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

public class VariableDeclaredTypeQuery extends JavaRelatedConceptualQuery<FerretObject> {

	public VariableDeclaredTypeQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IRelation dt = getSphere().resolve(monitor, ObjectOrientedRelations.OP_DECLARED_TYPES, parameter);
		for(FerretObject t : dt) {
			SimpleSolution s = new SimpleSolution(this, this);
			s.add("declared-type", t);
			addSolution(s);
		}
	}

	public String getDescription() {
		return "declared type";
	}

}
