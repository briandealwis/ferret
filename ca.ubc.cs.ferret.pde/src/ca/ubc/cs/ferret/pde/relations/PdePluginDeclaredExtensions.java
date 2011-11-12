package ca.ubc.cs.ferret.pde.relations;

import java.util.Collection;

import org.eclipse.pde.core.plugin.IPluginModelBase;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

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
