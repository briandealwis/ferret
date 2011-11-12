package ca.ubc.cs.ferret.types;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import ca.ubc.cs.ferret.model.ISphere;

public class ConversionSpecification {
	/**
	 * Representation of the fidelity of a conversion.  <EM>Exact</EM> conversions are perfect;
	 * <EM>Equivalent</EM> conversions are the best possible match; <EM>Approximate</EM>
	 * matches are near-equivalent.
	 * FIXME: should work on these descriptions!
	 * @author Brian de Alwis
	 */
	public enum Fidelity { Approximate, Equivalent, Exact;
		public static Fidelity fromString(String string) {
			if("exact".equalsIgnoreCase(string)) {
				return Exact;
			} else if("equivalent".equalsIgnoreCase(string)) {
				return Equivalent;
			} else if("approximate".equalsIgnoreCase(string)) {
				return Approximate;
			} else {
				return Exact;	// default
			}
		}
		
		public Fidelity least(Fidelity other) {
			return this.compareTo(other) < 0 ? this : other;
		}
	};
	
	protected Fidelity fidelity;
	protected String providedType;
	protected String desiredType;
	protected IConfigurationElement specification;
	protected Class<?> providedClass;
	protected Class<?> desiredClass;
	
	public ConversionSpecification(String pt, String dt, Fidelity fid, IConfigurationElement spec) {
		providedType = pt;
		desiredType = dt;
		fidelity = fid;
		specification = spec;
	}
	
	public ConversionResult<?> convert(Object o, ISphere sphere) throws CoreException, ConversionException {
		ITypeConverter cv = (ITypeConverter) specification.createExecutableExtension("class");
		if(cv == null) { return null; }
		return cv.convert(o, this, sphere);
	}

	public String getProvidedType() {
		return providedType;
	}

	public String getDesiredType() {
		return desiredType;
	}

	public Fidelity getFidelity() {
		return fidelity;
	}

	public IConfigurationElement getSpecifyingConfigurationElement() {
		return specification;
	}
	
	public String toString() {
		return providedType + " -> " + desiredType + ": " + fidelity
			+ (specification == null ? "" : " (" + specification.getNamespaceIdentifier() + ")");
	}

	public Class<?> getProvidedClass() throws ClassNotFoundException {
		if(desiredClass == null) {
			desiredClass = specification.getClass().getClassLoader().loadClass(desiredType);
		}
		return desiredClass; 
	}

	public Class<?> getDesiredClass() throws ClassNotFoundException {
		if(desiredClass == null) {
			Bundle bundle = Platform.getBundle(specification.getNamespaceIdentifier());
			desiredClass = bundle.loadClass(desiredType);
		}
		return desiredClass; 
	}


}
