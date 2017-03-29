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

import ca.ubc.cs.ferret.model.ISphereCompositor;
import ca.ubc.cs.ferret.model.UnioningSphereCompositor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class UnioningSphereFactory extends
		AbstractSphereCompositorFactory {

	public UnioningSphereFactory() {}

	@Override
	public String getDescription() {
		return "union";
	}

	@Override
	public IStatus canCreateCompositor() {
		// we allow a union with only a single element
		return Status.OK_STATUS;
	}

	protected ISphereCompositor createCompositor() {
		return new UnioningSphereCompositor();
	}

}
