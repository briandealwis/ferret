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
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class FieldTypeProvider extends AbstractJavaAttributeValue<IField,Object> {

	public FieldTypeProvider() {
	}

	public Object[] getCategories() {
		return null;
	}

	public Object getCategory(IField field) {
		try {
			IType type = JavaModelHelper.getDefault().resolveSignature(field.getTypeSignature(), field);
			return type != null ? type : "(none)";
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
			return "(unknown)";
		}
	}

}
