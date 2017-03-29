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

public class ImplementorsQuery extends JavaIntersectionConceptualQuery<FerretObject,FerretObject> {
	public String getSubDescription() {
		return "implementors";
	}

	@Override
	protected Collection<FerretObject> performQuery(FerretObject it, IProgressMonitor monitor) {
		IRelation op =
			getSphere().resolve(monitor,
					ObjectOrientedRelations.OP_IMPLEMENTORS, it);
		Collection<FerretObject> impls = op.asCollection();
		impls.remove(it);	// implementors on a concrete Java class returns itself
		return impls;
	}

	@Override
	protected void processSolution(FerretObject clazz) {
		SimpleSolution s = new SimpleSolution(this, clazz);
		s.add("implementor", clazz);
		addSolution(s);
	}

}
