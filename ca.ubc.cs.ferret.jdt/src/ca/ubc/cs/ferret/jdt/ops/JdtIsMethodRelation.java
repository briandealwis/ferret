package ca.ubc.cs.ferret.jdt.ops;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class JdtIsMethodRelation extends AbstractToolRelation {
	protected boolean done = false;
	protected FerretObject fo;
	protected ConversionResult<IMethod> methods;

	public JdtIsMethodRelation() {}

	@Override
	protected boolean configure(FerretObject... arguments) {
		if (arguments.length != 1) { return false; }
		fo = arguments[0];
		methods = fo.convert(IMethod.class, 0, Fidelity.Approximate);
		return methods != null && methods.wasSuccessful();
	}

	public boolean hasNext() {
		if (done) { return false; }
		return !methods.getResults().isEmpty();
	}

	public FerretObject next() {
		done = true;
		fo.setPrimaryFidelity(fo.getPrimaryFidelity().least(methods.getFidelity()));
		return fo;
	}

}
