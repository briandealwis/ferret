package ca.ubc.cs.ferret.display;

import ca.ubc.cs.clustering.IRelation;
import ca.ubc.cs.ferret.model.ISolution;

public class DwSolutionIntermediate extends DwBaseObject {
	protected Object kiddies[];
	protected Object label;
	protected ISolution solution;
	
	public DwSolutionIntermediate(Object label, Object[] kids,
			ISolution solution, IDisplayObject parent) {
		super(parent);
		this.label = label; 
		kiddies = kids;
		this.solution = solution;
	}

	@Override
	public boolean hasChildren() {
		return kiddies.length > 0;
	}

	@Override
	protected void buildChildren(IDisplayObject[] oldChildren) {
		children = new IDisplayObject[kiddies.length];
		for(int i = 0; i < kiddies.length; i++) {
			if(kiddies[i] instanceof IRelation) {
				children[i] = DwSolutionEntity.forRelation((IRelation)kiddies[i],
						solution, this);
			} else if(!solution.getRelationsFrom(kiddies[i]).isEmpty()) {
				children[i] = new DwSolutionIntermediate(kiddies[i], 
						solution.getRelationsFrom(kiddies[i]).toArray(), solution, this);
			} else {
				IDisplayObject cdo = findDisplayObject(kiddies[i], oldChildren);
				if(cdo == null) { cdo = DwObject.forObject(kiddies[i], this); }
				children[i] = cdo;
			}
		}
	}

	public Object getObject() {
		return label;
	}

}
