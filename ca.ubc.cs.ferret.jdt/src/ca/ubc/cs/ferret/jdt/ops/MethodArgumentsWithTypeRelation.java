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
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class MethodArgumentsWithTypeRelation extends AbstractCollectionBasedRelation<IType> {

	public MethodArgumentsWithTypeRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		monitor.beginTask("MethodsWithArgumentType", 15);
		String typeSimpleName = input.getElementName();
		String typeFQName = input.getFullyQualifiedName();

		IRelation references = resolver.topPerform(new SubProgressMonitor(monitor, 10),
				ObjectOrientedRelations.OP_TYPE_REFERENCES,
				new FerretObject(input, getResultsFidelity(), resolver.getRootSphere()));
		Set<FerretObject> results = new HashSet<FerretObject>();
		for(FerretObject ref : references) {
			ConversionResult<IMethod> cr = ref.convert(IMethod.class, 1, Fidelity.Approximate);
			if(cr == null || cr.getSingleResult().getNumberOfParameters() == 0) { continue; }
			resultingFidelity = resultingFidelity.least(cr.getFidelity());
			try {
				for(String sig : cr.getSingleResult().getParameterTypes()) {
					if(JavaModelHelper.getDefault().referencesType(sig, input,
							typeSimpleName, typeFQName, cr.getSingleResult())) {
						results.add(ref); 
						break;
					}
				}
			} catch(JavaModelException e) {
				JavaModelHelper.logJME(e);
			}
		}
		return results;
	}

}
