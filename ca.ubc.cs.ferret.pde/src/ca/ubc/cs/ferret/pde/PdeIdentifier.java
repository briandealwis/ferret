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
package ca.ubc.cs.ferret.pde;

public class PdeIdentifier {
	protected String identifier;
	
	public PdeIdentifier(String id) {
		identifier = id;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PdeIdentifier && identifier.equals(((PdeIdentifier)obj).identifier);
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
	

}
