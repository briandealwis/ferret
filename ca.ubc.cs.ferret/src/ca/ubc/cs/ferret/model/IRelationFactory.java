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
package ca.ubc.cs.ferret.model;

import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IRelationFactory {
	
	/**
	 * Clone and configure this instance for the particular resolver and arguments.
	 * Return null if not supported.
	 * @param monitor
	 * @param resolver
	 * @param arguments
	 * @return configured instance
	 */
	public IRelation configure(IProgressMonitor monitor, IRelationResolver resolver, FerretObject... arguments);

	/**
	 * Clone and configure this instance for the particular resolver and arguments.
	 * Return null if not supported.
	 * @param monitor
	 * @param resolver
	 * @param arguments
	 * @return configured instance
	 */
	public IRelation configure(IProgressMonitor monitor, IRelationResolver resolver, Object... arguments);

}
