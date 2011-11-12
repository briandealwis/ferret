/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;

import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;

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
