/*******************************************************************************
 * Copyright (c) 2005, 2017 Manumitting Technologies Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manumitting Technologies Inc - initial API and implementation
 *******************************************************************************/

package ca.ubc.cs.ferret.model;

import ca.ubc.cs.ferret.FerretFatalError;
import java.util.Objects;

public abstract class AbstractSphereFactory implements ISphereFactory {

	@Override
	public ISphereFactory clone() {
		try {
			return (ISphereFactory)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new FerretFatalError("clone failed!", e);
		}
	}

	public <T> T getAdapter(Class<T> adapter) {
		if (adapter.isInstance(this)) {
			return adapter.cast(this);
		}
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public boolean equals(Object obj) {
		return getClass() == obj.getClass() && Objects.equals(getId(), getClass().cast(obj).getId());
	}
}
