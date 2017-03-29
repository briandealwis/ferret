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

public interface FerretErrorConstants {

	/**
	 * Unexpected values or circumstances.
	 */
	public static int VALIDATION_ERRORS = 10;
	
	/**
	 * Some component not adhering to contracts.
	 */
	public static int CONTRACT_VIOLATION = 11;

	/**
	 * An error or exception occurred during component configuration.
	 */
	public static int CONFIGURATION_ERROR = 12;

	/**
	 * Errors due to shut-down of Eclipse platform.
	 */
	public static int PLATFORM_NOT_RUNNING = 20;

	/**
	 * An unexpected occurrence at run-time
	 */
	public static int UNEXPECTED_RUNTIME_OCCURRENCE = 30;

	/* 200s indicate client code failure caught by Ferret core */
	public static int CLONE_FAILURE = 200;

	/* Errors occurring that have been handled by Ferret,
	 * reported for informational purposes 	 */
	public static int EXCEPTION_HANDLED = 400;


}
