/*
 * Copyright 2004  University of British Columbia
 * @author bsd
 */
package ca.ubc.cs.ferret.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.SingletonMap;
import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.IMemento;

import ca.ubc.cs.ferret.Consultancy;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.ICallback;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;

/**
 * This class represents a consultation to the great guru of how a particular set of
 * query elements relates to the rest of the system.  It has a three-phase lifecycle:
 * created, activated (queries-in-progress), and done.
 * @author Brian de Alwis
 */
public class Consultation {
	protected Object originalElements[];
	protected FerretObject queryElements[];
	protected ISphere sphere;
	protected IConceptualQuery conceptualQueries[];
	protected IMemento navigationHistory;
	protected Set<ICallback<? super Consultation>> callbacks;
	protected enum Lifecycle { Created, Querying, Done };
	protected Lifecycle phase;
	
	public Consultation(ISphere _sphere) {
	    sphere = _sphere;
	    reset();
	}
	
	public synchronized void reset() {
		phase = Lifecycle.Created;
		conceptualQueries = null;
		// regenerate queryElements as necessary, e.g., if the sphere has changed
		queryElements = null;		
//		fireUpdateBlocks();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Consultation && 
			originalElements.equals(((Consultation)obj).originalElements);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for(Object o : originalElements) { 
			hash = 37 * hash + o.hashCode(); 
		}
		return hash;
	}

	/**
	 * Register a callback on every change of this consultation.
	 * The provided argument will be this consultation.
	 * @param callback the callback for any change
	 */
	public void registerChangeCallback(ICallback<? super Consultation> callback) {
        if(callbacks == null) { callbacks = new HashSet<ICallback<? super Consultation>>(); }
        callbacks.add(callback);
    }
    
    public void removeChangeCallback(ICallback<? super Consultation> callback) {
        if(callbacks == null) { return; }
        callbacks.remove(callback);
    }

	public void run(IProgressMonitor monitor) {
        if(isDone() && isValid()) { return; }
        phase = Lifecycle.Querying;
        boolean timingMessages = FerretPlugin.hasDebugOption("timings");
        try {
	        monitor.beginTask("Querying", 20);
            StopWatch sw = new StopWatch();
	        if(timingMessages) {
	        	System.out.println("Building queries...");
	        	sw.start();
	        }
	        buildConceptualQueries(new SubProgressMonitor(monitor, 10));
	        if(timingMessages) {
	        	sw.stop();
	        	System.out.println("Building queries took " + sw.toString());
	        	sw.reset(); sw.start();
		        System.out.println("Running queries...");
	        }
	        runConceptualQueries(new SubProgressMonitor(monitor, 10));
	        if(timingMessages) {
	        	sw.stop();
	        	System.out.println("Running queries took: " + sw.toString());
	        }
        	phase = Lifecycle.Done;
        } finally {
        	monitor.done();
        }
        fireUpdateBlocks();
	}
	
	protected void runConceptualQueries(IProgressMonitor monitor) {
		IConceptualQuery queries[] = conceptualQueries;
		if(queries == null) { return; }	// may have been reset, so abort
        monitor.beginTask("Performing queries", queries.length);
        boolean debugMessages = FerretPlugin.hasDebugOption("debug/issuingConceptualQuery");
        boolean timingMessages = FerretPlugin.hasDebugOption("timings");
        Set<IConceptualQuery> toBeRemoved = null;
        try {
            for(IConceptualQuery cq : queries) {
                if(monitor.isCanceled()) { break; }
                if(cq.isDone()) {
                	if(cq.isValid()) { continue; }
                	cq.reset();
                }
                monitor.subTask(cq.getDescription());
                StopWatch sw = new StopWatch();
				if(debugMessages) { System.out.println("ICQ: " + cq.getDescription()); }
                sw.start();
                try {
                    cq.run(new SubProgressMonitor(monitor, 1));
                } catch(OperationCanceledException e) {
//                	if(timingMessages) { System.out.println("ICQ Time taken: " + sw + " (" + cq.getDescription() + ")"); }
                	if(debugMessages) { System.out.println("ICQ cancelled"); }
                    cq.reset();
                    return;
                } catch(UnsupportedOperationException e) {
                	if(debugMessages) {
	                	FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 254,
	                			"ICQ requires unsupported operation: " + cq, e));
                	}
                	if(toBeRemoved == null) { toBeRemoved = new HashSet<IConceptualQuery>(); }
                	toBeRemoved.add(cq);
                    cq.reset();
                    /*FALLTHROUGH*/
                } catch(Exception e) {
                	FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 255,
                			"Exception while executing ICQ " + cq, e));
                    cq.reset();
                    cq.markErrorOccurred();
                    /*FALLTHROUGH*/
                }
                sw.stop();
            	if(timingMessages) { System.out.println("ICQ Time taken: " + sw + " (" + cq.getDescription() + ")"); }
            	if(debugMessages) { System.out.println("ICQ finished"); }
                fireUpdateBlocks();
            }
            if(toBeRemoved != null) {
            	synchronized(this) {
            		if(conceptualQueries == null) { return; }
	            	IConceptualQuery revisedCQs[] = new IConceptualQuery[conceptualQueries.length - toBeRemoved.size()];
	            	int index = 0;
	            	for(IConceptualQuery cq : conceptualQueries) {
	            		if(!toBeRemoved.contains(cq)) {
	            			revisedCQs[index++] = cq;
	            		}
	            	}
	            	conceptualQueries = revisedCQs;
            	}
            	fireUpdateBlocks();
            }
        } finally {
        	monitor.done();
        }
	}
    
    public void buildConceptualQueries(IProgressMonitor monitor) {
    	synchronized(this) {
    		if(conceptualQueries != null) { return; }
    		List<IConceptualQuery> queries = new ArrayList<IConceptualQuery>();
    		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    		for(IConfigurationElement element : extensionRegistry.getConfigurationElementsFor(FerretPlugin.pluginID, FerretPlugin.conceptualQueriesExtensionPointId)) {
    			if(monitor.isCanceled()) { throw new OperationCanceledException(); }
    			IConceptualQuery query = createConceptualQuery(getQueryElements(), element);
    			if(query != null) { queries.add(query); }
    		}

    		for(IConceptualQuery q : queries) {
    			q.setConsultation(this);
    		}
    		conceptualQueries = queries.toArray(new IConceptualQuery[queries.size()]);
    	}
        fireUpdateBlocks();
    }

    protected IConceptualQuery createConceptualQuery(FerretObject[] queryElements, IConfigurationElement element) {
    	try {
	    	IConfigurationElement children[] = element.getChildren();
	    	if(children.length == 0) {
	    		FerretPlugin.log(new Status(IStatus.WARNING, element.getNamespaceIdentifier(),
	    				FerretErrorConstants.CONTRACT_VIOLATION, 
	    				"ICQ missing parameter description [ICQ id=" + element.getAttribute("id") + " defined in "
	    				+ element.getNamespaceIdentifier() + "]", null));
	    		return null;
	    	} else if(children.length != 1) {
	    		FerretPlugin.log(new Status(IStatus.WARNING, element.getNamespaceIdentifier(),
	    				FerretErrorConstants.CONTRACT_VIOLATION, 
	    				"ICQ cannot support multiple parameters [ICQ id=" + element.getAttribute("id") + " defined in "
	    				+ element.getNamespaceIdentifier() + "]", null));
	    		return null;
	    	}
	    	IConfigurationElement parmSpec = children[0];
	    	String parmClassName = parmSpec.getAttribute("class"); 
	    	if(parmClassName == null) { parmClassName = FerretObject.class.getName(); }
	    	if(parmSpec.getAttribute("id") != null && !IConceptualQuery.DEFAULT_PARAMETER.equals(parmSpec.getAttribute("id"))) {
	    		FerretPlugin.log(new Status(IStatus.ERROR, element.getNamespaceIdentifier(),
						FerretErrorConstants.CONTRACT_VIOLATION,
						"ICQs currently don't support named parameters [ICQ id="
								+ element.getAttribute("id") + " defined in "
								+ element.getNamespaceIdentifier() + "]", null));
	    		return null;    		
	    	}
	    	ArrayList<Object> convertedObjects = new ArrayList<Object>();
	    	Class<?> parmClass = null;
	    	Fidelity desiredFidelity = parmSpec.getAttribute("fidelity") != null ?
	    			Fidelity.fromString(parmSpec.getAttribute("fidelity")) : Fidelity.Exact;
	    	for(int i = 0; i < queryElements.length; i++) {
	    		ConversionResult<?> cr = queryElements[i].convert(parmClassName, 1, desiredFidelity);
	    		if(cr != null) {
		    		convertedObjects.addAll(cr.getResults());
		    		parmClass = cr.getResultClass();
	    		}
	    	}
	    	if(convertedObjects.isEmpty() || !checkElementCounts(convertedObjects.size(), parmSpec)) {
	    		return null;
	    	}
            // support enablement expressions?
            // Expression expression= ExpressionConverter.getDefault().perform(enablementElement);
            IConceptualQuery icq = (IConceptualQuery)element.createExecutableExtension("class");
            icq.setCategory(element.getAttribute("category"));
            Object[] convertedObjectsAsArray = convertedObjects.toArray(
            		(Object[])Array.newInstance(parmClass, convertedObjects.size()));
            if(!icq.setParameters(new SingletonMap<String, Object[]>(
					IConceptualQuery.DEFAULT_PARAMETER, 
					convertedObjectsAsArray))) {
            	return null;
            }
            return icq;
    } catch(ClassCastException e) {
            FerretPlugin.log(new Status(IStatus.ERROR, element.getNamespaceIdentifier(), 
                    FerretErrorConstants.CONTRACT_VIOLATION, "extension does not implement " + IConceptualQuery.class.getName(), e));
        } catch(CoreException e) {
            FerretPlugin.log(e.getStatus());
        } catch(Exception e) {
            FerretPlugin.log(e);
        }
        return null;
    }

    protected static boolean checkElementCounts(int providedCount,
			IConfigurationElement parameterSpecification) {
    	String enablesFor = parameterSpecification.getAttribute("enablesFor");
    	if(enablesFor == null) { enablesFor = parameterSpecification.getAttribute("count"); }
    	int elementCount = 1;
    	boolean orMore = false;
    	if(enablesFor != null) {
    		if(enablesFor.equals("*")) {
    			elementCount = 0; orMore = true;
    		} else if(enablesFor.equals("!")) {
    			elementCount = 0; orMore = false;
    		} else if(enablesFor.equals("+")) {
    			elementCount = 1; orMore = true;
    		} else if(enablesFor.equals("1")) {
    			elementCount = 1; orMore = false;
    		} else if(enablesFor.equals("multiple")) {
    			elementCount = 2; orMore = true;
    		} else if(enablesFor.equals("?")) {
    			return providedCount >= 0 && providedCount <= 1;
    		} else {
        		int index = 0;
        		elementCount = 0;
        		while(index < enablesFor.length() 
        				&& Character.isDigit(enablesFor.charAt(index))) {
        			elementCount = 10 * elementCount + Character.digit(enablesFor.charAt(index++), 10);
        		}
        		if(index < enablesFor.length() && enablesFor.charAt(index) == '+') {
        			orMore = true;
        			index++;
        		}
        		if(index != enablesFor.length()) {
            		FerretPlugin.log(new Status(IStatus.ERROR, parameterSpecification.getNamespaceIdentifier(), 
            				FerretErrorConstants.CONTRACT_VIOLATION,
            				"Invalid enablesFor specification: \"" + enablesFor + "\"", null));
            		return false;
        		}
    		}
    	}
    	return (orMore && providedCount >= elementCount)
    		|| (!orMore && providedCount  == elementCount);
	}


    
	public boolean isDone() {
		if(phase != Lifecycle.Done) { return false; }
		return conceptualQueries != null;
    }
	
	public synchronized boolean isValid() {
		if(conceptualQueries == null) { return false; }
    	for(IConceptualQuery q : conceptualQueries) {
    		if(!q.isDone() || !q.isValid()) { 
    			return false;
			}
    	}
		return true;
	}
    
    public boolean isAwaited() {
    	return getConsultancy().isAwaited(this);
    }
    
    /**
     * @return the queryElements
     */
    public FerretObject[] getQueryElements() {
    	if(queryElements == null) {
			queryElements = new FerretObject[originalElements.length];
	        for(int i = 0; i < originalElements.length; i++) {
	        	if(originalElements[i] instanceof FerretObject) {
	        		if(((FerretObject)originalElements[i]).getSphere() == sphere) {
	        			queryElements[i] = (FerretObject)originalElements[i];
	        		} else {
	        			queryElements[i] =
	        				new FerretObject(((FerretObject)originalElements[i]).getPrimaryObject(), sphere);
	        		}
	        	} else {
	        		queryElements[i] = new FerretObject(originalElements[i], sphere);
	        	}
	        }
        }
        return queryElements;
    }
    
    /**
     * @return Returns the conceptualQueries.
     */
    public synchronized IConceptualQuery[] getConceptualQueries() {
    	if(conceptualQueries == null) { return new IConceptualQuery[0]; }
        return conceptualQueries;
    }
    
    /**
     * Return the composite sphere.
     */
	public ISphere getSphere() {
        return sphere;
	}

    /**
     * Set the sphere; this will not have any effect on already-completed queries
     */
	public void setSphere(ISphere sph) {
		if(sphere != sph) {
			reset();	// we need to regen the queryElements and the ICQs
		}
        sphere = sph;
	}

    /**
     * @param conceptualQueries The conceptualQueries to set.
     */
    public synchronized void setConceptualQueries(IConceptualQuery[] conceptualQueries) {
        this.conceptualQueries = conceptualQueries;
    }
    
    public String toString() {
        StringBuffer desc = new StringBuffer();
        desc.append("Consultation(");
        if(!isDone()) { desc.append("!"); }
        desc.append("done: ");
        desc.append(FerretPlugin.compactPrettyPrint(getQueryElements()));
        desc.append(')');
        return desc.toString();
    }
    
    public void cancel() {
        getConsultancy().abandon(this);
    }

    public void boostPriority() {
        getConsultancy().boost(this);
    }
    
    public int getPriority() {
    	return getConsultancy().getPriority(this);
    }

	public Consultancy getConsultancy() {
		return Consultancy.getDefault();
	}

    protected Map<String,Integer> categoryRanks;
	public int getCategoryRank(String category) {
		if(categoryRanks == null) { buildCategoryRanks(); }
		if(categoryRanks == null) { return 0; }
		Integer value = categoryRanks.get(category);
		if(value == null) { return 0; }
		return value.intValue();
	}
	
	protected synchronized void buildCategoryRanks() {
		if(conceptualQueries == null) { return; }
		categoryRanks = new HashMap<String,Integer>();
		Set<String> unsortedCategories = new HashSet<String>();
		for(IConceptualQuery icq : conceptualQueries) { unsortedCategories.add(icq.getCategory()); }
		List<String> sortedCategories = new ArrayList<String>(unsortedCategories);
		// OK, perhaps we should allow this to be configurable by the user
		Collections.sort(sortedCategories);
		for(int i = 0; i < sortedCategories.size(); i++) {
			categoryRanks.put(sortedCategories.get(i), i);
		}
	}

	public boolean isInProgress() {
		return getConsultancy().isInProgress(this);
	}

	public void setOriginalElements(Object[] elements) {
		originalElements = elements;
		if(elements == null) {
			queryElements = null;
			return;
		}
	}

	public Object[] getOriginalElements() {
		return originalElements;
	}

	public void fireUpdateBlocks() {
		if(callbacks == null) { return; }
		for (ICallback<? super Consultation> callback : callbacks) {
			try {
				callback.run(this);
			} catch(Exception e) {
				FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 3,  
						"Exception while invoking provided consultation-update notification", e));                
			}
		}
	}

    public IMemento getNavigationHistory() {
		return navigationHistory;
	}

	public void setNavigationHistory(IMemento navigationHistory) {
		this.navigationHistory = navigationHistory;
	}

}
