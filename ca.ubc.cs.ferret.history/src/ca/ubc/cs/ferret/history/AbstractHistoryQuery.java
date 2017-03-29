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
package ca.ubc.cs.ferret.history;

import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;

public abstract class AbstractHistoryQuery extends AbstractSingleParmConceptualQuery<Object> {
	protected int selectionCounter;
	
	public AbstractHistoryQuery() {}

	public void completed() {
		selectionCounter = getHistoryMonitor().getSelectionCounter();
	}

	public boolean isValid() {
		return selectionCounter == getHistoryMonitor().getSelectionCounter();
	}

	protected HistoryMonitor getHistoryMonitor() {
		return HistorySphereHelper.getDefault().getHistoryMonitor();
	}
}
