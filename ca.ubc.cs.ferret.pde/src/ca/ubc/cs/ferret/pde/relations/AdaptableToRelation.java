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

import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;


public class AdaptableToRelation extends PossibleAdaptationsRelation {

	public AdaptableToRelation() {}

	@Override
	protected boolean checkFactory(IPluginModelBase pluginModel,
			IPluginExtension extension,
			IPluginAttribute adaptorFactoryClassAttribute,
			IPluginAttribute sourceTypeAttribute) {
		
		// If this spec adapts from one of our types, then report it
		return relevantTypeNames.contains(sourceTypeAttribute.getValue());
	}

	@Override
	protected boolean isRelevantSpecification(IPluginModelBase pluginModel,
			IPluginExtension extension, IPluginElement factoryElement,
			IPluginAttribute adaptorFactoryClassAttribute,
			IPluginAttribute sourceTypeAttribute,
			IPluginAttribute destinationTypeAttribute) {
		
		// If this spec adapts from one of our types, then report it
		// (Note: this shouldn't be necessary because of the test in checkFactory())
		return relevantTypeNames.contains(sourceTypeAttribute.getValue());
	}	
}
