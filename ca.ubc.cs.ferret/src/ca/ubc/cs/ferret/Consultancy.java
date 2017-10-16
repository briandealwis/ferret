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
import ca.ubc.cs.ferret.model.IConceptualQuery;
import ca.ubc.cs.ferret.model.ISolution;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.preferences.IFerretPreferenceConstants;
import ca.ubc.cs.ferret.types.FerretObject;
import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A Consultancy is a single repository of the consultations made throughout the image.
 * It's responsible for automatically re-issuing new consultations, as well as managing
 * ongoing consultations.
 */
public class Consultancy {
    public final static int HIGH = 0;
    public final static int MEDIUM = 50;
    public final static int BACKGROUND = 100;

	protected static Consultancy singleton;
    protected Collection<IConsultancyClient> clients;
    protected Map<Set<?>,Consultation> consultationCache = null;
    protected Job backgroundJob = null;
    protected PriorityQueue<WorkUnit> outstandingConsultations = null;
    protected WorkUnit activeWork = null;

	private AtomicInteger pendingResets = new AtomicInteger(0);
	private ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

    /**
     * Consultancy is meant to be a singleton.  Access the singleton instance through
     * Consultancy#getDefault()
     */
    private Consultancy() {
        reset();
    }

    /**
     * Return the singleton instance of this consultancy.
     * @return the singleton instance
     */
    public static Consultancy getDefault() {
    	if(singleton == null) {
    		singleton = new Consultancy();
    		singleton.start();
    	}
    	return singleton;
	}
    
    /**
     * Record the running state of the consultancy: if stopped, then no
     * actions should be performed.  This is assumed to happen only at the
     * end of this plug-in's lifecycle.
     */
    protected boolean stopped = false;
    
    /**
     * Helper function to fetch the Eclipse workspace
     * @return the workspace
     */
    protected IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }
    
    protected IResourceChangeListener resourceChangeListener;
    
	/**
	 * @since 0.5
	 */
    public static void shutdown() {
        if (singleton == null) {
            return;
        }
        singleton.stop();
        singleton = null;
    }

    /**
     * Cause all consultancy activity to stop.
     */
    public void stop() {
        stopped = true;
        if(resourceChangeListener != null) {
            getWorkspace().removeResourceChangeListener(resourceChangeListener);
            resourceChangeListener = null;
        }
        if(backgroundJob != null) {
            backgroundJob.cancel();
        }
		if(executor != null) {
			executor.shutdown();
		}
    }
    
    public void start() {
    	if(clients != null) { return; }	// already started
    	clients = new HashSet<IConsultancyClient>();
        resourceChangeListener = new IResourceChangeListener() {
            public void resourceChanged(IResourceChangeEvent event) {
                ModificationVisitor visitor = new ModificationVisitor();
                try {
                    event.getDelta().accept(visitor);
                    if(FerretPlugin.hasDebugOption("debug/cacheMaintenance")) {
                    	System.out.println("Workspace changed: am " + (visitor.wasModified() ? "" : "not ") 
                    			+ "resetting consultation cache");
                    }
                    if(visitor.wasModified()) {
						scheduleReset();
                    }
                } catch(CoreException e) {
                    FerretPlugin.log(new Status(IStatus.WARNING, FerretPlugin.pluginID, 4,
                            "Exception while determining impact of workspace change", e));
                    if(FerretPlugin.hasDebugOption("debug/cacheMaintenance")) {
                    	System.out.println("Exception while investigating workspace change; resetting consultation cache");
                    }
					scheduleReset();
                }
            }};
        getWorkspace().addResourceChangeListener(resourceChangeListener,
                IResourceChangeEvent.POST_CHANGE);
    }
    
    protected boolean canSearchInBackground() {
        return FerretPlugin.shouldSupportBackgroundRelatedQueries();
    }
    
    protected int getMaximumBackgroundCount() {
    	return FerretPlugin.getMaximumBackgroundCount();
    }
    
	/**
	 * @since 0.5
	 */
	protected void scheduleReset() {
		Runnable r = new Runnable() {
			public void run() {
				int pending = pendingResets.decrementAndGet();
				if(pending == 0) {
					reset();
				}
			}
		};
		pendingResets.getAndIncrement();
		executor.schedule(r, 500, TimeUnit.MILLISECONDS);
	}

    /**
     * Reset the consultancy, generally to avoid inconsistency such as upon workspace
     * change.
     */
    public synchronized void reset() {
        /* TODO: Question: should we really throw away consultations?  Or could we do something
         * smarter to figure out impact resource-changes have and trim appropriate consultations? */
    	/* Note: don't have to explicit reset individual consultations: each ICQ is provided
    	 * the opportunity to check whether they are still current (isValid()), which can
    	 * cooperate with their sphere. */
    	abandonBackground();
    	if(consultationCache == null) {
    		consultationCache = new MapMaker().weakValues().makeMap();
    	} else {
    		for(Consultation c : consultationCache.values()) { c.reset(); }
    	}
        outstandingConsultations = new PriorityQueue<WorkUnit>();
        if(clients != null) {	// clients is used as a proxy as to whether instance is started
	        for(IConsultancyClient client : clients) {
	        	client.consultancyReset();
	        }
        }
    }
    
    /**
     * Issue a request for a consultation for the provided elements.  The provided
     * callback is called upon completion of the consultation.  The consultation is
     * also returned; its completion can be tested with Consultation#isDone().
     * Note that it is possible that the callback may be issued before this method
     * returns.
     * @param queryElements
     * @param changedRunnable
     * @return the consultation
     */
    public Consultation getConsultation(Object queryElements[],
    		ISphere sphere) {
    	if(sphere == null) {
    		throw new FerretFatalError("Consultancy.getConsultation() provided null sphere");
    	}
//      ICallback finishedRunnable, boolean finishedEntailsWork, int priority) {
        if(queryElements.length == 0) { return null; }
        Consultation c = null;
        // Lists support hashCode() and equals()
        Set<Object> cacheKey = new HashSet<Object>();
        Collections.addAll(cacheKey, queryElements);
        cacheKey.add(sphere);
        if((c = (Consultation)consultationCache.get(cacheKey)) == null) {
            c = createConsultation(queryElements, sphere);
            consultationCache.put(cacheKey, c);
        } else {
        	c.setSphere(sphere);
        }
        return c;
    }   

    protected void startBackgrounder(final Consultation c) {
		if (!canSearchInBackground()) {
			return;
		}
    	if(!c.isDone()) { return; }
        if(c.getPriority() >= BACKGROUND) { return; }
        Job j = new Job("Lodging background consultations") {
            protected IStatus run(IProgressMonitor monitor) {
            	lodgeBackgroundSearches(c, monitor);
                return Status.OK_STATUS;
            }};
        j.setPriority(Job.DECORATE);
        j.schedule();
    }

	protected void lodgeBackgroundSearches(Consultation c, IProgressMonitor monitor) {
		if(!canSearchInBackground()) { return; }
        monitor.beginTask("Lodging background tasks", c.getConceptualQueries().length);
        try {
        	Set<Object> objects = new HashSet<Object>();
            for(IConceptualQuery cq : c.getConceptualQueries()) {
            	if(!cq.isDone()) { continue; }
        		for(ISolution sol : cq.getSolutions()) {
        			for(Object o : sol.getEntities().values()) {
        				objects.add(o);
        			}
        		}
            }

            // Convert Set into List -- assume this will randomize order enough
            List<Object> randomized = new ArrayList<Object>(objects);
            int count = getMaximumBackgroundCount() - outstandingConsultations.size();
            while(count > 0 && !randomized.isEmpty()) {
            	Object o = randomized.remove(0);
            	if(o instanceof FerretObject) {
            		o = ((FerretObject)o).getPrimaryObject();
            	}
           		if(!FerretPlugin.isCommonElement(o)) {
           			startBackgrounder(o, c.getSphere(), BACKGROUND);
           			count--;
           		}
            }
        } finally {
            monitor.done(); 
        }
	}

	public synchronized int getPriority(Consultation c) {
		WorkUnit wu = activeWork;
		if(wu != null && wu.consultation == c) {
			return wu.getPriority();
		}
		wu = fetchWork(c);
		if(wu == null) { return BACKGROUND; }
		int pri = wu.getPriority();
	    submitWork(wu);	// don't lose it!
		return pri;
	}
	
	protected int lowerPriority(int prio) {
		return prio + 5;
	}
	
	protected int higherPriority(int prio) {
		return prio - 5;
	}

	/**
     * So we are (or have; it may have completed already) issuing a consultation 
     * on this element already.  Issue consultations on our siblings as well as our children.
     * @param element
     */
    protected void startBackgrounder(Object element, ISphere sphere, int priority) {
        if(!(element instanceof Object[])) {
            element = new Object[] { element };
        } else if(element instanceof Collection) {
        	element = ((Collection<?>)element).toArray();
        }
        Consultation c = getConsultation((Object[])element, sphere);
        basicPerformConsultation(c, false, priority);
    }

    public void boost(Consultation c) {
        WorkUnit wu = fetchWork(c);
        if(wu == null) { return; }
        wu.setPriority(higherPriority(wu.getPriority()));
        submitWork(wu);
    }
    
    /**
	 * Create a consultation for the provided objects.  The resulting
	 * consultation is *not* registered with the consultation cache.
	 * This is not the recommended way for obtaining consultations.
	 */
    public Consultation createConsultation(Object queryElements[], ISphere sphere) {
        if(FerretPlugin.hasDebugOption("debug/issuingConsultations")) {
            System.out.println("Building consultation for [" + FerretPlugin.compactPrettyPrint(queryElements) + "]");
        }
        
        Consultation c = new Consultation(sphere);
        c.setOriginalElements(queryElements);
        return c;
    }

	public void performConsultation(Consultation c) {
    	performConsultation(c, false, MEDIUM);
    }

    public void performConsultation(Consultation c, boolean finishedEntailsWork) {
    	performConsultation(c, finishedEntailsWork, MEDIUM);
    }
    
    /**
     * 
     * @param consult the consultation to be performed
     * @param changedRunnable callback when consultation is completed
     * @param changedEntailsWork if true then work must actually be performed for the 
     * 	changedRunnable callback to be invoked
     * @param priority the priority for performing the consultation
     */
    public void performConsultation(Consultation consult, boolean changedEntailsWork, int priority) {
        abandonBackground();
        basicPerformConsultation(consult, changedEntailsWork, priority);
        startBackgrounder(consult);		// in case it's done already
    }

    public void basicPerformConsultation(Consultation consult, 
            boolean changedEntailsWork, int priority) {

        if(consult.isDone() && consult.isValid()) {
            try {
                if(!changedEntailsWork) { consult.fireUpdateBlocks(); }
            } catch(Exception e) {
                FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID,
                		FerretErrorConstants.EXCEPTION_HANDLED,  
                        "Exception while invoking provided consultation-finished notification", e));                
            }
            return;
		} else if (stopped) {
			FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, FerretErrorConstants.PLATFORM_NOT_RUNNING,
					"Aborting consultation: not running", null));
			return;
        }
        
	    WorkUnit wu;
	    if((wu = fetchWork(consult)) != null) {
	    	wu.setPriority(priority);
	    } else {
	    	wu = new WorkUnit(consult, priority);
	    }
	    submitWork(wu);
        kick();
    }

	protected synchronized void kick() {
        if(stopped) { return; }
        if(backgroundJob != null) {
        	if(backgroundJob.getState() != Job.NONE) { return; }
        	FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 256,
        			"Ferret background job present but not running: restarting", null));
        	backgroundJob.cancel();
        	activeWork = null;
    	}
        if(activeWork != null) {
    		throw new FerretFatalError("activeWork should be null!");
		}
        if((activeWork = fetchNextWork()) == null) {
            if(FerretPlugin.hasDebugOption("debug/issuingConsultations")) {
            	System.out.println("Background consultations exhausted");
            }
            return;
        } 
        if(FerretPlugin.hasDebugOption("debug/issuingConsultations")) {
        	System.out.println("Another further " + outstandingConsultations.size()
        			+ " consultations outstanding");
        }
        String jobName = activeWork.getPriority() < BACKGROUND
        	?  "Running Consultation" : "Running Background Consultation";
        if(verboseJobTitle()) {
        	try {
	        	jobName = jobName + ": " + FerretPlugin.prettyPrint(activeWork.getConsultation().getQueryElements())
	        		+ " [" + outstandingConsultations.size() + "]";
        	} catch(Throwable t) {
        		FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
        				"Unexpected error while generating job name", t));
        	}
        }
        backgroundJob = new Job(jobName) {
            public IStatus run(IProgressMonitor monitor) {
				if (stopped) {
            		return new Status(IStatus.INFO, FerretPlugin.pluginID, FerretErrorConstants.PLATFORM_NOT_RUNNING,
            				"Background query process aborting: platform not running", null);
        		}
                WorkUnit current = activeWork;
                if(FerretPlugin.hasDebugOption("debug/issuingConsultations")) {
                	System.out.println("Processing["+ current.getPriority() + "]: " + 
                			FerretPlugin.debugPrint(activeWork.getConsultation().getOriginalElements()));
                }
                current.setMonitor(monitor);
                try {
                    current.getConsultation().run(monitor);
                    startBackgrounder(current.getConsultation());
                } catch(OperationCanceledException e) {
                    /* do nothing */
                } catch(Exception e) {
                    FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 2,  
                            "Exception while invoking provided consultation-finished notification", e));
                }
                if(activeWork != current) { throw new FerretFatalError("activeWork changed underneath job"); }
                activeWork = null;
                backgroundJob = null;
                kick();
                return Status.OK_STATUS;
            }
        };
        backgroundJob.setPriority(Job.DECORATE);
        if(activeWork.getPriority() <= MEDIUM && 
        		FerretPlugin.hasDebugOption("debug/issuingConsultations")) {
        	System.out.println("User-initiated: " + activeWork.toString());
        }
        backgroundJob.setSystem(activeWork.getPriority() > MEDIUM
        		&& !backgroundJobsAreUserJobs());
        backgroundJob.schedule();
    }
    
    protected boolean verboseJobTitle() {
		return FerretPlugin.getDefault().getPreferenceStore()
		.getBoolean(IFerretPreferenceConstants.PREF_VERBOSE_JOB_TITLES);
	}

	protected boolean backgroundJobsAreUserJobs() {
		return FerretPlugin.getDefault().getPreferenceStore()
			.getBoolean(IFerretPreferenceConstants.PREF_BACKGROUND_JOBS_AS_USER);
	}

	public void abandon(Consultation c) {
        WorkUnit w = activeWork;
		if (w != null && c.equals(w.getConsultation())) {
			w.cancel();
		} else if ((w = fetchWork(c)) != null) {
            w.cancel();
        }
//        abandonBackground();
    }
    
    public synchronized void abandonBackground() {
    	PriorityQueue<WorkUnit> newQueue = new PriorityQueue<WorkUnit>();
    	if(outstandingConsultations != null) {
    		for(WorkUnit wu : outstandingConsultations) {
    			// Only take those whose priority is higher than background
    			// or are actively being waited for
    			if(wu.getPriority() < BACKGROUND && isAwaited(wu.getConsultation())) {
    				newQueue.offer(wu);
    			}
    		}
    	}
        outstandingConsultations = newQueue;
    }
    
    public boolean isAwaited(Consultation consultation) {
		for(IConsultancyClient client : clients) {
			if(client.isAwaiting(consultation)) {
//				System.out.println("Client " + client + " is awaiting " + consultation);
				return true;
			}
		}
		return false;
	}

	/**
     * Find the WorkUnit associated with the provided consultation.  Return null if no
     * work unit is found.
     * @param consult
     * @return the associated work unit
     */
    private synchronized WorkUnit fetchWork(Consultation consult) {
        for (Iterator<WorkUnit> iter = outstandingConsultations.iterator(); iter.hasNext();) {
            WorkUnit wu = iter.next();
            if(consult.equals(wu.getConsultation())) {
                iter.remove();
                // outstandingConsultations.remove(wu);
                return wu;
            }
        }
        return null;
    }
    
    private synchronized WorkUnit fetchNextWork() {
        while(!outstandingConsultations.isEmpty()) {
            WorkUnit wu = outstandingConsultations.remove();
            if(!wu.getConsultation().isDone() || !wu.getConsultation().isValid()) { return wu; }
        }
        return null;
    }
    
    protected synchronized void submitWork(WorkUnit unit) {
         if(unit.getConsultation().isDone() && unit.getConsultation().isValid()) { 
        	 //throw new AssertionError("Submitting already-done work!"); 
        	 return;
    	 }
        if(stopped) { return; }
        if(outstandingConsultations.contains(unit)) { return; }
        outstandingConsultations.offer(unit);
        if(activeWork == null) { return; }  // highest-priority will be picked up on subsequent kick()
        
        WorkUnit top = outstandingConsultations.peek();
        if(top == null) { throw new AssertionError("outstanding work queue should have one element!"); }
        if(top.compareTo(activeWork) < 0) {	// is top-most higher priority than active?
        	activeWork.cancel();							// if so, cancel active
        	if(isAwaited(activeWork.getConsultation())) {
        		outstandingConsultations.offer(activeWork);	// ensure outstanding is eventually completed
        	}
    	}
        /* and will be picked up on the subsequent kick() */
    }
    
	public void reconfigure() {
		abandonBackground();
	}

	public void addClient(IConsultancyClient c) {
		clients.add(c);
	}
	
	public void removeClient(IConsultancyClient c) {
		clients.remove(c);
	}

	public synchronized boolean isInProgress(Consultation consultation) {
		if(activeWork != null && activeWork.getConsultation() == consultation) {
			return true;
		}
		for(WorkUnit wu :  outstandingConsultations) {
			if(wu.getConsultation() == consultation) { return true; }
		}
		return false;
	}

}
