package ca.ubc.cs.ferret.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.FerretConfigurationException;

public interface ISphereFactory extends IAdaptable, Cloneable {
	/**
	 * Answer this factory type's unique identifier.
	 * @return unique identifier for this factory type
	 */
	public String getId();

	/**
	 * Answer this factory type's natural language description.
	 * @return natural language description
	 */
	public String getDescription();

	/**
	 * Return whether this factory has been configured and
	 * thus able to create a sphere.
	 * @return status of creation
	 */
	public IStatus canCreate();

	/**
	 * Create a sphere based on this factory's settings.
	 * @param monitor 
	 * @return
	 * @throws FerretConfigurationException
	 */
	public ISphere createSphere(IProgressMonitor monitor) throws FerretConfigurationException;

	public ImageDescriptor getImageDescriptor();

	public String getHelpContextId();
	
	public ISphereFactory clone();
}
