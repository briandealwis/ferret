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

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.natures.PDE;

public class DefiningBundle extends PdeSingleParmConceptualQuery<IType> {

	public boolean validateParameter(IType type) {
		IProject project = type.getJavaProject().getProject();
		try {
			return project.hasNature(PDE.PLUGIN_NATURE)
					|| project.hasNature(PDE.FEATURE_NATURE);
		} catch(CoreException e) {
			FerretPlugin.log(e.getStatus());
			return false;
		}
	}

	public String getDescription() {
		return "defined in";
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		IPackageFragmentRoot root = (IPackageFragmentRoot) parameter.getAncestor(IType.PACKAGE_FRAGMENT_ROOT);
		IPluginModelBase plugin = null;
		PdeModelHelper modelHelper = PdeModelHelper.getDefault();
		// Could check the IJavaProject's classpath entries and verify it's
		// the Plugin Dependencies container
		if (root.isExternal()) {
			String rootLocation = root.getPath().toOSString();
			plugin = modelHelper.locatePluginModel(rootLocation);
		}
		if (plugin == null) {
			IProject project = parameter.getJavaProject().getProject();
			plugin = modelHelper.findPluginModel((IProject) project);
		}
		SimpleSolution s = new SimpleSolution(this, null);
		s.add("defined", plugin);
		s.setPrimaryEntityName("defined");
		addSolution(s);
	}
}
