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
import org.eclipse.jdt.core.IField;

public class JdtIsFieldRelation extends AbstractToolRelation {
	protected boolean done = false;
	protected FerretObject fo;
	protected ConversionResult<IField> fields;

	public JdtIsFieldRelation() {}

	@Override
	protected boolean configure(FerretObject... arguments) {
		if (arguments.length != 1) { return false; }
		fo = arguments[0];
		fields = fo.convert(IField.class, 0, Fidelity.Approximate);
		return fields != null && fields.wasSuccessful();
	}

	public boolean hasNext() {
		if (done) { return false; }
		return !fields.getResults().isEmpty();
	}

	public FerretObject next() {
		done = true;
		fo.setPrimaryFidelity(fo.getPrimaryFidelity().least(fields.getFidelity()));
		return fo;
	}
}
