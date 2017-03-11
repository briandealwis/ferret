/*******************************************************************************
 * Copyright (c) 2017 Manumitting Technologies Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manumitting Technologies Inc - initial API and implementation
 *******************************************************************************/

package ca.ubc.cs.ferret.pde.ui;

import ca.ubc.cs.ferret.jdt.JdtSphereFactory;
import ca.ubc.cs.ferret.pde.PdeSphereFactory;
import ca.ubc.cs.ferret.sphereconfig.ReplacementSphereCompositorFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class JdtPdeSphereFactory implements IExecutableExtensionFactory {
	@Override
	public Object create() throws CoreException {
		ReplacementSphereCompositorFactory root = new ReplacementSphereCompositorFactory();
		root.add(new JdtSphereFactory());
		root.add(new PdeSphereFactory());
		return root;
	}
}
