package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class JdtIsFieldRelation extends AbstractToolRelation {
	protected boolean done = false;
	protected FerretObject fo;
	protected ConversionResult<IField> fields;

	public JdtIsFieldRelation() {}

	@Override
	protected boolean configure(FerretObject... arguments) {
		if (arguments.length != 1) { return false; }
		fo = arguments[0];
		fields = fo.convert(IField.class, 0, Fidelity.Approximate);
		return fields != null && fields.wasSuccessful();
	}

	public boolean hasNext() {
		if (done) { return false; }
		return !fields.getResults().isEmpty();
	}

	public FerretObject next() {
		done = true;
		fo.setPrimaryFidelity(fo.getPrimaryFidelity().least(fields.getFidelity()));
		return fo;
	}
}
