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
import java.util.Collection;

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
