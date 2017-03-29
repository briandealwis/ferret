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
import org.eclipse.jdt.core.JavaModelException;

public class SubinterfacesQuery extends
		JavaIntersectionConceptualQuery<IType,FerretObject> {

	public SubinterfacesQuery() {}

	public String getSubDescription() {
		return "subinterfaces";
	}

	@Override
	protected boolean validateParameter(IType value) {
		try {
			return value.isInterface();
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return false;
		}
	}

	@Override
	protected Collection<FerretObject> performQuery(IType iface, IProgressMonitor monitor) {
		IRelation op =
			getSphere().resolve(monitor,ObjectOrientedRelations.OP_SUBINTERFACES, iface);
		return op.asCollection();
	}

	@Override
	protected void processSolution(FerretObject subiface) {
		SimpleSolution s = new SimpleSolution(this, subiface);
		s.add("subinterface", subiface);
		addSolution(s);		
	}


}
