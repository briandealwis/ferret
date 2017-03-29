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
package ca.ubc.cs.ferret.jdt.ops;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class JdtMethodSignatureRelation extends AbstractCollectionBasedRelation<IMethod> {

	public JdtMethodSignatureRelation() {}

	@Override
	protected Class<IMethod> getInputType() {
		return IMethod.class;
	}

	@Override
	protected Collection<?> realizeCollection(IMethod input) {
		try {
			Collection<String> result = new LinkedList<String>();
			result.add(input.getElementName() + JavaModelHelper.getDefault().resolvedMethodSignature(input));
			return result;
		} catch(JavaModelException e) {
			JavaModelHelper.logJME(e);
			return Collections.emptyList();
		}
	}
}
