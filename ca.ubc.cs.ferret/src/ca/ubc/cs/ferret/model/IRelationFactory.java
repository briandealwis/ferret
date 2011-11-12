package ca.ubc.cs.ferret.model;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.types.FerretObject;

public interface IRelationFactory {
	
	/**
	 * Clone and configure this instance for the particular resolver and arguments.
	 * Return null if not supported.
	 * @param monitor
	 * @param resolver
	 * @param arguments
	 * @return configured instance
	 */
	public IRelation configure(IProgressMonitor monitor, IRelationResolver resolver, FerretObject... arguments);

	/**
	 * Clone and configure this instance for the particular resolver and arguments.
	 * Return null if not supported.
	 * @param monitor
	 * @param resolver
	 * @param arguments
	 * @return configured instance
	 */
	public IRelation configure(IProgressMonitor monitor, IRelationResolver resolver, Object... arguments);

}
