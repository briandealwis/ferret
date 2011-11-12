package ca.ubc.cs.ferret.model;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.types.FerretObject;

public class ErrorRaisingSphere extends AbstractSphere {

	public ErrorRaisingSphere(String d) {
		super(d);
	}

	public <T> T get(String key, Class<T> clazz) {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public ISphere getParent() {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public void setParent(ISphere p) {
		// but ignore
	}

	@Override
	public IRelation resolve(IProgressMonitor monitor, String relationName,
			Object... arguments) throws UnsupportedOperationException {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public IRelation resolve(IProgressMonitor monitor, String relationName,
			FerretObject... arguments) throws UnsupportedOperationException {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public AbstractRelationResolvingState createResolverState(AbstractRelationResolvingState parent) {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public <T> void internalGetAll(String key, Class<T> cl,
			Map<ISphere, T> results) {
		throw new FerretFatalError("should not be used");
	}
}
