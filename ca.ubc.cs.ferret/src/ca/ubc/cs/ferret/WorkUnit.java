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
package ca.ubc.cs.ferret;

import ca.ubc.cs.ferret.model.Consultation;
import org.eclipse.core.runtime.IProgressMonitor;

public class WorkUnit implements Comparable<WorkUnit> {
    protected Consultation consultation;
    protected int priority;
    protected IProgressMonitor monitor = null;
        
    public WorkUnit(Consultation c, int p) {
        consultation = c;
        priority = p;
    }
    
    public String toString() {
        return "WorkUnit(priority=" + priority + ", " + consultation + ")";
    }
    
    public int compareTo(WorkUnit other) {
        return getPriority() - other.getPriority();
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public int getPriority() {
        return priority;
    }

    public void cancel() {
        if(monitor != null) { monitor.setCanceled(true); }
    }

    public void setMonitor(IProgressMonitor _monitor) {
        monitor = _monitor;
    }

    /**
     * Note that the work must be resubmitted for the new value to take effect.
     */ 
    public void setPriority(int i) {
        priority = i;
    }
}
