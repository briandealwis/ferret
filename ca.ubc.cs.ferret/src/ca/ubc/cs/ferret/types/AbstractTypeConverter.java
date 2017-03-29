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
package ca.ubc.cs.ferret.types;

import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public abstract class AbstractTypeConverter implements ITypeConverter {
	protected <T> ConversionResult<T> wrap(ConversionSpecification conversionContext,
			Fidelity conversionFidelity, Class<T> convClass, T converted) {
		ConversionResult<T> result = new ConversionResult<T>(convClass, conversionFidelity);
		if(converted != null) {
			result.addResult(converted);
		}
		return result;
	}


}
