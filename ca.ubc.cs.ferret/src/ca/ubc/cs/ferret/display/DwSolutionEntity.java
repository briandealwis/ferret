package ca.ubc.cs.ferret.display;

import java.util.Collection;

import ca.ubc.cs.clustering.IRelation;
import ca.ubc.cs.ferret.model.ISolution;

public class DwSolutionEntity extends DwObject<Object> implements IDisplayObject {
	protected Object key;
	
	public DwSolutionEntity(Object _key, Object value, IDisplayObject parent) {
		super(value, parent);
		key = _key;
	}

	@Override
	public String getText() {
		return key.toString() + " = " + super.getText();
	}

	public static IDisplayObject forRelation(IRelation rel, ISolution solution,
			IDisplayObject parent) {
		return forEntity(rel.getDescription(), rel.getObject(), solution, parent);
	}

	public static IDisplayObject forEntity(Object label, Object object,
			ISolution solution, IDisplayObject parent) {
    	if(object instanceof Object[]) {
    		return new DwSolutionIntermediate(label, (Object[])object, 
    				solution, parent);
    	} else if(object instanceof Collection) {
    		return new DwSolutionIntermediate(label, 
    				((Collection<?>)object).toArray(), solution, parent);
    	} else if(!solution.getRelationsFrom(object).isEmpty()){
    		return new DwSolutionIntermediate(label, 
    				solution.getRelationsFrom(object).toArray(), solution, parent);
    	} else {    		
    		return new DwSolutionEntity(label, object, parent);
    	}
	}
}
