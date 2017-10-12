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

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.objhdl.ClassLookupCache;
import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * Manager for type conversions.  Type conversions can be chained, as necessary.
 * Conversions are distinguished by the fidelity and the length of the conversion chain.
 * FIXME: this should probably somehow use the desired-type class hierarchy to
 * 		determine the conversion.  For now converters should explicitly enumerate the types
 * 		they support
 */
public class TypesConversionManager implements IRegistryChangeListener {
	public final static String conversionExtensionPoint = FerretPlugin.pluginID + ".typeConverters"; 
	protected static TypesConversionManager singleton;

	protected DirectedGraph<String,ConversionSpecification> typesConversionGraph;
	
	/**
	 * The shortest paths as calculated for the different fidelities for the type conversion graph.
	 */
	protected Map<Fidelity,DijkstraShortestPath<String, ConversionSpecification>> tcDijk;

	protected Map<Fidelity,Map<String,Map<String,List<ConversionPipeline>>>> knownConversions;
	private IAdapterManager adapterManager;

	public static void stop() {
		if(singleton == null) { return; }
		singleton.reset();
		RegistryFactory.getRegistry().removeRegistryChangeListener(singleton);
		singleton = null;
	}

	protected TypesConversionManager() {}

	public static TypesConversionManager getDefault() {
		if(singleton == null) {
			singleton = new TypesConversionManager ();
			RegistryFactory.getRegistry().addRegistryChangeListener(singleton, FerretPlugin.pluginID);
		}
		return singleton;
	}
	
	protected synchronized DirectedGraph<String,ConversionSpecification> getConversionGraph() {
		if(typesConversionGraph != null) { return typesConversionGraph; }
		typesConversionGraph = new DirectedSparseMultigraph<String, ConversionSpecification>();
		for(IConfigurationElement decl : RegistryFactory.getRegistry().getConfigurationElementsFor(conversionExtensionPoint)) {
			if(decl.getChildren().length > 0) { 
				FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID,
						FerretErrorConstants.CONFIGURATION_ERROR,
						conversionExtensionPoint + " extension from " + 
						decl.getNamespaceIdentifier() + " should have no child expression", null));
				continue;
			}
            if(FerretPlugin.hasDebugOption("debug/showTypeConversions")) {
            	System.out.println("adding type-conversion [" + decl.getNamespaceIdentifier() + "]: "
            			+ decl.getAttribute("providedType") + " -> " + decl.getAttribute("desiredType") 
            			+ " (" + decl.getAttribute("fidelity") + ")");
            }
			addConversionSpec(new ConversionSpecification(decl.getAttribute("providedType"), 
					decl.getAttribute("desiredType"), Fidelity.fromString(decl.getAttribute("fidelity")), decl));
		}
		tcDijk = new HashMap<Fidelity,DijkstraShortestPath<String, ConversionSpecification>>();
		for(Fidelity fidelity : Fidelity.values()) {
			tcDijk.put(fidelity,
					new DijkstraShortestPath<String, ConversionSpecification>(typesConversionGraph,
							getDijkTransformer(fidelity)));
		}
		return typesConversionGraph;
	}
	
	protected Function<ConversionSpecification, Number> getDijkTransformer(final Fidelity fidelity) {
		// Return a transformer that will compare the fidelity of a particular
		// conversionspec against a configured minimum fidelity
		// if the conversion spec isn't sufficiently high-enough fidelity,
		// then return infinity
		return cs -> cs == null || cs.getFidelity().compareTo(fidelity) < 0 ? Double.POSITIVE_INFINITY : 1;
	}

	protected void addConversionSpec(ConversionSpecification cs) {
		typesConversionGraph.addVertex(cs.getProvidedType());
		typesConversionGraph.addVertex(cs.getDesiredType());
		typesConversionGraph.addEdge(cs, cs.getProvidedType(), cs.getDesiredType());
	}
	
	public List<ConversionPipeline> findConversion(Class<?> objectClass, 
			String desiredType, Fidelity desiredFidelity) {
		List<ConversionPipeline> result = lookupConversion(objectClass, desiredType, desiredFidelity);
		if(result != null) { return result.isEmpty() ? null : result; }
		result = basicFindConversion(objectClass, desiredType, desiredFidelity);
		recordConversion(objectClass, desiredType, desiredFidelity, result);
		if(result.isEmpty()) { return null; }
		return result;
	}
	
	protected List<ConversionPipeline> basicFindConversion(Class<?> objectClass,
			String desiredType, Fidelity desiredFidelity) {
		Graph<String,ConversionSpecification> graph = getConversionGraph();
		if (!hasVertex(graph, desiredType)) {
			return Collections.emptyList();
		}
		ArrayList<ConversionPipeline> conversions = new ArrayList<ConversionPipeline>(1);
		for(String sourceType : getClassLookupOrder(objectClass)) {
			if(!hasVertex(graph, sourceType)) { continue; }
			DijkstraShortestPath<String,ConversionSpecification> dijk = tcDijk.get(desiredFidelity);
			Number distance = dijk.getDistance(sourceType, desiredType); 
			if(distance == null || distance.doubleValue() >= Double.POSITIVE_INFINITY) { continue; }
			List<ConversionSpecification> path = (List<ConversionSpecification>)dijk.getPath(sourceType, desiredType);
			if(path != null && !path.isEmpty()) {
				conversions.add(new ConversionPipeline(path));
			}
		}
		Collections.sort(conversions, new Comparator<ConversionPipeline>() {
			public int compare(ConversionPipeline o1, ConversionPipeline o2) {
				return o1.size() - o2.size();
			}});
		return conversions;
	}
	
	protected List<String> getClassLookupOrder(Class<?> objectClass) {
		return ClassLookupCache.getClassLookupOrder(objectClass);
	}

	private synchronized List<ConversionPipeline> lookupConversion(Class<?> objectClass,
			String desiredType, Fidelity desiredFidelity) {
		if(knownConversions == null) { return null; }
		if(!knownConversions.containsKey(desiredFidelity)) { return null; }
		Map<String,Map<String,List<ConversionPipeline>>> conversions = 
			knownConversions.get(desiredFidelity);
		if(!conversions.containsKey(objectClass.getName())) { return null; }
		return conversions.get(objectClass.getName()).get(desiredType);
	}

	private synchronized void recordConversion(Class<?> objectClass, String desiredType,
			Fidelity desiredFidelity, List<ConversionPipeline> pipelines) {
		if(FerretPlugin.hasDebugOption("debug/showTypeConversions")) {
			System.out.println("Conversion path for: " + objectClass.getName() + " -> " + desiredType 
					+ " for " + desiredFidelity + ": " + pipelines);
		}
		if(knownConversions == null) { knownConversions = new HashMap<Fidelity,Map<String,Map<String,List<ConversionPipeline>>>>(); }
		Map<String, Map<String,List<ConversionPipeline>>> conversions;
		if(!knownConversions.containsKey(desiredFidelity)) {
			knownConversions.put(desiredFidelity, conversions = new HashMap<String, Map<String,List<ConversionPipeline>>>());
		} else {
			conversions = knownConversions.get(desiredFidelity);
		}
		Map<String, List<ConversionPipeline>> typeConversions;
		if(!conversions.containsKey(objectClass.getName())) {
			conversions.put(objectClass.getName(), typeConversions = new HashMap<String, List<ConversionPipeline>>());
		} else {
			typeConversions = conversions.get(objectClass.getName());
		}
		typeConversions.put(desiredType, pipelines);
	}

	protected <T> boolean hasVertex(Graph<T, ?> graph, T adapterType) {
		if(graph.addVertex(adapterType)) {
			graph.removeVertex(adapterType);
			return false;
		}
		return true;
	}

	public void registryChanged(IRegistryChangeEvent event) {
		reset();
	}

	protected void reset() {
		knownConversions = null;
		typesConversionGraph = null;
		tcDijk = null;
	}

	public ConversionResult<?> convert(Object object, String adapterType,
			Fidelity fidelity, ISphere sphere) {
		Object adapted;
		if ((adapted = getAdapterManager().loadAdapter(object, adapterType)) != null) {
            if(FerretPlugin.hasDebugOption("debug/showTypeConversions")) {
            	System.out.println("Conversion using Eclipse Adapter Framework: " 
            			+ object.getClass().getName() + " -> " + adapterType);
            }
            return ConversionResult.forObject(adapted);
		}

		List<ConversionPipeline> pipelines = findConversion(object.getClass(), adapterType, fidelity);
		if(pipelines == null) { return null; }
		for(ConversionPipeline pipeline : pipelines) {
			ConversionResult<?> result = pipeline.convert(object, sphere);
			if(result != null) {
				if(FerretPlugin.hasDebugOption("debug/showTypeConversions")) {
					System.out.println("Conversion: " + pipelines.toString());
				}
				return result;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	private IAdapterManager getAdapterManager() {
		if (adapterManager == null) {
			BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			ServiceReference<IAdapterManager> reference = bundleContext.getServiceReference(IAdapterManager.class);
			adapterManager = bundleContext.getService(reference);
			Assert.isNotNull(adapterManager);
		}
		return adapterManager;
	}

	public static <T> T getAdapter(Object e, Class<T> clazz, Fidelity fidelity) {
		ConversionResult cr = getDefault().convert(e, clazz.getName(), fidelity, null);
		if(cr != null && cr.hasSingleResult()) {
			return clazz.cast(cr.getSingleResult());
		}
		return null;
	}

}
