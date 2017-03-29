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
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISolution;
import java.util.Collection;

public class DwSolution extends DwBaseObject implements IDisplayObject {
    protected ISolution solution;
    
    public DwSolution(ISolution _solution, IDisplayObject parent) {
        super(parent);
        solution = _solution;
    }

    @Override
    protected void buildChildren(IDisplayObject[] oldChildren) {
    	if(solution.getPrimaryEntityName() != null) {
	        Object primaryEntity = solution.getPrimaryEntity();
	        Collection<IRelation> relations = solution.getRelationsFrom(primaryEntity); 
	        children = new IDisplayObject[relations.size()];
	        int i = 0;
	        for(IRelation rel : relations) {
        		children[i++] = DwSolutionEntity.forRelation(rel, solution, this);
	        }
    	} else {
	        children = new IDisplayObject[solution.getEntities().size()];
	        int index = 0;
	        for(Object name : solution.getEntities().keySet()) {
	            children[index++] = 
	            	DwSolutionEntity.forEntity(name, 
	            		solution.getEntities().get(name), solution, this);
	        }
    	}
    }

    public String getText() {
    	if(solution.getPrimaryEntityName() != null) {
    		return FerretPlugin.prettyPrint(solution.getPrimaryEntity());
    	}
        return solution.toString();
    }
    

    public Object getObject() {
    	if(solution.getPrimaryEntityName() != null) {
    		return solution.getPrimaryEntity();
    	}
        return solution;
    }

    public static boolean allAreSimpleSolutions(Collection<?> solutions) {
        for (Object sol : solutions) {
        	if(sol instanceof ISolution) {
        		if(!((ISolution)sol).isSimpleSolution()) { return false; }
        	} else {
        		return false;
        	}
        }
        return true;
    }

	public static IDisplayObject asChild(IDisplayObject parent, ISolution sol, boolean asSimple) {
		if(asSimple) {
            return DwObject.forObject(sol.getPrimaryEntity(), parent);
		} else {
			return new DwSolution(sol, parent);
		}
	}

}
