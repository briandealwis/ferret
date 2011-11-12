package ca.ubc.cs.ferret.pde.relations;

import java.util.Collection;

import org.eclipse.pde.core.plugin.IPluginExtensionPoint;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.model.IRelationFactory;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class PdeExtensionPointExtensions extends
		AbstractCollectionBasedRelation<IPluginExtensionPoint> implements IRelationFactory {

	public PdeExtensionPointExtensions() {}

	@Override
	protected Class<IPluginExtensionPoint> getInputType() {
		return IPluginExtensionPoint.class;
	}

	@Override
	protected Collection<?> realizeCollection(IPluginExtensionPoint input) {
		return PdeModelHelper.getDefault().getExtensions(input).keySet();
	}

}
