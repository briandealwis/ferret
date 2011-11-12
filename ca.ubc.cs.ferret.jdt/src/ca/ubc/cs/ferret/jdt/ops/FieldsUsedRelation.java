package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class FieldsUsedRelation extends AbstractCollectionBasedRelation<IMember> {

	public FieldsUsedRelation() {}

	@Override
	protected Class<IMember> getInputType() {
		return IMember.class;
	}

	@Override
	protected Collection<?> realizeCollection(IMember input) {
		Set<IField> results = new HashSet<IField>();
		Collections.addAll(results, JavaModelHelper.getDefault().getUsedFields(input, monitor));
		return results;
	}

}
