package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;

import org.eclipse.jdt.core.IField;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class FieldGettersRelation extends AbstractCollectionBasedRelation<IField> {

	public FieldGettersRelation() {}

	@Override
	protected Class<IField> getInputType() {
		return IField.class;
	}

	@Override
	protected Collection<?> realizeCollection(IField input) {
		return JavaModelHelper.getDefault().getFieldReaders(input, monitor);
	}

}
