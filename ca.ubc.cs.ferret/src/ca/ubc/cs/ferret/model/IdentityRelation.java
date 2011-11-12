package ca.ubc.cs.ferret.model;

import java.util.LinkedList;

import ca.ubc.cs.ferret.types.FerretObject;

/**
 * An identity relation (one that maps any object to itself), optionally providing the object is of
 * the required type. 
 * @author Brian de Alwis
 */
public class IdentityRelation extends AbstractToolRelation {
	protected Class<?> clazz;
	protected LinkedList<FerretObject> list;
	
	public IdentityRelation() {}
	
	public IdentityRelation(Class<?> cl) {
		clazz = cl;
	}

	@Override
	protected void init() {
		list = null;
		super.init();
	}

	@Override
	protected boolean configure(FerretObject... arguments) {
		list = new LinkedList<FerretObject>();
		for(FerretObject fo : arguments) {
			if(clazz == null || fo.getAdapter(clazz) != null) {
				list.add(fo);
			}
		}
		return !list.isEmpty();
	}

	public boolean hasNext() {
		return !list.isEmpty();
	}

	public FerretObject next() {
		return list.removeFirst();
	}

}
