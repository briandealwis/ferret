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
package ca.ubc.cs.ferret.tests;

import ca.ubc.cs.clustering.attrs.IClassifier;
import org.eclipse.jface.resource.ImageDescriptor;

public class GradStudentDegreeClassifier implements
		IClassifier<GraduateStudent, String> {
	
	public String[] getCategories() {
		return new String[] { "MSc", "PhD" };
	}

	public String getCategory(GraduateStudent object) {
		return object.degree;
	}

	public ImageDescriptor getCategoryImage(String category) {
		return null;
	}

	public String getCategoryText(String category) {
		return category;
	}

}
