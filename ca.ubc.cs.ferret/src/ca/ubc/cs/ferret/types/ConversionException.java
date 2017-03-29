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
package ca.ubc.cs.ferret.types;

@SuppressWarnings("serial")
public class ConversionException extends Exception {
	protected Object relatedObject;
	
	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(String message, Object obj) {
		super(message);
		relatedObject = obj;
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

	public Object getRelatedObject() {
		return relatedObject;
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
}
