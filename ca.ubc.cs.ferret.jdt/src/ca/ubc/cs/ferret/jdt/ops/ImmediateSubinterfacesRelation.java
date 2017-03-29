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
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class ImmediateSubinterfacesRelation extends AbstractCollectionBasedRelation<IType> {

	public ImmediateSubinterfacesRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		return Arrays.asList(JavaModelHelper.getDefault().getSubinterfaces(input, monitor));
	}

	@Override
	protected IType checkInput(IType input) {
		try {
			return input.isInterface() ? input : null; 
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
		    return null;
		}   
	}
}
