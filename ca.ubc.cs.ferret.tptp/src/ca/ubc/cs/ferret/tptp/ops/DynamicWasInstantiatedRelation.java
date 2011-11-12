package ca.ubc.cs.ferret.tptp.ops;

import org.eclipse.hyades.models.trace.TRCClass;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class DynamicWasInstantiatedRelation extends AbstractToolRelation {

	protected boolean done = false;
	protected FerretObject fo;
	protected ConversionResult<TRCClass> classes;
	
	public DynamicWasInstantiatedRelation() {}

	@Override
	protected boolean configure(FerretObject... arguments) {
		if(arguments.length != 1) { return false; }
		fo = arguments[0];
		classes = fo.convert(TRCClass.class, 0, Fidelity.Approximate);
		return classes != null;
	}

	public boolean hasNext() {
		if(done) { return false; }
		// if there are any conversions at all, then it must have been instantiated!
		return !classes.getResults().isEmpty();
	}

	public FerretObject next() {
		done = true;
		fo.setPrimaryFidelity(fo.getPrimaryFidelity().least(classes.getFidelity()));
		return fo;
	}
}
