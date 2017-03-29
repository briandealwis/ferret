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

import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractSphere implements ISphere {
	protected ISphere parent;
	protected String description;

	public AbstractSphere() {}

	public AbstractSphere(String d) {
		description = d;
	}

	public String getDescription() {
		return description;
	}

	public IRelation resolve(IProgressMonitor monitor, String relationName,
			Object... arguments) throws UnsupportedOperationException {
		return resolve(monitor, relationName, FerretObject.wrap(arguments, Fidelity.Exact, this));
	}

	public IRelation resolve(IProgressMonitor monitor,
			String relationName, FerretObject... arguments) {
		ISphere top = this;
		while(top.getParent() != null) { top = top.getParent(); }
		AbstractRelationResolvingState state = top.createResolverState(null);
		return state.process(monitor, relationName, arguments);
	}

    public abstract AbstractRelationResolvingState createResolverState(AbstractRelationResolvingState parent);

	public ISphere getParent() {
		return parent;
	}

	public void setParent(ISphere parent) {
		this.parent = parent;
	}
	
	public ISphere getRootSphere() {
		ISphere root = AbstractSphere.this;
		while(root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}
	
	public <T> Map<ISphere, T> getAll(String key, Class<T> cl) {
		Map<ISphere, T> results = new HashMap<ISphere, T>();
		((AbstractSphere)getRootSphere()).internalGetAll(key, cl, results);
		return results;
	}

	public abstract <T> void internalGetAll(String key, Class<T> cl, Map<ISphere, T> results);
	
	public abstract class AbstractRelationResolvingState implements IRelationResolver, Cloneable {

		protected AbstractRelationResolvingState parent;
		protected IRelation result;
		protected boolean finished = false;
		
		/* the following are transient, and while they should be kept up-to-date, they should not be relied upon */ 
		protected String relationName;
		protected FerretObject arguments[];

		public AbstractRelationResolvingState(AbstractRelationResolvingState parent) {
			this.parent = parent;
		}

		public ISphere getSphere() {
			return AbstractSphere.this;
		}

		public ISphere getRootSphere() {
			return AbstractSphere.this.getRootSphere();
		}

		/**
		 * Restore state as if we were starting resolution from scratch from this sphere.
		 */
		protected void reset() {
			finished = false;
			result = null;
		}

		/**
		 * Have we reached the end of resolving? 
		 * @return true if the resolving is finished
		 */
		public boolean finished() {
			return result != null || (finished && parent == null);
		}

		public <T> T continueGet(String name, Class<T> clazz) {
			throw new FerretFatalError("to be implemented");
		}

		public Object clone() {
			try {
				AbstractRelationResolvingState clone = (AbstractRelationResolvingState)super.clone();
				if(parent != null) {
					clone.parent = (AbstractRelationResolvingState)parent.clone();
				}
				return clone;
			} catch (CloneNotSupportedException e) {
				throw new FerretFatalError(e);
			}
		}
		
		public IRelation continuePerform(IProgressMonitor monitor, FerretObject... args) {
			AbstractRelationResolvingState clone = (AbstractRelationResolvingState)clone();
			clone.finished = false;
			result = null;
			return clone.process(monitor, relationName, args);
		}

		public IRelation continuePerform(IProgressMonitor monitor,
				String relationName, FerretObject... args) {
			AbstractRelationResolvingState clone = (AbstractRelationResolvingState)clone();
			clone.reset();
			return clone.process(monitor, relationName, args);
		}

		public <T> T topGet(String name, Class<T> clazz) {
			throw new FerretFatalError("to be implemented");
		}

		public IRelation topPerform(IProgressMonitor monitor, String relationName,
				FerretObject... arguments) {
			return resolve(monitor, relationName, arguments);
		}

		/**
		 * @return the result, or null if a relation was not able to be resolved.
		 */
		public IRelation getResult() {
			return result;
		}
		
		/**
		 * Return the next resolution step.  Be sure to stash the provided arguments somewhere.
		 * @param monitor
		 * @param relationName
		 * @param arguments
		 * @return the state representing the next resolving state
		 */
		final public AbstractRelationResolvingState next(IProgressMonitor monitor, String relationName, FerretObject... arguments) {
			this.relationName = relationName;
			this.arguments = arguments;
			return next(monitor);
		}

		protected abstract AbstractRelationResolvingState next(IProgressMonitor monitor);

		public IRelation process(IProgressMonitor monitor, String relationName, FerretObject... arguments) {
			AbstractRelationResolvingState state = this;
			do {
				state = state.next(monitor, relationName, arguments);
			} while(!state.finished());
			if(state.getResult() == null) {
				throw new UnsupportedOperationException("Registered relation: \"" 
						+ relationName + "\" in \"" + getDescription() + "\" unable to support arguments " 
						+ FerretPlugin.prettyPrint(arguments));
			}
			return state.getResult();
		}
	}


}
