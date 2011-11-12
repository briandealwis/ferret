package ca.ubc.cs.objhdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassLookupCache {
	protected static ClassLookupCache singleton;
	
	protected Map<Class<?>,List<String>> classEquivalenceCache;
	protected Map<Class<?>,Set<String>> classAssignableCache;

	protected static ClassLookupCache getDefault() {
		if(singleton == null) {
			singleton = new ClassLookupCache ();
		}
		return singleton;
	}

	public static void stop() {
		if(singleton != null) { singleton.reset(); }
		singleton = null;
	}

	/**
	 * Return true if an instance of <code>actualClass</code> could be assigned
	 * to an instance of <code>assignedClass</code>
	 * @param assignedClass type-name for LHS of assignment
	 * @param actualClass actual type for RHS of assignment
	 * @return true if assignment would be permitted
	 */
	public static boolean isAssignableFrom(String assignedClass, Class<?> actualClass) {
		return getDefault().determineIfAssignableFrom(assignedClass, actualClass);
	}

	public static List<String> getClassLookupOrder(Class<?> actualClass) {
		return getDefault().lookupClassOrder(actualClass);
	}

	protected void reset() {
		classEquivalenceCache = null;
		classAssignableCache = null;
	}

	protected void computeClassLookupOrder(List<String> equivalents, Class<?> clazz, Set<Class<?>> seen) {
		if(clazz == null) { return; }
		if(seen.contains(clazz)) { return; }
		seen.add(clazz);
		Collection<String> other = classEquivalenceCache.get(clazz);
		if(other != null) {
			equivalents.addAll(other);
			return;
		}
		equivalents.add(clazz.getName());
		computeInterfaceLookupOrder(equivalents, clazz.getInterfaces(), seen);
		computeClassLookupOrder(equivalents, clazz.getSuperclass(), seen);
	}

	protected void computeInterfaceLookupOrder(List<String> equivalents,
			Class<?> interfaces[], Set<Class<?>> seen) {
		if(interfaces.length == 0) { return; }
		LinkedList<Class<?>> added = new LinkedList<Class<?>>();
		for(Class<?> iface : interfaces) {
			if(seen.add(iface)) {
				// musn't recurse here as would mess up the order
				equivalents.add(iface.getName());
				added.add(iface);
			}
		}
		for(Class<?> iface : added) {
			computeInterfaceLookupOrder(equivalents, iface.getInterfaces(), seen);
		}
	}

	protected synchronized List<String> lookupClassOrder(Class<?> actualClass) {
		if(classEquivalenceCache == null) {
			classEquivalenceCache = new HashMap<Class<?>,List<String>>();
			classAssignableCache = new HashMap<Class<?>,Set<String>>();
		}
		List<String> equivalents = classEquivalenceCache.get(actualClass);
		if(equivalents != null) { return equivalents; }
		
		equivalents = new ArrayList<String>();
		computeClassLookupOrder(equivalents, actualClass, new HashSet<Class<?>>());
		((ArrayList<String>)equivalents).trimToSize();
		classEquivalenceCache.put(actualClass, equivalents);
		classAssignableCache.put(actualClass, new HashSet<String>(equivalents));
		return equivalents;
	}

	protected synchronized boolean determineIfAssignableFrom(String assignedClass, Class<?> actualClass) {
		getClassLookupOrder(actualClass);
		return classAssignableCache.get(actualClass).contains(assignedClass);
	}

}
