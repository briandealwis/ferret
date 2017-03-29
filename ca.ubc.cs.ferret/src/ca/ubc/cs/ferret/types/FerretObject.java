/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.types;

import ca.ubc.cs.clustering.attrs.DelegatingAttributeSource;
import ca.ubc.cs.clustering.attrs.IAttributeSource;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.display.IDisplayObject;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.objhdl.ClassLookupCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An object wrapper for bridging objects between sphereHelpers.
 * @author Brian de Alwis
 */
public class FerretObject implements IAdaptable {
	protected static ConversionResult<?>[] EMPTY_CONVERSIONRESULT_ARRAY = 
		new ConversionResult[0];

	/**
	 * The sphere
	 */
	protected ISphere sphere;
	
	/**
	 * The primary object for this FerretObject.
	 */
	protected Object primaryObject;
	
	/**
	 * The fidelity of the primary object.
	 */
	protected Fidelity primaryFidelity;
	
	/**
	 * A conversion result corresponding to the primaryObject.
	 */
	protected ConversionResult<?> primaryObjectResult;
	
	/**
	 * The equivalent objects: this has a partial-sort by fidelity. 
	 */
	protected ConversionResult<?> equivalents[] = EMPTY_CONVERSIONRESULT_ARRAY;
	
	protected Map<String,ConversionResult<?>> cachedConversions = new HashMap<String, ConversionResult<?>>();
	
	public FerretObject(Object object, ISphere tb) {
		this(object, Fidelity.Exact, tb);
	}
	
	public FerretObject(Object object, Fidelity fidelity, ISphere tb) {
		primaryObject = object;
		primaryFidelity = fidelity; 
		sphere = tb;
	}
	
	/**
	 * Adapt this object to a single instance of the provided adapter.  Objects adapted in this
	 * manner, if available, are guaranteed to have {@link Fidelity.Exact}.
	 * @see IAdaptable#getAdapter(Class)
	 */
	public <T> T getAdapter(Class<T> adapter) {
		return getAdapter(adapter, Fidelity.Exact);
	}

	/**
	 * Adapt this object to a single instance of the provided adapter with fidelity of at least
	 * provided <code>fidelity</code>.  
	 * @param adapter the desired type resulting from the adaptation
	 * @param fidelity the minimum accepted fidelity of the adaptation
	 * @return the adapted object, or null if none
	 */
	public <T> T getAdapter(Class<T> adapter, Fidelity fidelity) {
        if(adapter == IAttributeSource.class) {
        	return adapter.cast(new DelegatingAttributeSource(primaryObject));
        } else if(adapter == IWorkbenchAdapter.class) {
        	return adapter.cast(FerretObjectWorkbenchAdapter.getDefault());
        } else if(adapter == IDisplayObject.class) {
        	// for types in FerretObjects, use IPrettyPrinter instead
        	return adapter.cast(new DwFerretObject(this));
        }

		if(adapter.isInstance(primaryObject)) { return adapter.cast(primaryObject); }	// optimization
//		if(primaryObject instanceof IAdaptable) {
//			Object adapted = ((IAdaptable)primaryObject).getAdapter(adapter);
//			if(adapted != null) { return (T)adapted; }
//		}
		ConversionResult<T> result = convert(adapter, 1, fidelity);
		if(result != null && result.hasSingleResult()) { return result.getSingleResult(); }
		return null;
	}
	
	/**
	 * Convert this object to one or more instances of <code>desiredClass</code>
	 * subject to the constraints encoded in <code>count</code> and <code>fidelity</code>.
	 * <code>count</code> encodes the maximum or minimum number of equivalent
	 * objects that would be acceptable: positive indicates a ceiling of up to this many
	 * objects; if negative, indicates a floor.  Hence <code>-1</code> would indicate at
	 * least a single object; <code>+1</code> indicates exactly one object.  If 
	 * <code>count</code> is 0 then a conversion is attempted with null indicating that
	 * no conversion is possible, and 0 results meaning that this particular object could
	 * not be converted.
	 * @param desiredClass the desired type resulting from the adaptation
	 * @param count the encoded acceptable instance count
	 * @param fidelity the minimum accepted fidelity of the adaptation
	 * @return the conversion result if successful, or null if a conversion was not possible
	 */
	@SuppressWarnings("unchecked")
	public <T> ConversionResult<T> convert(Class<T> desiredClass, int count, Fidelity fidelity) {
		//		return (ConversionResult<T>)convert(desiredClass.getName(), count, fidelity);
		ConversionResult<?> cr = convert(desiredClass.getName(), count, fidelity);
		if(cr != null) {
			for(Object o : cr.getResults()) {
				assert desiredClass.isInstance(o);
			}
		}
		return (ConversionResult<T>)cr;
	}
	
	/**
	 * Convert this object to one or more instances of <code>desiredClass</code>
	 * subject to the constraints encoded in <code>count</code> and <code>fidelity</code>.
	 * <code>count</code> encodes the maximum or minimum number of equivalent
	 * objects that would be acceptable: positive indicates a ceiling of up to this many
	 * objects; if negative, indicates a floor.  Hence <code>-1</code> would indicate at
	 * least a single object; <code>+1</code> indicates exactly one object.  If 
	 * <code>count</code> is 0 then a conversion is attempted with null indicating that
	 * no conversion is possible, and 0 results meaning that this particular object could
	 * not be converted.
	 * @param desiredClass the desired type resulting from the adaptation
	 * @param count encoded acceptable instance count
	 * @param fidelity the minimum accepted fidelity of the adaptation
	 * @return the conversion result if successful, or null if a conversion was not possible
	 */
	public ConversionResult<?> convert(String desiredClass, int count, Fidelity fidelity) {
		String conversionDescription = desiredClass + "|" + count + "|" + fidelity;
		ConversionResult<?> result = cachedConversions.get(conversionDescription);
		if(result == null) {
			result = primitiveConvert(desiredClass, count, fidelity);
			cachedConversions.put(conversionDescription, result);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected ConversionResult<?> primitiveConvert(String desiredClass, int count, Fidelity fidelity) {
		if(desiredClass.equals(FerretObject.class.getName())) {
			ConversionResult<FerretObject> cr = new ConversionResult(FerretObject.class, Fidelity.Exact);
			cr.addResult(this);
			return cr;
		} else if(countSatisfied(1, count) && isAssignableFrom(desiredClass, primaryObject.getClass())) {
			if(primaryObjectResult == null) {
				// Unfortunately have this generic nasty as we don't actually know the type of primaryObject
				ConversionResult cr = new ConversionResult(primaryObject.getClass(), Fidelity.Exact);
				cr.addResult(primaryObject);
				primaryObjectResult = cr;
			}
			return primaryObjectResult;
		}
		// Check to see if we've previously made a compatible conversion
		// equivalents is sorted by fidelity, so we go from best to worst
		for(ConversionResult<?> cr : equivalents) {
			if(countSatisfied(cr.getResults().size(), count)
					&& isAssignableFrom(desiredClass, cr.getResultClass())
					&& (fidelity == null || cr.getFidelity().compareTo(fidelity) <= 0)) {
				return cr;
			}
		}
		
		// Try a conversion: first on the primaryObject, and then on any equivalents
		ConversionResult<?> result;
		if((result = tryConversion(primaryObject, desiredClass, count, fidelity)) != null) {
			return result;
		}
		for(ConversionResult<?> cr : equivalents) {
			// FIXME: what should we do if the equivalent has multiple objects!?
			// Should we just iterate over them until we find one? For now, we ignore them
			if(cr.hasSingleResult()
					&& cr.getFidelity().compareTo(fidelity) <= 0
					&& (result = tryConversion(cr.getSingleResult(), desiredClass, count, fidelity)) != null) {
				return result;
			}
		}
		return null;
	}
	
	protected boolean isAssignableFrom(String desiredClass, Class<?> actualClass) {
		return ClassLookupCache.isAssignableFrom(desiredClass, actualClass);
	}

	/**
	 * 	Check that the actual number of elements (<code>size</code>) satisfies the
	 * constraint as encoded in <code>count</code>.  <code>count</code> encodes 
	 * the maximum or minimum number of acceptable objects: a positive number indicates
	 * a ceiling, a negative indicates a floor.  Hence <code>-1</code> would indicate at
	 * least a single object; <code>+1</code> indicates exactly one object.  
	 * If <code>count</code> is zero, then any size is acceptable.
	 * @param size the actual size
	 * @param count the encoded constraint
	 * @return true if size satisfies the constraint
	 */
	protected boolean countSatisfied(int size, int count) {
		return count == 0 || (count > 0 && size <= count) || (count < 0 && size >= -count);
	}

	protected ConversionResult<?> tryConversion(Object object, String adapterType, int count, Fidelity fidelity) {
		ConversionResult<?> result = 
			TypesConversionManager.getDefault()
				.convert(object, adapterType, fidelity, sphere);
		if(result != null) {
			if(!countSatisfied(result.getResults().size(), count)) { return null; } 
			addEquivalent(result);
		}
		return result;
	}
	
	/**
	 *  Insert the newly adapted object into <code>equivalents</code> and
	 *  <code>equivalentsFidelity</code>, sorting it appropriately by the
	 *  fidelity of the resulting adapted object. 
	 */
	protected void addEquivalent(ConversionResult<?> equiv) {
		ConversionResult<?> newEquivalents[] = new ConversionResult<?>[equivalents.length + 1];
		int insertionPoint = 0;
		while(insertionPoint < equivalents.length
				&& equivalents[insertionPoint].getFidelity().compareTo(equiv.getFidelity()) > 0) {
			insertionPoint++;
		}
		System.arraycopy(equivalents, 0, newEquivalents, 0, insertionPoint);
		newEquivalents[insertionPoint] = equiv;
		System.arraycopy(equivalents, insertionPoint, newEquivalents, insertionPoint + 1, 
				equivalents.length - insertionPoint);
		equivalents = newEquivalents;
	}

	@Override
	public boolean equals(Object obj) {
		// This isn't ideal, I suppose, but it's good enough for now
		Object me = getAdapter(IEquivalenceTesting.class, Fidelity.Exact);
		if(me != null) { 
			return ((IEquivalenceTesting)me).equals(obj);
		} else {
			me = primaryObject; 
		}
		if(obj instanceof FerretObject) {
			Object other = ((FerretObject)obj).getAdapter(IEquivalenceTesting.class, Fidelity.Exact);
			if(other != null) {
				return other.equals(me);
			} else { 
				obj = ((FerretObject)obj).primaryObject; 
			}
		}
		
		return me.equals(obj);
	}

	@Override
	public int hashCode() {
		Object me = getAdapter(IEquivalenceTesting.class, Fidelity.Exact);
		if(me == null) { me = primaryObject; }
		return me.hashCode();
	}
	
	public String toString() {
		return  "FerretObject(" + primaryObject.getClass().getSimpleName() + ": "
			+ FerretPlugin.compactPrettyPrint(primaryObject) + ")";
	}

	public ISphere getSphere() {
		return sphere;
	}
	
	public Object getPrimaryObject() {
		return primaryObject;
	}

	public Fidelity getPrimaryFidelity() {
		return primaryFidelity;
	}

	public static FerretObject[] wrap(Object[] inputs, Fidelity fidelity, ISphere tb) {
		if(inputs instanceof FerretObject[]) {
			return (FerretObject[])inputs;
		}
		FerretObject wrapped[] = new FerretObject[inputs.length];
		for(int i = 0; i < inputs.length; i++) {
			wrapped[i] = inputs[i] instanceof FerretObject ? (FerretObject)inputs[i] :
				new FerretObject(inputs[i], fidelity, tb);
		}
		return wrapped;
	}

	public static Collection<FerretObject> wrap(Collection<?> inputs,
			Fidelity fidelity, ISphere tb) {
		Collection<FerretObject> wrapped = new ArrayList<FerretObject>(inputs.size());
		for(Object value : inputs) {
			wrapped.add(value instanceof FerretObject ? (FerretObject)value :
				new FerretObject(value, fidelity, tb));
		}
		return wrapped;
	}

	public static Collection<FerretObject> wrapAsCollection(Object[] inputs,
			Fidelity  fidelity, ISphere tb) {
		Collection<FerretObject> wrapped = new ArrayList<FerretObject>(inputs.length);
		for(Object value : inputs) {
			wrapped.add(value instanceof FerretObject ? (FerretObject)value :
				new FerretObject(value, fidelity, tb));
		}
		return wrapped;
	}

	public void setPrimaryFidelity(Fidelity fidelity) {
		primaryFidelity = fidelity;
		// and downgrade the fidelities for any other conversions
		if(primaryObjectResult != null) { primaryObjectResult.setFidelity(fidelity); }
		for(ConversionResult<?> cr : cachedConversions.values()) {
			if(cr != null) {	// null indicates an inability to do the conversion
				cr.setFidelity(fidelity.least(cr.getFidelity()));
			}
		}
	}

	public IRelation resolve(IProgressMonitor monitor, String relationName) {
		return sphere.resolve(monitor, relationName, this);
	}

	public IRelation resolve(String relationName) {
		return resolve(new NullProgressMonitor(), relationName);
	}

	public boolean containedIn(Collection<FerretObject> collection) {
		return contains(collection, this);
	}
	
	public static boolean contains(Collection<FerretObject> collection, Object o) {
		for(FerretObject fo : collection) {
			if(fo.equals(o)) { return true; }
		}	
		return false;
	}

}
