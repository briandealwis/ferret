package ca.ubc.cs.ferret.pde.relations;

import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;

public class AdaptableFromRelation extends PossibleAdaptationsRelation {

	public AdaptableFromRelation() {
	}

	@Override
	protected boolean checkFactory(IPluginModelBase pluginModel,
			IPluginExtension extension,
			IPluginAttribute adaptorFactoryClassAttribute,
			IPluginAttribute sourceTypeAttribute) {
		
		// gotta check all factories
		return true;
	}

	@Override
	protected boolean isRelevantSpecification(IPluginModelBase pluginModel,
			IPluginExtension extension, IPluginElement factoryElement,
			IPluginAttribute adaptorFactoryClassAttribute,
			IPluginAttribute sourceTypeAttribute,
			IPluginAttribute destinationTypeAttribute) {
		
		// If this spec can adapt to one of our types, then report it
		return relevantTypeNames.contains(destinationTypeAttribute.getValue());
	}	

}
