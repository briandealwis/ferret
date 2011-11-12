package ca.ubc.cs.ferret.model;

import ca.ubc.cs.ferret.FerretFatalError;

public abstract class AbstractSphereFactory implements ISphereFactory {

	@Override
	public ISphereFactory clone() {
		try {
			return (ISphereFactory)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new FerretFatalError("clone failed!", e);
		}
	}

}
