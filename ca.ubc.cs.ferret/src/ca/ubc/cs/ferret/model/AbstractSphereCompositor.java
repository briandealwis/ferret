package ca.ubc.cs.ferret.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.types.FerretObject;

/**
 * A sphere compositor, a sphere that composes other spheres.  Different subclasses 
 * will provide different lookup policies.
 * @author Brian de Alwis
 *
 */
public abstract class AbstractSphereCompositor extends AbstractSphere 
		implements ISphereCompositor {
	protected List<ISphere> spheres = new ArrayList<ISphere>();
	
	public AbstractSphereCompositor() {}

	public AbstractSphereCompositor(ISphere... spheres) {
		this();
		for(ISphere t : spheres) {
			add(t);
		}
	}

	public void add(ISphere t) {
		spheres.add(t);
		t.setParent(this);
	}
	
	public boolean remove(ISphere t) {
		if(t.getParent() == this) { t.setParent(null); }
		return spheres.remove(t);
	}

	public List<ISphere> getComposedSpheres() {
		return spheres;
	}
	
	public <T> T get(String key, Class<T> clazz) {
		// Return the first result found
		for(ISphere t : spheres) {
			T result = t.get(key, clazz);
			if(result != null && clazz.isInstance(result)) { return result; }
		}
//		if(parent != null) { return parent.get(key, clazz); }
		return null;
	}

	@Override
	public <T> void internalGetAll(String key, Class<T> cl,
			Map<ISphere, T> results) {
		for(ISphere tb : spheres) {
			((AbstractSphere)tb).internalGetAll(key, cl, results);
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
