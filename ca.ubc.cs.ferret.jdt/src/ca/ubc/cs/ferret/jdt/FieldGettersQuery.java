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
import org.eclipse.jdt.core.IField;

public class FieldGettersQuery extends
		JavaIntersectionConceptualQuery<IField,FerretObject> {

	/**
	 * The required public 0-argument constructor as per the extension-point.
	 */
	public FieldGettersQuery() {
	}

	public String getSubDescription() {
		return "field getters";
	}

	@Override
	protected void processSolution(FerretObject member) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("getter", member);
		addSolution(s);
	}

	@Override
	protected Collection<FerretObject> performQuery(IField field,
			IProgressMonitor monitor) {
		IRelation getters = getSphere().resolve(monitor,
				ObjectOrientedRelations.OP_GETTERS, field);
		return getters.asCollection();
	}
}
