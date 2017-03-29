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

import ca.ubc.cs.ferret.model.AbstractSphere.AbstractRelationResolvingState;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;

public interface ISphere {

	/**
	 * Perform a relation by the given name. Arguments will be converted to
	 * instances of {@link FerretObject} with fidelity of {@link Fidelity.Exact}
	 * 
	 * @param monitor
	 * @param relationName
	 * @param arguments
	 * @return the operation, encoding the results
	 * @throws UnsupportedOperationException
	 */
	public IRelation resolve(IProgressMonitor monitor, 
			String relationName, Object... arguments) throws UnsupportedOperationException;


	/**
	 * Perform a relation by the given name.
	 * @param monitor
	 * @param relationName
	 * @param arguments
	 * @return the operation, encoding the results
	 * @throws UnsupportedOperationException
	 */
	public IRelation resolve(IProgressMonitor monitor, 
			String relationName, FerretObject... arguments) throws UnsupportedOperationException;

	public String getDescription();
	
	/**
	 * Lookup parameter with provided name.  Return null if no such parameter. 
	 * @param key the parameter name
	 * @return the associated parameter, or null if not present
	 */
	public <T> T get(String key, Class<T> clazz);

	/**
	 * Find all parameters with provided name within the sphere network. 
	 * @param key the parameter name
	 * @return the associated parameters
	 */
	public <T> Map<ISphere,T> getAll(String key, Class<T> clazz);



	/*** The remaining methods are not meant for public consumption ***/
	

	/**
	 * Return this sphere's parent sphere. 
	 * @return the parent
	 */
	public ISphere getParent();

	
	/**
	 * Set this sphere's parent sphere. 
	 */
	public void setParent(ISphere parent);


	public AbstractRelationResolvingState createResolverState(AbstractRelationResolvingState parent);
	
}
