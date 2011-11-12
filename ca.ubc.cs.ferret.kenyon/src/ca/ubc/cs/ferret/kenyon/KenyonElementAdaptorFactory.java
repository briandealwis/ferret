package ca.ubc.cs.ferret.kenyon;

import java.util.Date;

import org.eclipse.core.runtime.IAdapterFactory;

import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;
import edu.se.evolution.kenyon.scm.SCMTransaction;

public class KenyonElementAdaptorFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object obj, Class adapterType) {
		if (adapterType.isInstance(obj)) {
			return obj;
		}
		if(obj instanceof SCMReposConfigSpec) {
			SCMReposConfigSpec spec = (SCMReposConfigSpec)obj;
			if(adapterType == Date.class) {
				return spec.getDate();
			}
		}
		if(obj instanceof KTransaction) {
			KTransaction tx = (KTransaction)obj;
			if(adapterType == Date.class) {
				return tx.getTransaction().getStartDate();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { SCMReposConfigSpec.class, SCMTransaction.class };
	}

}
