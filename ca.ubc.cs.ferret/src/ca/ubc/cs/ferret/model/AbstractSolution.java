/*
 * Created on Jul 29, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.ubc.cs.ferret.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;

import ca.ubc.cs.clustering.ClusteringPlugin;
import ca.ubc.cs.clustering.IRelation;
import ca.ubc.cs.clustering.attrs.AttributeSource;
import ca.ubc.cs.clustering.attrs.AttributeSourceManager;
import ca.ubc.cs.clustering.attrs.DelegatingAttributeSource;
import ca.ubc.cs.clustering.attrs.IAttributeSource;

public abstract class AbstractSolution implements ISolution, IAdaptable {
	protected IConceptualQuery query;
    protected Map<String,Object> entities = new HashMap<String,Object>();
    protected Set<IRelation> relations = new HashSet<IRelation>();
    protected Object schema = null;
    protected String primaryEntityName;

    public AbstractSolution(IConceptualQuery q) {
        query = q;
    }

    public AbstractSolution(IConceptualQuery q, Object _schema) {
        this(q);
        schema = _schema;
    }
    
    public IConceptualQuery getQuery() {
        return query;
    }

    public boolean isSimpleSolution() {
        return entities.size() == 1 && getRelationsFrom(getPrimaryEntity()).isEmpty();
    }

    public Map<String,Object> getEntities() {
        return entities;
    }
    
    public Object getEntity(String slotName) {
        return entities.get(slotName);
    }
    
    public Object getSchema() {
        // FIXME: fix clients to set this properly
        return getClass();  // so all solutions will look homogenous for now
//        return schema;
    }
    
    public void setSchema(Object _schema) {
        schema = _schema;
    }

    public Set<IRelation> getRelationsFrom(Object ent) {
        Set<IRelation> results = new HashSet<IRelation>();
        for (IRelation r : relations) {
            if(r.getSubject().equals(ent)) { results.add(r); }
        }
        return results;
    }

    public Set<IRelation> getRelationsTo(Object ent) {
        Set<IRelation> results = new HashSet<IRelation>();
        for (IRelation r : relations) {
            if(r.getObject().equals(ent)) { results.add(r); }
        }
        return results;
    }

    public void add(String slotName, Object entity) {
        entities.put(slotName, entity);
    }

    public void add(IRelation r) {
        relations.add(r);
    }

	public <T> T getAdapter(Class<T> adapter) {
        if(adapter == IAttributeSource.class) {
        	return adapter.cast(new DelegatingAttributeSource(getPrimaryEntity()));
        }
        return null;
    }
    
    public Object getPrimaryEntity() {
    	if(primaryEntityName != null) { return getEntity(primaryEntityName); }
		return getEntities().values().iterator().next();
	}
    
    public String getPrimaryEntityName() {
		return primaryEntityName;
	}

    public void setPrimaryEntityName(String name) {
    	primaryEntityName = name;
    }
}
