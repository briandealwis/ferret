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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A sphere where constituent spheres are consulted  in FIFO order, and 
 * the first sphere providing a matching operation is the one used.
 */
public class ReplacementSphereCompositor extends AbstractSphereCompositor {

	public ReplacementSphereCompositor() {}

	public ReplacementSphereCompositor(ISphere... spheres) {
		super(spheres);
	}

	@Override
	public AbstractRelationResolvingState createResolverState(AbstractRelationResolvingState parent) {
		return new ReplacementResolvingState(parent);
	}

	protected class ReplacementResolvingState extends AbstractRelationResolvingState {
		protected int index = 0;
		protected AbstractRelationResolvingState current;

		public ReplacementResolvingState(AbstractRelationResolvingState parent) {
			super(parent);
		}

		@Override
		protected void reset() {
			super.reset();
			current = null;
			index = 0;
		}

		@Override
		public AbstractRelationResolvingState next(IProgressMonitor monitor) {
			if(finished) { return parent; }

			if(current == null) {
				AbstractSphere sp = (AbstractSphere)spheres.get(index++);
				current = sp.createResolverState(null);
			}
			if(!current.finished()) {
				current = current.next(monitor, relationName, arguments);
				return this;
			}
			if(current.getResult() != null) {
				result = current.getResult();
				finished = true;
				return this;
			}
			current = null;
			finished = index >= spheres.size();
			return this;
		}
	}
}
