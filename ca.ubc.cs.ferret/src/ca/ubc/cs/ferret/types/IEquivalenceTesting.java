package ca.ubc.cs.ferret.types;

/**
 * A special interface used to compare contextual objects against some representative object
 * as perceived by a developer.   For example, TPTP generates a unique 
 * {@link org.eclipse.hyades.models.trace.TRCMethod TRCMethod} instance for
 * a particular method for each dynamic program trace found, despite that these
 * TRCMethods represent the same method.  
 * @author Brian de Alwis
 */
public interface IEquivalenceTesting {

	public int hashCode();
	
	public boolean equals(Object other);
	
}
