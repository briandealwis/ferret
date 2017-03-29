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
package ca.ubc.cs.ferret.tests;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import java.util.Iterator;

public class CollectionIterationRelation extends
		AbstractToolRelation {

	protected Iterator<FerretObject> iter;
	
	public CollectionIterationRelation() {}

	@SuppressWarnings("unchecked")
	protected boolean configure(FerretObject... arguments) {
		if(arguments.length == 0) { return false; }
		Collection<FerretObject> c;
		if(arguments.length == 1 && arguments[0] instanceof Collection) {
			c = FerretObject.wrap((Collection<?>)arguments[0], Fidelity.Exact, resolver.getRootSphere());
		} else {
			c = FerretObject.wrapAsCollection(arguments, Fidelity.Exact, resolver.getRootSphere());
		}
		iter = c.iterator();
		return true;
	}

	public boolean hasNext() {
		return iter.hasNext();
	}

	public FerretObject next() {
		return iter.next();
	}

}
