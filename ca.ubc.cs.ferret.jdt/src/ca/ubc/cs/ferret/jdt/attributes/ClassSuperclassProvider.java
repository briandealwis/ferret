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
package ca.ubc.cs.ferret.jdt.attributes;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class ClassSuperclassProvider extends AbstractJavaAttributeValue<IType, Object> {

	public ClassSuperclassProvider() {	}

	public Object[] getCategories() {
		return null;
	}

	public Object getCategory(IType clazz) {
		try {
			if(clazz.isClass()) {
				IType sup = JavaModelHelper.getDefault().getSuperclass(clazz, new NullProgressMonitor());
				return sup != null ? sup : "(none)";
			}
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return "(unknown)";
		}
		return "(non-class)";
	}
}
