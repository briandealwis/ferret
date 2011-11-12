/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.Consultation;

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