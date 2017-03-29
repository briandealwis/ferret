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
package ca.ubc.cs.ferret.pde.relations;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import java.util.Collection;
import org.eclipse.pde.core.plugin.IPluginModelBase;

public class PdePluginDeclaredExtensions extends
		AbstractCollectionBasedRelation<IPluginModelBase> {

	public PdePluginDeclaredExtensions() {}
	
	@Override
	protected Class<IPluginModelBase> getInputType() {
		return IPluginModelBase.class;
	}

	@Override
	protected Collection<?> realizeCollection(IPluginModelBase input) {
		return PdeModelHelper.getDefault().getExtensions(input).keySet();
	}

}
