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

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.core.IJavaVariable;

public class ShadowedFieldsRelation extends
		AbstractCollectionBasedRelation<IJavaElement> {

	public ShadowedFieldsRelation() {}

	@Override
	protected Class<IJavaElement> getInputType() {
		return IJavaElement.class;
	}

	@Override
	protected IJavaElement checkInput(IJavaElement input) {
		return (input instanceof IField || input instanceof ILocalVariable || input instanceof IJavaVariable) ? input : null;
	}

	@Override
	protected Collection<?> realizeCollection(IJavaElement input) {
		IType startPoint = null;
		String fieldName = null;
		monitor.beginTask("Finding shadowed variables of " + FerretPlugin.prettyPrint(input), IProgressMonitor.UNKNOWN);
		if(input instanceof IField) {
			startPoint = JavaModelHelper.getDefault().getSuperclass(((IField)input).getDeclaringType(), new SubProgressMonitor(monitor, 1));
			fieldName = ((IField)input).getElementName();
		} else if(input instanceof ILocalVariable) {
			startPoint = (IType)((ILocalVariable)input).getAncestor(IJavaElement.TYPE);
			fieldName = ((ILocalVariable)input).getElementName();
		}
		Collection<IField> shadowed = new ArrayList<IField>();
		while(startPoint != null) {
			IField f = startPoint.getField(fieldName);
			if(f != null && f.exists()) { shadowed.add(f); }
			startPoint = JavaModelHelper.getDefault().getSuperclass(startPoint, new SubProgressMonitor(monitor, 1));
		}
		monitor.done();
		return shadowed;
	}
}
