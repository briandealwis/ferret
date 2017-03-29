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

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.jdt.core.IMethod;

public class JdtIsMethodRelation extends AbstractToolRelation {
	protected boolean done = false;
	protected FerretObject fo;
	protected ConversionResult<IMethod> methods;

	public JdtIsMethodRelation() {}

	@Override
	protected boolean configure(FerretObject... arguments) {
		if (arguments.length != 1) { return false; }
		fo = arguments[0];
		methods = fo.convert(IMethod.class, 0, Fidelity.Approximate);
		return methods != null && methods.wasSuccessful();
	}

	public boolean hasNext() {
		if (done) { return false; }
		return !methods.getResults().isEmpty();
	}

	public FerretObject next() {
		done = true;
		fo.setPrimaryFidelity(fo.getPrimaryFidelity().least(methods.getFidelity()));
		return fo;
	}

}
