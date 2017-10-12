/*******************************************************************************
 * Copyright (c) 2004 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.model;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.ferret.FerretPlugin;
import com.google.common.base.Stopwatch;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * A base class for CQs.
 */
public abstract class AbstractConceptualQuery
		implements IConceptualQuery {
    protected Consultation consultation;
    protected String category;
	protected boolean pending = true;
    protected boolean simple;
    protected boolean homogenous;
    protected Object homogenousSchema;
    protected Set<Fact> facts;
    protected Set<ISolution> solutions;
    protected Set<Clustering<Object>> clusterings;
    
    protected boolean errorOccurred = false;

    public AbstractConceptualQuery() {
        reset();
    }
    
    @Override
	public boolean equals(Object obj) {
		return getClass() == obj.getClass() 
			&& consultation.equals(((AbstractConceptualQuery)obj).getConsultation());
	}

	@Override
	public int hashCode() {
		return consultation.hashCode();
	}

	public void reset() {
        facts = new HashSet<Fact>();
        solutions = new HashSet<ISolution>();
        clusterings  = new HashSet<Clustering<Object>>();
        pending = true;
        simple = true;
        homogenous = true;
        homogenousSchema = null;
        errorOccurred = false;
    }

    public void setConsultation(Consultation details) {
        consultation = details;
    }

    protected abstract void internalRun(IProgressMonitor monitor);
    
    protected Thread current = null;
    public void run(IProgressMonitor monitor) {
        pending = true;
        if(current != null) {
            throw new AssertionError("ICQ already running! (" + getDescription() +")");
        }
        current = Thread.currentThread();
        try {
            if(monitor.isCanceled()) { return; }
            monitor.beginTask("Performing " + getClass() + ": " + getDescription(), 100);
            Stopwatch watch = Stopwatch.createStarted();
            internalRun(new SubProgressMonitor(monitor, 50));
            watch.stop();
            if(FerretPlugin.hasDebugOption("timings")) {
                System.out.println(getClass().getName() + " time: " + watch);
            }
            if(monitor.isCanceled()) { return; }
            completed();
        } finally {
            monitor.done();
            pending = false;
            current = null;
        }
     }
    
    /**
     * Indicate query successfully completed with no exceptions.
     * Provides an opportunity to record any validation information
     * necessary.
     */
    protected void completed() {
    	/*do nothing*/
	}

	public boolean isDone() {
        return !pending;
    }

    public Set<ISolution> getSolutions() {
        return solutions;
    }
    
    public void addSolution(ISolution sol) {
        if(homogenousSchema == null) {
            homogenousSchema = sol.getSchema();
        }
        solutions.add(sol);
        homogenous = homogenous && sol.getSchema() == homogenousSchema;
        simple = simple && sol.isSimpleSolution();
    }
    
    public void addFact(Fact fact) {
        facts.add(fact);
    }

    public Set<Fact> getFacts() {
        return facts;
    }
    

    public boolean isHomogenous() {
        return homogenous;
    }

    public boolean isSimple() {
        return simple;
    }
    
    public Consultation getConsultation() {
        return consultation;
    }
    
    public Set<Clustering<Object>> getAllClusterings() {
        return clusterings;
    }
    
    public void addClustering(Clustering<Object> c) {
        clusterings.add(c);
    }

    protected ISphere getSphere() {
    	return consultation.getSphere();
    }

	public void markErrorOccurred() {
		errorOccurred = true;
	}
	
	public boolean errorOccurred() {
		return errorOccurred;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String newCategory) {
		category = newCategory;
	}

	public String toString() {
		return getClass().getSimpleName() + "{" + (pending ? "pending, " : "") 
			+ "category: " + category + ", " + consultation.toString() + "}";
	}
}
