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

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.AbstractTypeConverter;
import ca.ubc.cs.ferret.types.ConversionException;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.ITypeConverter;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

public class JDTDebugConverter extends AbstractTypeConverter implements
		ITypeConverter {

	public ConversionResult<?> convert(Object objectInstance,
			ConversionSpecification spec, ISphere sphere)
			throws ConversionException {
		if(objectInstance instanceof IJavaFieldVariable) {
			try {
				IJavaFieldVariable var = (IJavaFieldVariable)objectInstance;
				IType type = JavaModelHelper.getDefault().resolveType(var.getReferenceTypeName());
				if(type == null) { return null; }
				IField equivalent = type.getField(var.getName());
				if(equivalent.exists()) { return wrap(spec, Fidelity.Exact, IField.class, equivalent); }
			} catch(DebugException e) {
				FerretPlugin.log(e);
			}
		} else if(objectInstance instanceof IJavaStackFrame) {
			try {
				IJavaStackFrame frame = (IJavaStackFrame)objectInstance;
				IType type = JavaModelHelper.getDefault().resolveType(frame.getDeclaringTypeName());
				if(type == null) { return null; }
				IMethod equivalent = type.getMethod(frame.getMethodName(), 
						Signature.getParameterTypes(frame.getSignature().replace('/', '.')));
				IMethod candidates[] = type.findMethods(equivalent);
				if(candidates == null || candidates.length == 0) { return null; }
				return wrap(spec, Fidelity.Exact, IMethod.class, candidates[0]);
			} catch(DebugException e) {
				FerretPlugin.log(e);
			}
		}
		return null;
	}

}
