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

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

public class FieldsOfType extends JavaRelatedConceptualQuery<IType> {
	
	public FieldsOfType() {}

	public String getDescription() {
		return "fields of type";
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		try {
			monitor.beginTask(getDescription(), 10);
			IRelation fields = getSphere().resolve(monitor, 
					ObjectOrientedRelations.OP_FIELDS_OF_TYPE, parameter);
			for (FerretObject field : fields) {
				SimpleSolution s = new SimpleSolution(this, this);
				s.add("field", field);
				addSolution(s);            
			}
		} finally {
			monitor.done();
		}
	}

}
