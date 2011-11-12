package ca.ubc.cs.ferret.display;

import org.eclipse.jface.resource.ImageDescriptor;

public class DwText extends DwBaseObject {
	protected String text;
	protected ImageDescriptor image;
	
	public DwText(IDisplayObject parent, String text) {
		this(parent, text, null);
	}

	public DwText(IDisplayObject parent, String text,
			ImageDescriptor image) {
		super(parent);
		this.text = text;
		this.image = image;
	}

	public Object getObject() {
		return text;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	protected void buildChildren(IDisplayObject[] oldChildren) {
		/* we don't have children */
		children = new IDisplayObject[0];
	}

	@Override
	public ImageDescriptor getImage() {
		return image;
	}
}
