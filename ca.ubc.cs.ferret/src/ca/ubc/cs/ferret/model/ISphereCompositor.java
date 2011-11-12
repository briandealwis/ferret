package ca.ubc.cs.ferret.model;

import java.util.List;

/**
 * Implements a sphere composition function.
 * @author Brian de Alwis
 */
public interface ISphereCompositor extends ISphere {
	/**
	 * Add the provided sphere as a component of this compositor.
	 * @param t the provided sphere
	 */
	public void add(ISphere t);

	/**
	 * Remove the specified sphere as a component of this sphere.
	 * @param t the sphere to be removed
	 * @return true if <code>t</code> was a component, false if it was not
	 */
	public boolean remove(ISphere t);

	public List<ISphere> getComposedSpheres();
	
}
