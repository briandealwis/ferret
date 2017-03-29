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
package ca.ubc.cs.ferret.display;

import ca.ubc.cs.ferret.Consultancy;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.ICallback;
import ca.ubc.cs.ferret.model.Consultation;
import ca.ubc.cs.ferret.model.IConceptualQuery;
import java.util.ArrayList;
import java.util.List;

public class DwConsultation extends DwBaseObject {
    protected Consultation consultation;
    boolean previousCompleteStatus = false;
    boolean shouldRebuild = true;
    boolean previousSkipEmptyCQs = FerretPlugin.skipEmptyCQs();
    protected ICallback<? super Consultation> updateCallback;
    
    // Note: nothing is done with consultation in constructor -- some
    // of the conversions require setting the parent later

    protected DwConsultation() {
    	super(null);
    }

	public DwConsultation(Consultation object) {
        super(null);    // by default: no parent
        consultation = object;
    }

    public DwConsultation(Consultation object, IDisplayObject parent) {
        super(parent);
        consultation = object;
    }

    public void registerRefresh() {
        if(consultation == null || consultation.isDone()) { return; }
        if(updateCallback == null) {
            updateCallback = new ICallback<Consultation>() {
                public void run(Consultation argument) {
                	shouldRebuild = true;
                    refresh();
                }};
        }
        consultation.registerChangeCallback(updateCallback);
    }
  
    @Override
    public void dispose() {
        if(updateCallback == null) {
            consultation.removeChangeCallback(updateCallback);            
        }
        super.dispose();
    }

    @Override
    protected void buildChildren(IDisplayObject[] oldChildren) {
    	boolean isDone = consultation.isDone();
		if(!isDone && !consultation.isInProgress()) {
			consultation.getConsultancy().performConsultation(consultation, true, Consultancy.HIGH);
		}
    	IConceptualQuery cqs[] = consultation.getConceptualQueries();
        if(cqs.length == 0 && !isDone) {
            shouldRebuild = false;
            children = new IDisplayObject[] { new DwText(this, "(in progress...)", 
            		FerretPlugin.getImageDescriptor("icons/hourglass.gif")) };
            return;
        }
        List<DwConceptualQuery> queries = new ArrayList<DwConceptualQuery>(cqs.length);
        for(IConceptualQuery cq : cqs) {
        	boolean cqIsDone = cq.isDone();
        	if((FerretPlugin.showOnlyCompletedCQs() && !cqIsDone)
					|| (FerretPlugin.skipEmptyCQs() && cqIsDone
							&& cq.getSolutions().isEmpty() && cq.getFacts().isEmpty())) {
        		continue; 
        	}
        	// Try to avoid creating unnecessary display objects (so
        	// as to preserve selected elements and avoid jumping)
        	DwConceptualQuery dobj = (DwConceptualQuery)findDisplayObject(cq, oldChildren);
        	if(dobj == null) { dobj = new DwConceptualQuery(cq, this); } 
        	queries.add(dobj);
        }
        // if we're only showing completed queries then we could have a case where
        // there are queries in progress, but none have completed
        if(queries.isEmpty() && FerretPlugin.showOnlyCompletedCQs() && !consultation.isDone()) {
            children = new IDisplayObject[] { new DwText(this, "(in progress...)", 
            		FerretPlugin.getImageDescriptor("icons/hourglass.gif")) };
            return;
        }
        shouldRebuild = false;
        children = queries.toArray(new DwConceptualQuery[queries.size()]);
    }

    public String getText() {
        return consultation.toString();
    }

    public Object getObject() {
        return consultation;
    }
    
    @Override
	public boolean hasChildren() {
        registerRefresh();
		return true;
	}

	protected boolean shouldRebuildChildren() {
        if(super.shouldRebuildChildren()) { return true; }
        boolean oldCompletionStatus = previousCompleteStatus;
        boolean oldSkipStatus = previousSkipEmptyCQs;
        // store the values for the next run -- note that they're current for now though
        // check skip status in case that's changed too
        previousCompleteStatus = consultation.isDone();
        previousSkipEmptyCQs = FerretPlugin.skipEmptyCQs();
        return shouldRebuild || oldCompletionStatus != previousCompleteStatus
        	|| oldSkipStatus != previousSkipEmptyCQs;
    }

	public void setConsultation(Consultation c) {
		consultation = c;
		registerRefresh();
	}
}
