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
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IType;

public class SiblingsRelation extends AbstractCollectionBasedRelation<FerretObject> {

	@Override
	protected Class<FerretObject> getInputType() {
		return FerretObject.class;
	}

	@Override
	protected Collection<?> realizeCollection(FerretObject input) {
		monitor.beginTask("Finding siblings of " + FerretPlugin.prettyPrint(input), IProgressMonitor.UNKNOWN);

		IRelation supcl = input.resolve(new SubProgressMonitor(monitor,2), 
				ObjectOrientedRelations.OP_SUPERCLASS);

		Set<FerretObject> siblings = new HashSet<FerretObject>();
		for(FerretObject sup : supcl.asCollection()) {
			IType t = (IType)sup.getAdapter(IType.class);
			if(t == null) { continue; }
			// don't bother with j.l.O -- too many to return
			if(t.getFullyQualifiedName().equals("java.lang.Object")) { continue; }
			IRelation subclasses = sup.resolve(new SubProgressMonitor(monitor, 1),
					ObjectOrientedRelations.OP_SUBCLASSES);
			siblings.addAll(subclasses.asCollection());
		}
		siblings.remove(input);
		monitor.done();
		return siblings;
	}
}
