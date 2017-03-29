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
package ca.ubc.cs.ferret.views;

public interface DossierConstants {

	public final static int NUMBER_COLUMNS = 4;
	public final static int COLUMN_DESCRIPTION = 0;
	public final static int COLUMN_CATEGORY = 1;
	public final static int COLUMN_ELEMENT_COUNT = 2;
	public final static int COLUMN_CLUSTERING = 3;

	/**
	 * Identifies a conceptual query
	 */
	public final static String IMG_QUERY = "IMG_QUERY";
	
	/**
	 * Identifies a query in progress (meant to be composited)
	 */
	public final static String IMG_HOURGLASS = "IMG_HOURGLASS";
	public static final String IMG_QUERY_IN_PROGRESS = "IMG_QUERY_IN_PROGRESS";
}
