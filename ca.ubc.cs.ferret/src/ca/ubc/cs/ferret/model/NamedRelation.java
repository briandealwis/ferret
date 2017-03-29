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

import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;

public class NamedRelation implements IRelationFactory {
	protected String relationName;

	public NamedRelation(String name) {
		relationName = name;
	}
	
	public IRelation configure(IProgressMonitor monitor,
			IRelationResolver resolver, FerretObject... arguments) {
		IRelation top;
		try {
			top = resolver.topPerform(monitor, relationName, arguments);
		} catch(UnsupportedOperationException e) { return null; }
		return top;
	}

	public IRelation configure(IProgressMonitor monitor,
			IRelationResolver resolver, Object... arguments) {
		return configure(monitor, resolver, 
				FerretObject.wrap(arguments, Fidelity.Exact, resolver.getRootSphere()));
	}
	
}
