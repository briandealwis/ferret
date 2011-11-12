package ca.ubc.cs.ferret.util;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.FerretPlugin;

public abstract class AbstractClassifier<T,C> implements IClassifier<T,C> {

	public String getCategoryText(C category) {
		return FerretPlugin.compactPrettyPrint(category);
	}

	public ImageDescriptor getCategoryImage(C category) {
		return FerretPlugin.getImage(category);
	}
}
