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
package ca.ubc.cs.ferret.jdt.ops;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class SuperclassRelation extends AbstractCollectionBasedRelation<IType> {
	
	@Override
	protected IType checkInput(IType input) {
		try {
			return input.isClass() ? input : null;
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return null;
		}
	}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		Collection<IType> results = new LinkedList<IType>();
		IType supercl = JavaModelHelper.getDefault().getSuperclass(input, monitor);
		if(supercl != null) { results.add(supercl); }
		return results;
	}
}
