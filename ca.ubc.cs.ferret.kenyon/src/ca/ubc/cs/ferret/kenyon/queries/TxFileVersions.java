package ca.ubc.cs.ferret.kenyon.queries;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.kenyon.KTransaction;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.model.SimpleSolution;
import edu.se.evolution.kenyon.scm.Revision;

public class TxFileVersions extends
		AbstractSingleParmConceptualQuery<KTransaction> {

	public TxFileVersions() {}

	@SuppressWarnings("unchecked")
	@Override
	protected void internalRun(IProgressMonitor monitor) {
		for(Revision revision : (Collection<Revision>)parameter.getTransaction().getRevisions()) {
			SimpleSolution s = new SimpleSolution(this, this);
			s.add("revision", revision);
			addSolution(s);
		}
		KenyonSphereHelper.getDefault().closeSession();
	}

	public String getDescription() {
		return "files affected";
	}

	public boolean isValid() {
		return true;
	}

}
