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
package ca.ubc.cs.ferret.history;

import ca.ubc.cs.ferret.model.SimpleSolution;
import org.eclipse.core.runtime.IProgressMonitor;

public class TransitionsFromObjects extends AbstractHistoryQuery {
	
	public TransitionsFromObjects() {
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
        monitor.beginTask(getDescription(), 20);
//        IRelation<Object> impls = getSphere().perform(monitor,
//        		HistorySphereHelper.TRANSITIONS_TO, elements);  
        for(Object to : HistorySphereHelper.getDefault().getHistoryMonitor().transitionsTo(parameter, 3)) {
        	SimpleSolution s = new SimpleSolution(this, parameter);
//        	s.add("to", to);
        	if(to instanceof Object[]) {
        		for(int i = 0; i < ((Object[])to).length; i++) {
        			s.add("from." + i, ((Object[])to)[i]);
        		}
        	} else {
        		s.add("from", to);
        	}
        	addSolution(s);
        }
        monitor.done();
	}

	public String getDescription() {
		return "navigated from";
	}
}
