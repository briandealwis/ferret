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
package ca.ubc.cs.ferret.kenyon;

/**
 * A source of SQL handles for describing some element.  The handles
 * are organized in stages, with successive stages being less precise.
 * Multiple keys can be proposed within a stage, useful for elements that
 * may be described using multiple handles.  
 */
public interface IKHandlesSource {

	/**
	 * Return an SQL expression to test whether a particular element's
	 * handle is described by the descriptions in the provided stage. 
	 * @param stage the stage to be used
	 * @param elementVarName the SQL expression for the element
	 * @return an SQL sub-query
	 */
	public String asSQL(int stage, String elementVarName);
	
	/**
	 * Return the total number of stages
	 * @return the total number of stages
	 */
	public int getNumberStages();
}
