package ca.ubc.cs.ferret.display;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.FerretPlugin;

public class DelegatingPrettyPrinter implements IPrettyPrinter {
	protected Object object;
	
	public DelegatingPrettyPrinter(Object o) {
		object = o;
	}

	public ImageDescriptor getImage() {
		return FerretPlugin.getImage(object);
	}

	public String getText() {
		return FerretPlugin.prettyPrint(object);
	}
}
