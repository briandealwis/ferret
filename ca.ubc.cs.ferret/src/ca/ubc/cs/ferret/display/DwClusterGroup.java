/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.display;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.clustering.Cluster;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISolution;

public class DwClusterGroup<T> extends DwClusterableCollection<T> {
    protected Cluster<T> cg;
    protected boolean doSimple;
    
    public DwClusterGroup(Cluster<T> _cg, IDisplayObject parent) {
        super(parent);
        cg = _cg;
    }

    public String getText() {
        return "Cluster: " + FerretPlugin.prettyPrint(cg.getIndex());
    }

    public Object getObject() {
        return cg;
    }

    @Override
    public ImageDescriptor getImage() {
        return FerretPlugin.getImage(cg.getIndex());
    }

	@Override
	protected IDisplayObject buildChild(T child) {
		if(child instanceof ISolution) {
			return DwSolution.asChild(parent, (ISolution)child, doSimple);
		} else {
			return DwObject.forObject(child, parent);
		}
	}

	@Override
	protected Collection<T> getClusterableElements() {
        return cg.getElements();
	}

	@Override
	protected void buildChildren(IDisplayObject[] oldChildren) {
		doSimple = DwSolution.allAreSimpleSolutions(cg.getElements());
		super.buildChildren(oldChildren);
		if(!cg.getSubclusters().isEmpty()) {
			IDisplayObject kids[] = new IDisplayObject[children.length + cg.getSubclusters().size()];
			System.arraycopy(children, 0, kids, cg.getSubclusters().size(), children.length);
			int index = 0;
			for(Cluster<T> c : cg.getSubclusters()) {
				IDisplayObject cdo = findDisplayObject(c, oldChildren);
				if(cdo == null) { cdo = new DwClusterGroup<T>(c, this); }
				kids[index++] = cdo;
			}
			children = kids;
		}
	}

	@Override
	public int getImportance() {
		return 10;
	}
}
