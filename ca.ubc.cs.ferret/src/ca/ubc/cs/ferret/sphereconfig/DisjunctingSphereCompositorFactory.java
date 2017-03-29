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
package ca.ubc.cs.ferret.sphereconfig;

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.DisjunctingSphereCompositor;
import ca.ubc.cs.ferret.model.ISphereCompositor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class DisjunctingSphereCompositorFactory extends
		AbstractSphereCompositorFactory {

	public DisjunctingSphereCompositorFactory() {}

	@Override
	public String getDescription() {
		return "disjunction";
	}

	@Override
	public IStatus canCreateCompositor() {
		if(sphereFactories.size() > 1) { return Status.OK_STATUS; }
		return new Status(IStatus.ERROR, FerretPlugin.pluginID, 
				FerretErrorConstants.VALIDATION_ERRORS,
				"Disjunction requires at least two spherees", null);
	}

	protected ISphereCompositor createCompositor() {
		return new DisjunctingSphereCompositor();
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
