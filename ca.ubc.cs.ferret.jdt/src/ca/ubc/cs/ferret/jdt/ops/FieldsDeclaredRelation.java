package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class FieldsDeclaredRelation extends AbstractCollectionBasedRelation<IType> {
	
	public FieldsDeclaredRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		Set<IField> results = new HashSet<IField>();
		try {
			Collections.addAll(results, input.getFields());
		} catch (JavaModelException e) {
			JavaModelHelper.logJME(e);
		}
		return results;
	}

}
