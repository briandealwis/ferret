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

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

public abstract class AbstractIntersectionConceptualQuery<IT,OT> extends
		AbstractConceptualQuery {
    protected IT elements[];

	public AbstractIntersectionConceptualQuery() {	}

	@SuppressWarnings("unchecked")
	public boolean setParameters(Map<String, Object[]> singletonMap) {
		assert singletonMap.size() == 1;
		assert singletonMap.containsKey(DEFAULT_PARAMETER);
		elements = (IT[])singletonMap.get(DEFAULT_PARAMETER);
		for(IT e : elements) {
			if(!validateParameter(e)) { return false; }
		}
		return true;
	}
	
    protected boolean validateParameter(IT it) {
		return true;
	}

    protected void internalRun(IProgressMonitor monitor) {
	    try {
	        monitor.beginTask(getDescription(), elements.length);
	        List<Collection<OT>> references = new ArrayList<Collection<OT>>(elements.length);
	        for(int i = 0; i < elements.length; i++) {
	        	Collection<OT> queryResults = performQuery(elements[i], new SubProgressMonitor(monitor, 1));
	        	if(queryResults == null) {
	        		FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, FerretErrorConstants.CONTRACT_VIOLATION,
	        				"Subquery returned null: " + getClass().getName(), null));
	        		continue;
	        	}
	        	references.add(i, queryResults);
	        }
	        
	        if(references.size() == 1) {
	        	for(OT e : references.get(0)) {
	        		processSolution(e);
	        	}
	        	return;
	        }
	        
	        Collection<OT> smallest = null;
	        for(Collection<OT> candidate : references) {
	        	if(smallest == null || candidate.size() < smallest.size()) {
	        		smallest = candidate;
	        	}
	        }
			Assert.isNotNull(smallest);
	        references.remove(smallest);
	        for(OT e : smallest) {
	        	boolean containedInAll = true;
	        	for(Collection<OT> other : references) {
	        		if(!other.contains(e)) { containedInAll = false; break; }
	        	}
	        	if(containedInAll) {
	        		processSolution(e);
	        	}
	        }
	    } finally {
	        pending = false;
	        monitor.done();
	    }
    }

	protected abstract void processSolution(OT e);

	protected abstract Collection<OT> performQuery(IT it, IProgressMonitor monitor);

	public String getDescription() {
		if(elements.length > 1) {
			return "common " + getSubDescription();
		} else {
			return getSubDescription();
		}
	}

	/**
	 * Provide sub-description of query (will be prefixed with "common " when multiple
	 * elements selected).
	 * @return
	 */
	protected abstract String getSubDescription();
}
