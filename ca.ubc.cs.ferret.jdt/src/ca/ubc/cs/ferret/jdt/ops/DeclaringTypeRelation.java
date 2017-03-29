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

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;

public class DeclaringTypeRelation extends AbstractCollectionBasedRelation<IMember>{

	@Override
	protected Class<IMember> getInputType() {
		return IMember.class;
	}

	@Override
	protected Collection<?> realizeCollection(IMember input) {
		LinkedList<IType> declaringType = new LinkedList<IType>();
		declaringType.add(input.getDeclaringType());
		return declaringType;
	}
}
