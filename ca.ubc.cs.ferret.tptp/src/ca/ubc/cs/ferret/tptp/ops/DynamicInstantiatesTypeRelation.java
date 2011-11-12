package ca.ubc.cs.ferret.tptp.ops;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCLanguageElement;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.models.trace.TRCMethodInvocation;

import ca.ubc.cs.ferret.tptp.TptpSphereHelper;

public class DynamicInstantiatesTypeRelation extends AbstractTptpCollectionBasedRelation<TRCClass> {

	@Override
	protected Class<TRCClass> getInputType() {
		return TRCClass.class;
	}

	@Override
	protected Collection<?> realizeCollection(TRCClass input) {
		Set<TRCLanguageElement> solutions = new HashSet<TRCLanguageElement>();
		for(Iterator<?> iter = input.getMethods().iterator(); iter.hasNext();) {
			TRCMethod method = (TRCMethod)iter.next();
			if(TptpSphereHelper.isConstructor(method)) {
				for(Iterator<?> iter2 = method.getInvocations().iterator(); iter2.hasNext();) {
					TRCMethodInvocation inv = (TRCMethodInvocation)iter2.next();
					TRCMethod m = inv.getInvokedBy().getMethod();
					// FIXME: should this be differentiating between initializers?  Or should
					// we be pushing this logic into the conversions?
					solutions.add(TptpSphereHelper.isInitializer(m) ? m.getDefiningClass() : m);
				}				
			}
		}
		return solutions;
	}

}
