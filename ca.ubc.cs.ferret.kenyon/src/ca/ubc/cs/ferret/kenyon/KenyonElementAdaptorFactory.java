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
package ca.ubc.cs.ferret.kenyon;

import java.util.Date;

import org.eclipse.core.runtime.IAdapterFactory;

import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;
import edu.se.evolution.kenyon.scm.SCMTransaction;

public class KenyonElementAdaptorFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object obj, Class adapterType) {
		if (adapterType.isInstance(obj)) {
			return obj;
		}
		if(obj instanceof SCMReposConfigSpec) {
			SCMReposConfigSpec spec = (SCMReposConfigSpec)obj;
			if(adapterType == Date.class) {
				return spec.getDate();
			}
		}
		if(obj instanceof KTransaction) {
			KTransaction tx = (KTransaction)obj;
			if(adapterType == Date.class) {
				return tx.getTransaction().getStartDate();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { SCMReposConfigSpec.class, SCMTransaction.class };
	}

}
