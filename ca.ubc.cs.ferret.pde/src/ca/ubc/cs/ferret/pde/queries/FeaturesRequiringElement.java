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
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;

public class FeaturesRequiringElement extends
		AbstractIntersectionConceptualQuery<IModel, IFeatureModel> {

	@Override
	protected String getSubDescription() {
		return "required by";
	}

	@Override
	protected Collection<IFeatureModel> performQuery(IModel model,
			IProgressMonitor monitor) {
		return PdeModelHelper.getDefault().getFeaturesRequiring(model);
	}

	@Override
	protected void processSolution(IFeatureModel dependent) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("required by", dependent);
		s.setPrimaryEntityName("required by");
		addSolution(s);
	}

	public boolean isValid() {
		return false;
	}

}
