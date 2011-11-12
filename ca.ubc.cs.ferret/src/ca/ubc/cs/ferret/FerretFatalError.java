package ca.ubc.cs.ferret;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class FerretFatalError extends RuntimeException {
	protected IStatus status;
	
	public FerretFatalError(String message) {
		super(message);
	}

	public FerretFatalError(Throwable cause) {
		super(cause);
	}

	public FerretFatalError(String message, Throwable cause) {
		super(message, cause);
	}

	public FerretFatalError(String message, Status status) {
		super(message);
		this.status = status;
	}

	public FerretFatalError(Status status) {
		this(status.getMessage(), status);
	}
	
	public IStatus getStatus() {
		return status;
	}

}
