package ca.ubc.cs.ferret.kenyon;

import java.util.Set;

import ca.ubc.cs.ferret.FerretPlugin;
import edu.se.evolution.kenyon.scm.Revision;
import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;
import edu.se.evolution.kenyon.scm.SCMTransaction;

public class KTransaction implements Comparable<KTransaction> {
	protected SCMReposConfigSpec spec;
	protected SCMTransaction tx;
	
	public KTransaction(SCMReposConfigSpec spec, SCMTransaction tx) {
		this.spec = spec;
		this.tx = tx;
	}
	
	public SCMReposConfigSpec getConfigSpec() {
		return spec;
	}
	
	public SCMTransaction getTransaction() {
		return tx;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof KTransaction) {
			KTransaction other = (KTransaction)obj;
			return spec.equals(other.spec) && tx.equals(other.tx);
		}
		return false;
	}

	
	@Override
	public String toString() {
		return FerretPlugin.prettyPrint(tx);
	}

	@Override
	public int hashCode() {
		return spec.hashCode() * 37 + tx.hashCode();
	}

	public int compareTo(KTransaction o) {
		return tx.getStartDate().compareTo(o.tx.getStartDate());
	}

	public String getAuthor() {
		return tx.getAuthor();
	}
	
	@SuppressWarnings("unchecked")
	public Set<Revision> getRevisions() {
		return tx.getRevisions();
	}
}
