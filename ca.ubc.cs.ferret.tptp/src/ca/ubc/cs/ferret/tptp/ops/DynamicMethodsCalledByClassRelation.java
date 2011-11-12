package ca.ubc.cs.ferret.tptp.ops;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.models.trace.TRCMethodInvocation;

public class DynamicMethodsCalledByClassRelation extends
		AbstractTptpCollectionBasedRelation<TRCClass> {

	@Override
	protected Class<TRCClass> getInputType() {
		return TRCClass.class;
	}

	@Override
	protected Collection<?> realizeCollection(TRCClass input) {
		Collection<TRCMethod> results = new HashSet<TRCMethod>();
		for(Object method : input.getMethods()) {
			for(Object invocation : ((TRCMethod)method).getInvocations()) {
				for(Object invoked : ((TRCMethodInvocation)invocation).getInvokes()) {
					results.add(((TRCMethodInvocation)invoked).getMethod());
				}
			}
		}
		return results;
	}


}
