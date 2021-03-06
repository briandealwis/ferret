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
package ca.ubc.cs.ferret.views;

import ca.ubc.cs.ferret.AskFerretObjectAction;

public class PopulatedAskFerretObjectContribution extends AskFerretObjectAction {

	public PopulatedAskFerretObjectContribution() {}

	@Override
	protected void process(QueriesDossierView view, Object[] objects) {
		view.promptForDesiredQueries(objects);
	}

}
