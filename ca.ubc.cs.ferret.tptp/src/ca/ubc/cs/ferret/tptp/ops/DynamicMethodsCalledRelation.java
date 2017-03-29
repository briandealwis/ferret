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
package ca.ubc.cs.ferret.tptp.ops;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.models.trace.TRCMethodInvocation;

public class DynamicMethodsCalledRelation extends AbstractTptpCollectionBasedRelation<TRCMethod> {

	@Override
	protected Class<TRCMethod> getInputType() {
		return TRCMethod.class;
	}

	@Override
	protected Collection<?> realizeCollection(TRCMethod input) {
		Collection<TRCMethod> results = new HashSet<TRCMethod>();
		for(Object invocation : input.getInvocations()) {
			TRCMethodInvocation inv = (TRCMethodInvocation)invocation;
			for(Object invoked : inv.getInvokes()) {
				results.add(((TRCMethodInvocation)invoked).getMethod());
			}
		}
		return results;
	}

}
