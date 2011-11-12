package ca.ubc.cs.ferret.model;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.types.FerretObject;

/**
 * Encapsulated the state of resolving a relation through a (possibly composite) sphere 
 * network. There are two types of operations: those that start an operation afresh from the 
 * root (or top) of the network, and those that continue resolution from the point as captured by this
 * instance.
 * @author Brian de Alwis
 */
public interface IRelationResolver {
	
	/**
	 * Continue the resolution of the relation currently being resolved
	 * @param monitor
	 * @param args
	 * @return the subsequent
	 */
	public IRelation continuePerform(IProgressMonitor monitor, FerretObject... args);
	public IRelation continuePerform(IProgressMonitor monitor, String relationName, FerretObject... args);

	public IRelation topPerform(IProgressMonitor monitor, String relationName, FerretObject... args);

//	public <T> T topGet(String name, Class<T> clazz);
//	public <T> T continueGet(String name, Class<T> clazz);
	
	public ISphere getRootSphere();
	public ISphere getSphere();


}
