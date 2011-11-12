package ca.ubc.cs.ferret.pde.classifiers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;

import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.pde.PdeModelHelper;

public class PluginObjectClassifier implements IClassifier<IPluginObject,IPluginModelBase>{

	public PluginObjectClassifier() {}

	public IPluginModelBase[] getCategories() {
		return null;
	}

	public IPluginModelBase getCategory(IPluginObject object) {
		return PdeModelHelper.getDefault().getDefiningModel(object);
	}

	public String getCategoryText(IPluginModelBase category) {
		return FerretPlugin.compactPrettyPrint(category);
	}

	public ImageDescriptor getCategoryImage(IPluginModelBase category) {
		return FerretPlugin.getImage(category);
	}
}
