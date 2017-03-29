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

public class FieldShadowsQuery extends JavaRelatedConceptualQuery<FerretObject> {

	public FieldShadowsQuery() {}

	public String getDescription() {
		return "shadows";
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IRelation shadows = getSphere().resolve(monitor, ObjectOrientedRelations.OP_SHADOWS, parameter);
		for(FerretObject s : shadows) {
			SimpleSolution sol = new SimpleSolution(this, this);
			sol.setPrimaryEntityName("shadowed");
			sol.add("shadowed", s);
		}
	}
}
