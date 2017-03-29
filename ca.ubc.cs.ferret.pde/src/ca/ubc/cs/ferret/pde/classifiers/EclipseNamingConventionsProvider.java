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
package ca.ubc.cs.ferret.pde.classifiers;

import ca.ubc.cs.clustering.attrs.IClassifier;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.resource.ImageDescriptor;

public class EclipseNamingConventionsProvider implements IClassifier<IMember,String> {
	public final String API = "api";
	public final String INTERNAL = "internal";
	public final String PROVISIONAL = "provisional";
	
	public EclipseNamingConventionsProvider() {
		super();
	}

	public String getCategory(IMember member) {
		String typeName = null;
		if(member.getDeclaringType() == null) {
			if(member instanceof IType) {
				typeName = ((IType)member).getFullyQualifiedName();
			} else {
				return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
			}
		} else {
			typeName = member.getDeclaringType().getFullyQualifiedName();
		}
		if(typeName == null) { return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE; }
		if(typeName.contains(".internal.")) { return INTERNAL; }
		if(typeName.contains(".provisional.")) { return PROVISIONAL; }
		return API;
	}

	public String[] getCategories() {
		return new String[] { API, INTERNAL, PROVISIONAL };
	}

	public ImageDescriptor getCategoryImage(String category) {
		return null;
	}

	public String getCategoryText(String category) {
		return category;
	}

}
