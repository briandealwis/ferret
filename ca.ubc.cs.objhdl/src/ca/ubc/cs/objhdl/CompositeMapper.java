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
package ca.ubc.cs.objhdl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CompositeMapper implements IObjectMapper {
	protected Map<String, Collection<IObjectMapper>> handleMappers = 
		new HashMap<String, Collection<IObjectMapper>>();
	protected Map<String,Collection<IObjectMapper>> classMappers = 
		new HashMap<String, Collection<IObjectMapper>>();
	protected String handleTypes[] = new String[0];

	public String[] describe(Object o) {
		for(String typename : ClassLookupCache.getClassLookupOrder(o.getClass())) {
			String d[] = describe(o, typename);
			if(d != null) { return d; }
		}
		return null;
	}
	
	protected String[] describe(Object o, String typename) {
		if(classMappers.containsKey(typename)) {
			for(IObjectMapper mapper : classMappers.get(typename)) {
				String d[] = mapper.describe(o);
				if(d != null) { return d; }
			}
		}
		return null;
	}

	public String[] getHandleTypes() {
		return handleTypes;
	}

	public Object resolve(String handleType, String description) {
		for(IObjectMapper mapper : handleMappers.get(handleType)) {
			Object result = mapper.resolve(handleType, description);
			if(result != null) { return result; }
		}
		return null;
	}

	public void add(IObjectMapper m, String handleType, String classType) {
		Collection<IObjectMapper> map = handleMappers.get(handleType);
		if(map == null) {
			handleMappers.put(handleType, map = new HashSet<IObjectMapper>());
		}
		map.add(m);
		
		map = classMappers.get(classType);
		if(map == null) {
			classMappers.put(classType, map = new HashSet<IObjectMapper>());
		}
		map.add(m);
		handleTypes = handleMappers.keySet().toArray(new String[handleMappers.keySet().size()]);
	}

}
