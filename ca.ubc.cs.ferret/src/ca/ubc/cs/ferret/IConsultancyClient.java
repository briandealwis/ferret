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
package ca.ubc.cs.ferret;

import ca.ubc.cs.ferret.model.Consultation;

public interface IConsultancyClient {
	/**
	 * Return true if this client is awaiting the result of the provided consultation.
	 * @param c
	 * @return true if consultation is being used
	 */
	public boolean isAwaiting(Consultation c);
	
	/**
	 * Notify that any existing consultations should be flushed and potentially
	 * regenerated.
	 */
	public void consultancyReset();
}
