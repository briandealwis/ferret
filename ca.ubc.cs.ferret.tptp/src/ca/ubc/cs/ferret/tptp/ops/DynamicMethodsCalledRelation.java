package ca.ubc.cs.ferret.tptp.ops;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.models.trace.TRCMethodInvocation;

public class DynamicMethodsCalledRelation extends AbstractTptpCollectionBasedRelation<TRCMethod> {

	@Override
	protected Class<TRCMethod> getInputType() {
		return TRCMethod.class;
	}

	@Override
	protected Collection<?> realizeCollection(TRCMethod input) {
		Collection<TRCMethod> results = new HashSet<TRCMethod>();
		for(Object invocation : input.getInvocations()) {
			TRCMethodInvocation inv = (TRCMethodInvocation)invocation;
			for(Object invoked : inv.getInvokes()) {
				results.add(((TRCMethodInvocation)invoked).getMethod());
			}
		}
		return results;
	}

}
