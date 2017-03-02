/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.display;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Multimap;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.IClusteringsProvider;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.Fact;
import ca.ubc.cs.ferret.model.IConceptualQuery;
import ca.ubc.cs.ferret.model.ISolution;
import ca.ubc.cs.ferret.preferences.IFerretPreferenceConstants;
import ca.ubc.cs.ferret.views.DossierConstants;

public class DwConceptualQuery extends DwClusterableCollection<Object> {
    protected IConceptualQuery icq;
    protected boolean previousCompleteStatus = false;
    protected boolean doSimple;
    
    public DwConceptualQuery(IConceptualQuery query, IDisplayObject parent) {
        super(parent);
        icq = query;
    }

    public String getText() {
        StringBuffer desc = new StringBuffer();
        desc.append(icq.getDescription());
        if(icq.errorOccurred()) {
        	desc.append(" [error occurred]");
        } else if(!icq.isDone()) {
        	desc.append(" [in progress]");
        }
        return desc.toString();
    }

    @Override
    protected void buildChildren(IDisplayObject[] oldChildren) {
        if(!icq.isDone()) { 
            children = new IDisplayObject[] { new DwText(this, "(in progress...)", 
            		FerretPlugin.getImageDescriptor("icons/hourglass.gif")) };
            return;
        }
        super.buildChildren(oldChildren);
    }
    
    protected IDisplayObject buildChild(Object child) {
    	if(child instanceof ISolution) {
    		return new DwSolution((ISolution)child, this);
    	} else {
    		return DwObject.forObject(child, this);
    	}
    }
    
    
    public Object getObject() {
        return icq;
    }
        
    protected boolean showFacts() {
    	return FerretPlugin.getDefault().getPreferenceStore()
    		.getBoolean(IFerretPreferenceConstants.PREF_SHOW_FACTS);
    }
    
    protected void addFacts(List<IDisplayObject> kids) {
    	if(!showFacts()) { return; }
        if(icq.getFacts().size() > 3) {
            kids.add(new DwFactList(icq.getFacts(), this));
        } else {
            for(Fact fact : icq.getFacts()) {
                kids.add(new DwFact(fact, this));
            }
        }        
    }
    
    public IConceptualQuery getQuery() {
        return icq;
    }
    

    @Override
    protected boolean shouldRebuildChildren() {
//    	need to ensure previousCompleteStatus is set whenever a rebuild
//    	is done to avoid needless rebuilds
        boolean oldStatus = previousCompleteStatus; 
        previousCompleteStatus = icq.isDone();	
        if(super.shouldRebuildChildren()) { return true; }
        return oldStatus != previousCompleteStatus;
    }

    @Override
    public int getImportance() {
        // TODO Auto-generated method stub
        return super.getImportance() - (icq.isDone() ? 5 : 0);
    }

	@Override
	public ImageDescriptor getImage() {
		if(icq.errorOccurred()) {
			return PlatformUI.getWorkbench().getSharedImages()
			.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
		} else if(!icq.isDone()) {			
			return FerretPlugin.getDefault().getImageRegistry()
			.getDescriptor(DossierConstants.IMG_QUERY_IN_PROGRESS);
		}
		return FerretPlugin.getDefault().getImageRegistry()
			.getDescriptor(DossierConstants.IMG_QUERY);
	}

	@Override
	public boolean hasChildren() {
		if(children == null) { return true; }
		return super.hasChildren();
	}
	
	public String getToolTip() {
	   	if(!icq.isDone() || !showFacts() || icq.getFacts().isEmpty()) {
	   		return super.getToolTip(); 
   		}
		StringBuffer buf = new StringBuffer();
		for(Fact f : icq.getFacts()) {
			buf.append(f.getFact());
			buf.append('\n');
		}
		return buf.toString();
	}

	@Override
	protected Collection<Object> getClusterableElements() {
		Collection<ISolution> solutions = icq.getSolutions();
        if(!DwSolution.allAreSimpleSolutions(solutions)) { 
        	return new ArrayList<Object>(solutions);
    	}
		Collection<Object> primaries = new ArrayList<Object>(solutions.size());
		for(ISolution sol : solutions) { primaries.add(sol.getPrimaryEntity()); }
		return primaries;
	}

	@Override
	protected void buildClusterings(IProgressMonitor monitor, Multimap<IClusteringsProvider<Object>, Clustering<Object>> newClusterings) {
		for(Clustering<Object> cp : icq.getAllClusterings()) {
			if(cp.isRelevant()) {
				newClusterings.put(icq, cp);
			}
		}
		super.buildClusterings(monitor, newClusterings);
	}

	@Override
	public Multimap<IClusteringsProvider<Object>, Clustering<Object>> getAllClusterings() {
		if(!icq.isDone()) { return null; }
		return super.getAllClusterings();
	}
}
