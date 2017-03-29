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
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

public class ReferencesToType extends JavaIntersectionConceptualQuery<IType,FerretObject> {
    
    public ReferencesToType() {
    }
    
    protected String getReferencesOperation() {
		return ObjectOrientedRelations.OP_TYPE_REFERENCES;
	}

    public String getSubDescription() {
        return "references to type";
        //+ FerretPlugin.prettyPrint(method) + " (incl through interfaces and subtypes)";
    }

	@Override
	protected Collection<FerretObject> performQuery(IType it,
			IProgressMonitor monitor) {
    	IRelation op = getSphere().resolve(monitor,
        		getReferencesOperation(), it);
    	return op.asCollection();
	}

	@Override
	protected void processSolution(FerretObject e) {
    	SimpleSolution s = new SimpleSolution(this, e);
        s.add("item", e);
        addSolution(s);		
	}

}
