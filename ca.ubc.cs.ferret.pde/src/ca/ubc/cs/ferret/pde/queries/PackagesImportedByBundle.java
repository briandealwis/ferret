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

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.eclipse.pde.core.plugin.IPluginModelBase;

public class PackagesImportedByBundle extends
		AbstractIntersectionConceptualQuery<IPluginModelBase, ImportPackageSpecification> {

	@Override
	protected String getSubDescription() {
		return "imports packages";
	}

	@Override
	protected Collection<ImportPackageSpecification> performQuery(
			IPluginModelBase bundle, IProgressMonitor monitor) {
		return Arrays.asList(bundle.getBundleDescription().getImportPackages());
	}

	@Override
	protected void processSolution(ImportPackageSpecification dependent) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("imports", dependent);
		s.setPrimaryEntityName("imports");
		addSolution(s);
	}

	public boolean isValid() {
		return false;
	}

}
