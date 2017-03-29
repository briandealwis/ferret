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

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.FerretJdtPlugin;
import ca.ubc.cs.ferret.util.AbstractClassifier;
import org.eclipse.jdt.core.JavaModelException;

public abstract class AbstractJavaAttributeValue<T,C> extends AbstractClassifier<T,C> {

	protected void log(Exception e) {
		if(!(e instanceof JavaModelException) || 
				FerretJdtPlugin.logJavaModelExceptions()) {
			FerretPlugin.log(e);
		}
	}
}
