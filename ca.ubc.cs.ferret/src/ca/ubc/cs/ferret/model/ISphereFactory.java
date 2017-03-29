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

import ca.ubc.cs.ferret.FerretConfigurationException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;

public interface ISphereFactory extends IAdaptable, Cloneable {
	/**
	 * Answer this factory type's unique identifier.
	 * @return unique identifier for this factory type
	 */
	public String getId();

	/**
	 * Answer this factory type's natural language description.
	 * @return natural language description
	 */
	public String getDescription();

	/**
	 * Return whether this factory has been configured and
	 * thus able to create a sphere.
	 * @return status of creation
	 */
	public IStatus canCreate();

	/**
	 * Create a sphere based on this factory's settings.
	 * @param monitor 
	 * @return
	 * @throws FerretConfigurationException
	 */
	public ISphere createSphere(IProgressMonitor monitor) throws FerretConfigurationException;

	public ImageDescriptor getImageDescriptor();

	public String getHelpContextId();
	
	public ISphereFactory clone();
}
