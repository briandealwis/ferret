/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
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
