package ca.ubc.cs.ferret.tptp.ops;

import org.eclipse.hyades.models.trace.TRCMethod;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class DynamicWasInvokedRelation extends AbstractToolRelation {
	protected boolean done = false;
	protected FerretObject fo;
	protected ConversionResult<TRCMethod> methods;
	
	public DynamicWasInvokedRelation() {}

	@Override
	protected boolean configure(FerretObject... arguments) {
		if(arguments.length != 1) { return false; }
		fo = arguments[0];
		methods = fo.convert(TRCMethod.class, 0, Fidelity.Approximate);
		return methods != null;
	}

	public boolean hasNext() {
		if(done) { return false; }
		for(TRCMethod method : methods.getResults()) {
			if(!method.getInvocations().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public FerretObject next() {
		done = true;
		fo.setPrimaryFidelity(fo.getPrimaryFidelity().least(methods.getFidelity()));
		return fo;
	}

}
