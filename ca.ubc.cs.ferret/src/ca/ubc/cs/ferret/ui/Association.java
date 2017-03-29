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
package ca.ubc.cs.ferret.ui;

public class Association<T1, T2> {
	protected T1 from;
	protected T2 to;
	
	public Association(T1 _from, T2 _to) {
		from = _from;
		to = _to;
	}

	public T1 getFrom() {
		return from;
	}

	public T2 getTo() {
		return to;
	}
}
