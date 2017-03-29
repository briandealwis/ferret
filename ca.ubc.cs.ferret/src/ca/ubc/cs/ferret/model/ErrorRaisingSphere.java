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
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;

public class ErrorRaisingSphere extends AbstractSphere {

	public ErrorRaisingSphere(String d) {
		super(d);
	}

	public <T> T get(String key, Class<T> clazz) {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public ISphere getParent() {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public void setParent(ISphere p) {
		// but ignore
	}

	@Override
	public IRelation resolve(IProgressMonitor monitor, String relationName,
			Object... arguments) throws UnsupportedOperationException {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public IRelation resolve(IProgressMonitor monitor, String relationName,
			FerretObject... arguments) throws UnsupportedOperationException {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public AbstractRelationResolvingState createResolverState(AbstractRelationResolvingState parent) {
		throw new FerretFatalError("should not be used");
	}

	@Override
	public <T> void internalGetAll(String key, Class<T> cl,
			Map<ISphere, T> results) {
		throw new FerretFatalError("should not be used");
	}
}
