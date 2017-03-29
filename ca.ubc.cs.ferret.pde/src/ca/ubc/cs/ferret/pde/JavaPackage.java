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

/** Representation of a named Java Package. */
public class JavaPackage {
	private String pkg;

	public JavaPackage(String packageName) {
		pkg = packageName;
	}

	public String getPackage() {
		return pkg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		JavaPackage other = (JavaPackage)obj;
		if(pkg == null) {
			if(other.pkg != null) return false;
		} else if(!pkg.equals(other.pkg)) return false;
		return true;
	}
}
