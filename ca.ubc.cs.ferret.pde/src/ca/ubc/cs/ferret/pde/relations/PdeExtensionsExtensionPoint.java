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
import ca.ubc.cs.ferret.model.IRelationFactory;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;

public class PdeExtensionsExtensionPoint extends
		AbstractCollectionBasedRelation<IPluginExtension> implements IRelationFactory {

	public PdeExtensionsExtensionPoint() {}

	@Override
	protected Class<IPluginExtension> getInputType() {
		return IPluginExtension.class;
	}

	@Override
	protected Collection<?> realizeCollection(IPluginExtension input) {
		IPluginExtensionPoint point = PdeModelHelper.getDefault().findExtensionPoint(input.getPoint());
		if(point == null) { return Collections.emptyList(); }
		return Collections.singletonList(point);
	}

}
