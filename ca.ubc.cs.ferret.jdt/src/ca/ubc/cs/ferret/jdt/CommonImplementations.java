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
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;

public class CommonImplementations extends JavaIntersectionConceptualQuery<FerretObject, FerretObject> {

	public CommonImplementations() {
	}

	@Override
	protected String getSubDescription() {
		return "implementations";
	}

	@Override
	protected Collection<FerretObject> performQuery(FerretObject it, IProgressMonitor monitor) {
		IRelation supertypes = getSphere().resolve(monitor, ObjectOrientedRelations.OP_SUPERCLASSES, it);
		Collection<FerretObject> results = new ArrayList<FerretObject>();
		results.add(it);	// add ourselves in case we're the supertype of all the others
		results.addAll(supertypes.asCollection());
		return results;
	}

	@Override
	protected void processSolution(FerretObject e) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("supertype", e);
		addSolution(s);
	}

}
