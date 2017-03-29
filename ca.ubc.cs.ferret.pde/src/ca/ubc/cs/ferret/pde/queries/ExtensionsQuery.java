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
package ca.ubc.cs.ferret.pde.queries;

import ca.ubc.cs.ferret.model.IConceptualQuery;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeSphereHelper;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;

public class ExtensionsQuery extends PdeSingleParmConceptualQuery<FerretObject> 
		implements IConceptualQuery {

	public ExtensionsQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IRelation extensions = getSphere().resolve(monitor, PdeSphereHelper.OP_EXTENDED_BY, parameter);
		for(FerretObject result : extensions) {
			SimpleSolution s = new SimpleSolution(this, this);
			s.setPrimaryEntityName("extension");
			s.add("extension", result);
			addSolution(s);
		}
	}

	public String getDescription() {
		return "extensions";
	}

	@Override
	protected boolean validateParameter(FerretObject value) {
		return value.getAdapter(IPluginExtensionPoint.class, Fidelity.Approximate) != null;
	}
}
