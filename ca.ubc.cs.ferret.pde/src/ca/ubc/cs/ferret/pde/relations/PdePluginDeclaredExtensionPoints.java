package ca.ubc.cs.ferret.pde.relations;

import java.util.Collection;

import org.eclipse.pde.core.plugin.IPluginModelBase;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class PdePluginDeclaredExtensionPoints 
		extends AbstractCollectionBasedRelation<IPluginModelBase> {

	public PdePluginDeclaredExtensionPoints() {}

	@Override
	protected Class<IPluginModelBase> getInputType() {
		return IPluginModelBase.class;
	}

	@Override
	protected Collection<?> realizeCollection(IPluginModelBase input) {
		return PdeModelHelper.getDefault().getExtensionPoints(input);
	}

}
