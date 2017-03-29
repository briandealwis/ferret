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
import java.util.Set;

import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.models.trace.TRCMethodInvocation;

import ca.ubc.cs.ferret.tptp.TptpSphereHelper;

public class DynamicMethodsUsedRelation extends
		AbstractTptpCollectionBasedRelation<TRCClass> {

	public DynamicMethodsUsedRelation() {}

	@Override
	protected Class<TRCClass> getInputType() {
		return TRCClass.class;
	}

	@Override
	protected Collection<?> realizeCollection(TRCClass input) {
		Set<TRCMethod> methods = new HashSet<TRCMethod>();
		for(Object o : input.getMethods()) {
			TRCMethod m = (TRCMethod)o;
			if(TptpSphereHelper.isInitializer(m)) { continue; }
			for(Iterator<?> iter = m.getInvocations().iterator(); iter.hasNext();) {
				TRCMethodInvocation inv = (TRCMethodInvocation)iter.next();
				if(inv.getInvokedBy() != null) {
					methods.add(m);
					break;
				}
			}
		}
		return methods;
	}

}
