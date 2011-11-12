package ca.ubc.cs.ferret.kenyon.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.kenyon.KTransaction;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;
import edu.se.evolution.kenyon.scm.SCMTransaction;

public class TransactionsOf extends
		AbstractSingleParmConceptualQuery<SCMReposConfigSpec> {

	@SuppressWarnings("unchecked")
	@Override
	protected void internalRun(IProgressMonitor monitor) {
		for(SCMTransaction tx : (Collection<SCMTransaction>)parameter.getCompletingTransactions()) {
			SimpleSolution s = new SimpleSolution(this,this);
			s.add("tx", new KTransaction(parameter, tx));
			s.setPrimaryEntityName("tx");
			addSolution(s);
		}
		KenyonSphereHelper.getDefault().closeSession();
	}

	public String getDescription() {
		return "constituent transactions";
	}

	public boolean isValid() {
		return true;
	}

}
