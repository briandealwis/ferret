/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
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
