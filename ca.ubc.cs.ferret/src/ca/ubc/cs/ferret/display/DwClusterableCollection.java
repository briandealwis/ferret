package ca.ubc.cs.ferret.display;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ca.ubc.cs.clustering.Cluster;
import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.ClusteringPlugin;
import ca.ubc.cs.clustering.IClusteringsContainer;
import ca.ubc.cs.clustering.IClusteringsProvider;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.util.AbstractJob;
import ca.ubc.cs.ferret.util.IJob;

public abstract class DwClusterableCollection<T> extends DwBaseObject 
		implements IClusteringsContainer<T> {

	private Multimap<IClusteringsProvider<T>,Clustering<T>> clusterings;
    protected Clustering<T> selectedClustering = null;
    protected IJob job;
    protected List<String> facts;

	public DwClusterableCollection(IDisplayObject _parent) {
		super(_parent);
	}

    public Multimap<IClusteringsProvider<T>,Clustering<T>> getAllClusterings() {
    	if(clusterings == null) {
    		if(job != null) { return null; }	// in progress
    		job = new AbstractJob() {
    			public boolean run(IProgressMonitor monitor) {
    				Multimap<IClusteringsProvider<T>, Clustering<T>> newClusterings = 
    					MultimapBuilder.hashKeys().linkedHashSetValues().build();
    				try {
    					buildClusterings(monitor, newClusterings);
    				} catch(OperationCanceledException e) { /* do nothing */ }
    	    		clusterings = newClusterings;
					job = null;
		    		refresh();
					return true;
				}
    		};
    		FerretPlugin.getDefault().getJobManager().request(job);
			return null;
    	}
        return clusterings;
    }

	protected void buildClusterings(IProgressMonitor monitor, Multimap<IClusteringsProvider<T>, Clustering<T>> newClusterings) {
		Collection<T> elements = getClusterableElements();
		if(elements == null || elements.size() < getMinimumElementsForClustering()) {
			return;
		}
		// filter the clusterings by whether they're relevant
		Multimap<IClusteringsProvider<T>, Clustering<T>> results = ClusteringPlugin.cluster(elements);
		facts = new ArrayList<String>();
		for(IClusteringsProvider<T> factory : results.keySet()) {
			for(Clustering<T> clustering : results.get(factory)) {
				if(monitor.isCanceled()) { throw new OperationCanceledException(); }
				if(clustering.isRelevant()) {
					newClusterings.put(factory, clustering);
				} else {
					StringBuilder buf = new StringBuilder();
					buf.append(clustering.getName() + ":");
					for(int i = clustering.getName().length(); i < 20; i += 5) {
						buf.append('\t');
					}
					for(Object o : clustering.getClusters().keySet()) {
						buf.append(' ');
						buf.append(FerretPlugin.prettyPrint(o));
					}
					facts.add(buf.toString());
				}
			}
		}
		Collections.sort(facts);
	}
	
	public String getToolTip() {
		if(facts == null) { return null; }
		StringBuilder b = new StringBuilder();
		for(String s : facts) {
			b.append(s);
			b.append('\n');
		}
		return b.substring(0, b.length());
	}

	protected abstract Collection<T> getClusterableElements();

	protected abstract IDisplayObject buildChild(T child);

	@Override
	protected void buildChildren(IDisplayObject[] oldChildren) {
		Collection<T> elements = getClusterableElements();
		List<IDisplayObject> kids = new ArrayList<IDisplayObject>(elements.size());
		if(getActiveClustering() == null) {
			for(T child : elements) {
				IDisplayObject c = findDisplayObject(child, oldChildren);
				if(c == null) { c = buildChild(child); }
				kids.add(c);
			}
		} else {
			for(Cluster<T> cg : getActiveClustering().getClusters().values()) {
				IDisplayObject c = findDisplayObject(cg, oldChildren);
				if(c == null) { c = new DwClusterGroup<T>(cg, this); }
				kids.add(c);
			}
		}
		children = kids.toArray(new IDisplayObject[kids.size()]);
	}

	protected int getMinimumElementsForClustering() {
		return FerretPlugin.getMinimumElementsForClustering();
	}

    public Clustering<T> getActiveClustering() {
      return selectedClustering;
  }

    public void setActiveClustering(Clustering<T> c) {
        children = null;
        selectedClustering = c;
    }

    public int getNumberClusterings() {
    	Multimap<IClusteringsProvider<T>,Clustering<T>> clusterings =
    		getAllClusterings();
    	int count = 0;
    	for(Entry<IClusteringsProvider<T>, Clustering<T>> f : clusterings.entries()) {
    		count += f.getValue().size();
    	}
		return count;
	}

	public int getNumberElements() {
		return getClusterableElements().size();
	}

	@Override
	public void dispose() {
		FerretPlugin.getDefault().getJobManager().discardJob(job);
		super.dispose();
	}
}
