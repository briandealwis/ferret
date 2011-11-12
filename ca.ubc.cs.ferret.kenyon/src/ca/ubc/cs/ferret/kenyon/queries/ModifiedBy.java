package ca.ubc.cs.ferret.kenyon.queries;

import java.util.Collection;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.kenyon.KTransaction;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.SimpleSolution;

public class ModifiedBy extends AbstractKenyonIntersectingConceptualQuery<Object, String> {
    protected MultiMap<String,KTransaction> authors;

	@Override
	protected String getSubDescription() {
		return "modified by";
	}

	@Override
	protected Collection<String> performQuery(Object it,
			IProgressMonitor monitor) {
        IRelation modifiers = 
        	getSphere().resolve(monitor, KenyonSphereHelper.OP_MODIFICATIONS, it);
        for(KTransaction tx : modifiers.asCollection(KTransaction.class)) {	
			authors.put(tx.getAuthor(), tx);
        }
        return authors.keySet();
	}

	@Override
	protected void processSolution(String author) {
    	SimpleSolution s = new SimpleSolution(this, this);
		s.setPrimaryEntityName("author");
		s.add("author", author);
		 s.add(new StupidlySimpleRelation(author, "transactions", authors.get(author)));
//		s.add("transactions", authors.get(author)); can't do this: we don't support relations and entities
    	addSolution(s);
	}


	@Override
	protected void internalRun(IProgressMonitor monitor) {
	    authors = new MultiHashMap<String, KTransaction>();
		super.internalRun(monitor);
	}

	public boolean isValid() {
		return true;
	}

}
