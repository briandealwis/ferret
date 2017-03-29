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
