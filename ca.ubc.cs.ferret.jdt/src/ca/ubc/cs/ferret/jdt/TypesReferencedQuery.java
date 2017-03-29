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
import org.eclipse.core.runtime.SubProgressMonitor;

public class TypesReferencedQuery  extends 
JavaIntersectionConceptualQuery<FerretObject,FerretObject> {

    /**
     * The required public 0-argument constructor as per the extension-point.
     */
    public TypesReferencedQuery() {
    }

    public String getSubDescription() {
        return "types referenced";
    }

	@Override
	protected Collection<FerretObject> performQuery(FerretObject it,
			IProgressMonitor monitor) {
        IRelation referenced = getSphere().resolve(new SubProgressMonitor(monitor, 10),
        		ObjectOrientedRelations.OP_TYPES_REFERENCED, it);
		return referenced.asCollection();
	}

	@Override
	protected void processSolution(FerretObject type) {
        SimpleSolution s = new SimpleSolution(this, this);
        s.add("referenced", type);
        addSolution(s);
	}
}
