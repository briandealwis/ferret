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

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class SiblingsQuery extends JavaRelatedConceptualQuery<IType> {
	
	public SiblingsQuery() {}

	@Override
	public boolean validateParameter(IType type) {
		try {
			return type.isClass();
		} catch (JavaModelException e) {
			FerretPlugin.log(new Status(IStatus.ERROR, FerretJdtPlugin.pluginID, FerretErrorConstants.EXCEPTION_HANDLED,
				"unexpected JME", e));
			return false;
		}
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		monitor.beginTask("Finding siblings of " + FerretPlugin.prettyPrint(parameter), 2);
		try {
			IRelation siblings =
				getSphere().resolve(monitor, ObjectOrientedRelations.OP_SIBLINGS, parameter);

			for(FerretObject sub : siblings) {
				SimpleSolution s = new SimpleSolution(this, parameter);
				s.add("sibling", sub);
				s.setPrimaryEntityName("sibling");
				addSolution(s);
			}
		} finally {
			monitor.done();
		}
	}

	public String getDescription() {
		return "siblings";
	}

}
