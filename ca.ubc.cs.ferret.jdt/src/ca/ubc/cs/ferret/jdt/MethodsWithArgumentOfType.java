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

import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

public class MethodsWithArgumentOfType
		extends JavaIntersectionConceptualQuery<IType,FerretObject> {

	public MethodsWithArgumentOfType() {
		super();
	}

	public String getDescription() {
		return "methods with arguments of type";
	}

	public String getSubDescription() {
		throw new FerretFatalError("method should never be called");
	}

	@Override
	protected void processSolution(FerretObject method) {
		SimpleSolution s = new SimpleSolution(this, method);
		s.add("has-argument", method);
		addSolution(s);
	}

	@Override
	protected Collection<FerretObject> performQuery(IType it, IProgressMonitor monitor) {
		IRelation op =
			getSphere().resolve(monitor, ObjectOrientedRelations.OP_METHODS_WITH_ARGUMENT_OF_TYPE, it);
		return op.asCollection();
	}

}
