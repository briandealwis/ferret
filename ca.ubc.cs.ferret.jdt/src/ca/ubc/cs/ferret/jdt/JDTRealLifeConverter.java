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
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.AbstractTypeConverter;
import ca.ubc.cs.ferret.types.ConversionException;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

public class JDTRealLifeConverter extends AbstractTypeConverter {

	public JDTRealLifeConverter() {}

	public ConversionResult<?> convert(Object objectInstance,
			ConversionSpecification spec, ISphere sphere)
			throws ConversionException {
		// FIXME: yeah yeah, should check the desiredClass is right too...
		if(objectInstance instanceof Class<?>) {
			IType t = JavaModelHelper.getDefault().resolveType(((Class<?>)objectInstance).getName());
			if(t != null) { return wrap(spec, Fidelity.Equivalent, IType.class, t); }
		}
		// FIXME: did we actually need this?
		//		else if(objectInstance instanceof Method) {
		//		Method m = (Method)objectInstance;
		//		IType t = JavaModelHelper.getDefault().resolveType(m.getDeclaringClass());
		//		t.getMethod(m.getName(), m.get)
		//		if(t != null) { return wrap(spec, Fidelity.Equivalent, IType.class, t);
		//	}
		else if(objectInstance instanceof IJavaProject) {
			IProject p = ((IJavaProject)objectInstance).getProject();
			if(p != null) { return wrap(spec, Fidelity.Exact, IProject.class, p); }
		}
		return null;
	}

}
