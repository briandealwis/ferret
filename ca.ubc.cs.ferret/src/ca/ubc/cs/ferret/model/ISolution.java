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
package ca.ubc.cs.ferret.model;

import ca.ubc.cs.clustering.IRelation;
import java.util.Map;
import java.util.Set;

/**
 * An answer to a conceptual query.  A query may have multiple possible solutions. 
 * @author bsd
 */
public interface ISolution {
    public IConceptualQuery getQuery();
    
    /**
     * Answer true if this solution is <EM>simple</EM>, meaning that its computation
     * is made up of a single real fact, with the remaining being descriptive properties.
     * @return true if simple
     */
    public boolean isSimpleSolution();
    
    public Map<String,Object> getEntities();  // Bindings? Slots-Fillers?
    public Object getEntity(String slotName);
    public Object getSchema();
    
    public Set<IRelation> getRelationsFrom(Object ent);
    public Set<IRelation> getRelationsTo(Object ent);

    /**
     * Return the [primary element -- such an element is only guaranteed for
     * simple solutions.  Return null if there is no primary entity.
     * @return primary entity
     */
	public Object getPrimaryEntity();

	public String getPrimaryEntityName();
}
