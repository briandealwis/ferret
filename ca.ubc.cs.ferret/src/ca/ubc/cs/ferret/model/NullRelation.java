package ca.ubc.cs.ferret.model;

import java.util.NoSuchElementException;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.types.FerretObject;

/**
 * Absorb all and release nothing.  Useful for testing, especially NamedJoinRelation's hasNext().
 * @author Brian de Alwis
 */
public class NullRelation extends AbstractToolRelation {

	public NullRelation() {}

	public boolean hasNext() {
		return false;
	}

	public FerretObject next() {
		throw new NoSuchElementException();
	}

	public IRelation configure(IProgressMonitor monitor,
			ISphere sphere, FerretObject... arguments) {
		return this;
	}
	
	protected boolean configure(FerretObject... arguments) {
		return true;
	}

}
