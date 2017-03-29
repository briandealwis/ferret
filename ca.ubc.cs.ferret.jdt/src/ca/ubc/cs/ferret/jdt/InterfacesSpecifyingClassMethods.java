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
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class InterfacesSpecifyingClassMethods extends JavaIntersectionConceptualQuery<IMethod,IType> {
	public InterfacesSpecifyingClassMethods() {}
	
	@Override
	protected boolean validateParameter(IMethod method) {
		try {
			return method.getDeclaringType().isClass() && 
				Flags.isPublic(method.getFlags());
		} catch(JavaModelException e) {
			FerretPlugin.log(e.getStatus());
			return false;
		}
	}
	
	@Override
	protected void processSolution(IType iface) {
		SimpleSolution s = new SimpleSolution(this, this);
		s.add("specified-by", iface);
		addSolution(s);
	}
	
	@Override
	protected Collection<IType> performQuery(IMethod method, IProgressMonitor monitor) {
		IRelation specifications = getSphere().resolve(monitor,
				ObjectOrientedRelations.OP_SPECIFICATIONS, method);
		Collection<IType> result = new HashSet<IType>();
		for(IMethod spec : specifications.asCollection(IMethod.class)) {
			result.add(spec.getDeclaringType());
		}
		return result;
	}
	
	@Override
	public String getSubDescription() {
		return "interfaces specifying methods"; 
	}

}
