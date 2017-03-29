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
import java.util.Iterator;

import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.models.trace.TRCMethodInvocation;

public class DynamicMethodReferencesRelation extends AbstractTptpCollectionBasedRelation<TRCMethod> {
	
	public DynamicMethodReferencesRelation() {	}

	@Override
	protected Class<TRCMethod> getInputType() {
		return TRCMethod.class;
	}

	@Override
	protected Collection<?> realizeCollection(TRCMethod input) {
		Collection<TRCMethod> results = new HashSet<TRCMethod>();
		for(Iterator<?> iter = input.getInvocations().iterator(); iter.hasNext();) {
			TRCMethodInvocation inv = (TRCMethodInvocation)iter.next();
//			System.out.println("TRCMethodInvocation: " + inv);
//			System.out.println("  method: " + inv.getMethod());
//			System.out.println("  invokedBy: " + inv.getInvokedBy());
//			System.out.println("  invokes: " + inv.getInvokes());
			if(inv.getInvokedBy() != null) {
				results.add(inv.getInvokedBy().getMethod());
			}
		}
		return results;
	}
}
