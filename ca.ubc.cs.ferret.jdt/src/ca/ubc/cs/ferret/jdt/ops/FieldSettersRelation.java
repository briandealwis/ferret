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
import org.eclipse.jdt.core.IField;

public class FieldSettersRelation extends AbstractCollectionBasedRelation<IField> {

	public FieldSettersRelation() {}

	@Override
	protected Class<IField> getInputType() {
		return IField.class;
	}

	@Override
	protected Collection<?> realizeCollection(IField input) {
		return JavaModelHelper.getDefault().getFieldSetters(input, monitor);
	}

}
