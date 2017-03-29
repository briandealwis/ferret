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
package ca.ubc.cs.ferret.kenyon.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.kenyon.KTransaction;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import edu.se.evolution.kenyon.scm.Revision;

public class TxFileVersions extends
		AbstractSingleParmConceptualQuery<KTransaction> {

	public TxFileVersions() {}

	@SuppressWarnings("unchecked")
	@Override
	protected void internalRun(IProgressMonitor monitor) {
		for(Revision revision : (Collection<Revision>)parameter.getTransaction().getRevisions()) {
			SimpleSolution s = new SimpleSolution(this, this);
			s.add("revision", revision);
			addSolution(s);
		}
		KenyonSphereHelper.getDefault().closeSession();
	}

	public String getDescription() {
		return "files affected";
	}

	public boolean isValid() {
		return true;
	}

}
