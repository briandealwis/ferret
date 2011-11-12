package ca.ubc.cs.ferret.tptp.ops;

import org.eclipse.hyades.models.trace.TRCLanguageElement;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.tptp.TptpSphereHelper;

public abstract class AbstractTptpCollectionBasedRelation<T extends TRCLanguageElement> extends
		AbstractCollectionBasedRelation<T> {

	public AbstractTptpCollectionBasedRelation() {}

	protected T checkInput(T input) {
		// re-resolve the instance in the TPTP sources defined for this sphere.
		return TptpSphereHelper.getDefault().reresolve(input, resolver.getSphere());
	}
}
