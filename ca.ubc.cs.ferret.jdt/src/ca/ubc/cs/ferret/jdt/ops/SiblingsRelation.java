package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.types.FerretObject;

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
