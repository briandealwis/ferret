package ca.ubc.cs.ferret.jdt.ops;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class JdtIsInterfaceRelation extends AbstractToolRelation {
	protected boolean done = false;
	protected FerretObject fo;
	protected ConversionResult<IType> classes;

	public JdtIsInterfaceRelation() {}

	@Override
	protected boolean configure(FerretObject... arguments) {
		if (arguments.length != 1) { return false; }
		fo = arguments[0];
		classes = fo.convert(IType.class, 0, Fidelity.Approximate);
		return classes != null && classes.wasSuccessful();
	}

	public boolean hasNext() {
		if (done) { return false; }
		for (IType t : classes.getResults()) {
			try {
				if (t.isInterface()) { return true; }
			} catch (JavaModelException e) {
				JavaModelHelper.logJME(e);
			}
		}
		return false;
	}

	public FerretObject next() {
		done = true;
		fo.setPrimaryFidelity(fo.getPrimaryFidelity().least(classes.getFidelity()));
		return fo;
	}
}
