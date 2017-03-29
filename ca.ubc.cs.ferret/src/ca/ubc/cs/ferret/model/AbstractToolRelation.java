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

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;

public abstract class AbstractToolRelation extends AbstractRelation
		implements IRelationFactory, Cloneable {
	protected IRelationResolver resolver;
	protected IProgressMonitor monitor;
	
	/**
	 * Required 0-argument constructor for creation from extension specification.
	 */
	public AbstractToolRelation() {
	}

	public IRelation configure(IProgressMonitor monitor,
			IRelationResolver resolver, FerretObject... arguments) {
		if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		try {
			AbstractToolRelation op = (AbstractToolRelation)clone();
			op.init();
			op.resolver = resolver;
			op.monitor = monitor;
			return op.configure(arguments) ? op : null;
		} catch(CloneNotSupportedException e) {
			FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID ,
					FerretErrorConstants.CLONE_FAILURE,
					"failure to clone class " + getClass().getName(), e));
			return null;
		} catch(UnsupportedOperationException e) {
			FerretPlugin.log(new Status(IStatus.INFO, FerretPlugin.pluginID ,
					FerretErrorConstants.CONFIGURATION_ERROR,
					"Could not configure relation " + getClass().getName(), e));
			return null;
		}
	}

	/**
	 * Perform any initialization necessary for this instance.  Since instances of
	 * AbstractToolRelation are created by cloning, any fields are shared upon
	 * clone.  Subclasses should implement this method to perform any initialization
	 * as necessary.
	 */
	protected void init() {
	}

	public IRelation configure(IProgressMonitor monitor,
			IRelationResolver resolver, Object... arguments) {
		return configure(monitor, resolver, FerretObject.wrap(arguments, Fidelity.Exact, resolver.getRootSphere()));
	}
	
	/**
	 * Configure the instance with the provided arguments.  Return true if this
	 * instance can handle the provided arguments, or false otherwise.
	 * @param arguments
	 * @return
	 */
	protected abstract boolean configure(FerretObject... arguments);
		
	public Collection<FerretObject> asCollection() {
		if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		return super.asCollection();
	}
	
	public <T> Collection<T> asCollection(Class<T> resultType,
			Collection<Fidelity> assessedFidelity) {
		if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		return super.asCollection(resultType, assessedFidelity);
	}
}
