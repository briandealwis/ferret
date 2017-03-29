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

public class DifferencingSphereCompositor extends FunctionalSphereCompositor {

	@Override
	public AbstractRelationResolvingState createResolverState(
			AbstractRelationResolvingState parent) {
		return new DifferencingResolvingState(parent);

	}

	public DifferencingSphereCompositor() {
	}

	public DifferencingSphereCompositor(ISphere... spheres) {
		super(spheres);
	}

	@Override
	protected RelationalFunction newComposingOperation() {
		return new DifferencingOperation();
	}
	
	public class DifferencingResolvingState extends FunctionalResolvingState {

		@Override
		protected boolean checkResult() {
			// for differencing, the first sphere *must* return something, otherwise
			// there is nothing to difference against
			return index != 1 || current.getResult() != null;
		}

		public DifferencingResolvingState(AbstractRelationResolvingState parent) {
			super(parent);
		}

	}

}
