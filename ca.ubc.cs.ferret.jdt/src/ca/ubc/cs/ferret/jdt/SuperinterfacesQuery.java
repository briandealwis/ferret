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
import org.eclipse.jdt.core.IType;

public class SuperinterfacesQuery extends
		JavaIntersectionConceptualQuery<IType,FerretObject> {

	public SuperinterfacesQuery() {}

	public String getSubDescription() {
		return "superinterfaces";
	}

	@Override
	protected Collection<FerretObject> performQuery(IType it, IProgressMonitor monitor) {
		IRelation op =
			getSphere().resolve(monitor, ObjectOrientedRelations.OP_SUPERINTERFACES, it);
		return op.asCollection();
	}

	@Override
	protected void processSolution(FerretObject iface) {
		SimpleSolution s = new SimpleSolution(this, iface);
		s.add("has-superinterface", iface);
//		for(IType t : elements) {
//			s.add(new StupidlySimpleRelation(iface, "implemented-by", t));
//		}
		addSolution(s);
	}

}
