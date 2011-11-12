package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.NamedJoinRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.types.FerretObject;

public class ClassMethodsOverriddenRelation 
		extends AbstractCollectionBasedRelation<FerretObject> {

	public ClassMethodsOverriddenRelation() {}
	
	@Override
	protected Class<FerretObject> getInputType() {
		return FerretObject.class;
	}

	@Override
	protected FerretObject checkInput(FerretObject input) {
		try {
			return input.resolve(ObjectOrientedRelations.OP_IS_METHOD) == null
			? null : input;
        } catch(UnsupportedOperationException e) {
            return null;	// this isn't an error
		}
	}
	
	@Override
	protected Collection<?> realizeCollection(FerretObject input) {
		FerretObject sig = null;
		for(FerretObject s : input.resolve(ObjectOrientedRelations.OP_SIGNATURE)) {
			sig = s;
		}
		if(sig == null) { return Collections.EMPTY_LIST; }
		IRelation methods = new NamedJoinRelation(
				ObjectOrientedRelations.OP_DECLARING_TYPE,
				ObjectOrientedRelations.OP_IS_CLASS,
				ObjectOrientedRelations.OP_SUPERTYPES,
				ObjectOrientedRelations.OP_IS_CLASS,
				ObjectOrientedRelations.OP_DECLARED_METHODS)
		.resolve(monitor, input.getSphere(), input);
		Collection<FerretObject> results = new HashSet<FerretObject>();
		for(FerretObject m : methods) {
			try {
				for(FerretObject s : m.resolve(ObjectOrientedRelations.OP_SIGNATURE)) {
					if(sig.equals(s)) {
						results.add(m);
						break;
					}
				}
			} catch(UnsupportedOperationException e) {
				// ignore
			}		
		}
		return results;
	}


}
