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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The relation container and resolver.  Relations are registered with particular names.
 * When resolving relations by name, the first configurable relation wins.
 */
public class Sphere extends AbstractSphere {

	/**
     * The actual relation implementations with their original name.
     */
    protected Multimap<String,IRelationFactory> relationsMap = 
    	MultimapBuilder.hashKeys().arrayListValues().build();
    
    /**
     * Associate parameters; may be used my relation implementations.
     */
    protected Map<String, Object> parameters =
    	new HashMap<String, Object>();
    
    public Sphere(String d) {
    	description = d;
    }

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> cl) {
		Object result = parameters.get(key);
		if(result == null) { return null; }
		if(!cl.isInstance(result)) { return null; }
		return (T)result;
	}
	
	public void set(String key, Object value) {
		parameters.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> void internalGetAll(String key, Class<T> cl,
			Map<ISphere, T> results) {
		Object result = parameters.get(key);
		if(result == null || !cl.isInstance(result)) { return; }
		results.put(this, (T)result);
	}

	
	/**
	 * Register the given relation under the provided name.  Throw an exception if relations
	 * were already registered under that name. (one time only deal: helps catch bugs)
	 * @param name
	 * @param relations
	 */
	public void register(String name, IRelationFactory... relations) {
		if(relationsMap.containsKey(name)) {
			throw new NameAlreadyRegisteredException("\"" + name + "\" already registered");
		}
		for(IRelationFactory r : relations) {
			relationsMap.put(name, r);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + description + ")";
	}

	@Override
	public AbstractRelationResolvingState createResolverState(AbstractRelationResolvingState parent) {
		return new SphereResolvingState(parent);
	}

    protected class SphereResolvingState extends AbstractRelationResolvingState {    	

    	public SphereResolvingState(AbstractRelationResolvingState parent) {
    		super(parent);
    	}
    	
		@Override
		public AbstractRelationResolvingState next(IProgressMonitor monitor) {
			if(finished) { return parent != null ? parent : this; }
			
			Collection<IRelationFactory> factories = relationsMap.get(relationName);
			if(factories != null) {
				for(IRelationFactory factory : factories) {
					if((result = factory.configure(monitor, this, arguments)) != null) {
						finished = true;
						return this;
					}
				}
			}
			finished = true;
			return this;
		}
    }
}
