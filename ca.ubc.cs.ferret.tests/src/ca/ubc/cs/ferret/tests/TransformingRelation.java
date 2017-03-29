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
package ca.ubc.cs.ferret.tests;

import ca.ubc.cs.ferret.model.AbstractToolRelation;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.function.Function;

public class TransformingRelation<T> extends AbstractToolRelation {
	public enum ItemTreatment {
		AllMustBeConformant, DiscardNonConformant, PassThroughNonConformant
	};

	protected Class<T> clazz;
	protected Function<T,?> transformer;
	protected Iterator<FerretObject> iterator;
	protected ItemTreatment argumentTreatment = ItemTreatment.PassThroughNonConformant;
	
	public TransformingRelation(Class<T> c, Function<T,?> t) {
		clazz = c;
		transformer = t;
	}
	

	@Override
	protected boolean configure(FerretObject... arguments) {
		if(argumentTreatment == ItemTreatment.AllMustBeConformant) {
			for(FerretObject arg : arguments) {
				ConversionResult<?> result = arg.convert(clazz, 1, Fidelity.Approximate);
				if(result == null || result.getResults().isEmpty()) {
					return false;
				}
			}
		}
		iterator = Iterators.forArray(arguments);
		return true;
	}


	public boolean hasNext() {
		return iterator.hasNext();
	}

	public FerretObject next() {
		FerretObject fo = iterator.next();
		ConversionResult<T> result = fo.convert(clazz, 1, Fidelity.Approximate);
		if(result == null) {
			return argumentTreatment == ItemTreatment.PassThroughNonConformant
					? fo : null;
		}
		return new FerretObject(transformer.apply(result.getSingleResult()), result.getFidelity(),
				fo.getSphere());
	}


	public ItemTreatment getArgumentTreatment() {
		return argumentTreatment;
	}


	public void setArgumentTreatment(ItemTreatment argumentTreatment) {
		this.argumentTreatment = argumentTreatment;
	}
}
