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

import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeSphereHelper;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;

public class DeclaredExtensionsQuery extends
		PdeSingleParmConceptualQuery<FerretObject> {

	public DeclaredExtensionsQuery() {}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		monitor.beginTask("Finding extensions of "
				+ FerretPlugin.prettyPrint(parameter), 10);
		monitor.subTask("Querying extensions");
		IRelation exts = getSphere().resolve(monitor, 
				PdeSphereHelper.OP_DECLARED_EXTENSIONS, parameter);
		for(FerretObject extension : exts) {
            SimpleSolution s = new SimpleSolution(this, null);
            s.add("extension", extension);
            s.add(new StupidlySimpleRelation(parameter, "defines-extension", extension));
            s.setPrimaryEntityName("extension");
            addSolution(s);
		}
	}

	public String getDescription() {
		return "declared extensions";
	}


}
