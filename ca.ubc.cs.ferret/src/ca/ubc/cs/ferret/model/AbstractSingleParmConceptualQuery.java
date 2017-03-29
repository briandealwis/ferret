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

import java.util.Map;

public abstract class AbstractSingleParmConceptualQuery<T> extends
		AbstractConceptualQuery {
	protected T parameter;
	
	public AbstractSingleParmConceptualQuery() {
	}

	protected boolean validateParameter(T value) {
		return true;
	}
	
	public boolean setParameters(Map<String, Object[]> singletonMap) {
		assert singletonMap.size() == 1;
		assert singletonMap.containsKey(DEFAULT_PARAMETER);
		Object[] parms = singletonMap.get(DEFAULT_PARAMETER);
		assert parms.length == 1;
		parameter = (T)parms[0];	// assume that it's been verified from the declarative XML
		return validateParameter(parameter);
	}

}
