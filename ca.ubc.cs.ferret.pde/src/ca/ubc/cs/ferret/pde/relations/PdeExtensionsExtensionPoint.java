package ca.ubc.cs.ferret.pde.relations;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.model.IRelationFactory;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

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
