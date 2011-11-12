package ca.ubc.cs.ferret.types;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.display.DwObject;
import ca.ubc.cs.ferret.display.IPrettyPrinter;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class DwFerretObject extends DwObject<FerretObject> {
	IPrettyPrinter printer;
	
	public DwFerretObject(FerretObject _object) {
		super(_object, _object.getSphere());
		printer = _object.getAdapter(IPrettyPrinter.class, Fidelity.Approximate);
	}

	@Override
	public ImageDescriptor getImage() {
		if(printer != null) { return printer.getImage(); }
		return FerretPlugin.getImage(object.getPrimaryObject());
	}

	@Override
	public String getText() {
		if(printer != null) { return printer.getText(); }
		return FerretPlugin.prettyPrint(object.getPrimaryObject());
	}
}
