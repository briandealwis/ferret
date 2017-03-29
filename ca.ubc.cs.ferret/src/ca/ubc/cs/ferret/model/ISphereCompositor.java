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

import java.util.List;

/**
 * Implements a sphere composition function.
 * @author Brian de Alwis
 */
public interface ISphereCompositor extends ISphere {
	/**
	 * Add the provided sphere as a component of this compositor.
	 * @param t the provided sphere
	 */
	public void add(ISphere t);

	/**
	 * Remove the specified sphere as a component of this sphere.
	 * @param t the sphere to be removed
	 * @return true if <code>t</code> was a component, false if it was not
	 */
	public boolean remove(ISphere t);

	public List<ISphere> getComposedSpheres();
	
}
