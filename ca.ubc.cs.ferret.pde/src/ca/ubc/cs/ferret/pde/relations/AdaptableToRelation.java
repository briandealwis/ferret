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
