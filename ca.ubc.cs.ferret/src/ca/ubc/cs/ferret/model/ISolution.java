/*
 * Created on Jul 29, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.ubc.cs.ferret.model;

import java.util.Map;
import java.util.Set;

import ca.ubc.cs.clustering.IRelation;

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
