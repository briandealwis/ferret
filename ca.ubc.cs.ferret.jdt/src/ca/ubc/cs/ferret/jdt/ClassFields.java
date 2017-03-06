package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.clustering.Cluster;
import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;

public class ClassFields extends JavaRelatedConceptualQuery<IType> {

	public ClassFields() {
	}

	public String getDescription() {
		return "fields declared";
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
		Set<String> seenNames = new HashSet<String>();
		Clustering<Object> shadowing = new Clustering<Object>("shadowed/unshadowed");
		Cluster<Object> shadowed = shadowing.createCluster("shadowed");
		Cluster<Object> unshadowed = shadowing.createCluster("unshadowed");
		addClustering(shadowing);
		
		monitor.beginTask(getDescription(), 20);
	    IRelation fields = getSphere().resolve(monitor,
	    		ObjectOrientedRelations.OP_FIELDS_DECLARED, parameter);
	    processFields(fields.asCollection(IField.class), seenNames, shadowed, unshadowed);
	    IRelation supers = getSphere().resolve(monitor,
	    		ObjectOrientedRelations.OP_SUPERCLASSES, parameter);  
	    for(FerretObject sup : supers) {
		    fields = getSphere().resolve(monitor,
		    		ObjectOrientedRelations.OP_FIELDS_DECLARED, sup);
		    processFields(fields.asCollection(IField.class), seenNames, shadowed, unshadowed);
	    }
	}

	private void processFields(Iterable<IField> fields, Set<String> seenNames, 
			Cluster<Object> shadowed, Cluster<Object> unshadowed) {
		for(IField f : fields) {
	    	SimpleSolution s = new SimpleSolution(this, f);
	    	s.add("declared", f);
	    	addSolution(s);
	    	if(seenNames.contains(f.getElementName())) {
	    		shadowed.add(s);
	    	} else {
	    		unshadowed.add(s);
	    	}
	    	seenNames.add(f.getElementName());
		}
	}

}
