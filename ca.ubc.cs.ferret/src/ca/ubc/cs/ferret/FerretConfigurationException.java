package ca.ubc.cs.ferret;

import org.eclipse.core.runtime.IStatus;

@SuppressWarnings("serial")
public class FerretConfigurationException extends Exception {
	protected IStatus status;
	
	public FerretConfigurationException(IStatus status) {
		this.status = status;
	}

	public IStatus getStatus() {
		return status;
	}

	@Override
	public Throwable getCause() {
		if(status != null && status.getException() != null) { return status.getException(); }
		return super.getCause();
	}

	@Override
	public String getMessage() {
		if(status != null && status.getMessage() != null) { return status.getMessage(); }
		return super.getMessage();
	}

}
