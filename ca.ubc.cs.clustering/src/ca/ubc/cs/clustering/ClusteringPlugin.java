package ca.ubc.cs.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import ca.ubc.cs.clustering.attrs.AttributeSourceManager;
import ca.ubc.cs.clustering.attrs.ClusterableCollection;
import ca.ubc.cs.clustering.attrs.IAttributeSourceManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class ClusteringPlugin extends Plugin {

	public static final String PLUGIN_ID = "ca.ubc.cs.clustering";
	public static final String clusterersExtensionPoint = PLUGIN_ID + ".clusterers";
	public static final String classifiersExtensionPoint = PLUGIN_ID + ".classifiers";

	private AttributeSourceManager attributeSourceManager;
	
	// The shared instance
	private static ClusteringPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ClusteringPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		attributeSourceManager = new AttributeSourceManager();
		Platform.getExtensionRegistry().addRegistryChangeListener(attributeSourceManager,
				getBundle().getSymbolicName());
		getPluginPreferences()	.setDefault(SqueezerClusteringFactory.SQUEEZER_THRESHOLD, 3);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Platform.getExtensionRegistry().removeRegistryChangeListener(attributeSourceManager);
		attributeSourceManager = null;
		plugin = null;
		super.stop(context);
	}

	public static <T> T getAdapter(Object object, Class<T> clazz) {
        if(clazz.isInstance(object)) { return (T)object; }
        if(object instanceof IAdaptable) {
        	T adapter = (T)((IAdaptable)object).getAdapter(clazz);
        	if(adapter != null) { return adapter; }
        }
        Object adapter = Platform.getAdapterManager().getAdapter(object, clazz);
        if(adapter != null) { return (T)adapter; }
		return null;
	}

	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static ClusteringPlugin getDefault() {
		return plugin;
	}

	public static <T> MultiMap<IClusteringsProvider<T>,Clustering<T>> cluster(Collection<? extends T> objects) {
		ClusterableCollection<T> cc = new ClusterableCollection<T>(objects); 
		return cluster(cc);
	}

	@SuppressWarnings("unchecked")
	public static <T> MultiMap<IClusteringsProvider<T>,Clustering<T>> cluster(ClusterableCollection<T> objects) {
		//objects.getElements()
		final MultiMap<IClusteringsProvider<T>,Clustering<T>> clusterings =
			new MultiHashMap<IClusteringsProvider<T>,Clustering<T>>();
		for(IConfigurationElement decl :  Platform.getExtensionRegistry().getConfigurationElementsFor(clusterersExtensionPoint)) {
			try {
				IConfigurationElement[] children = decl.getChildren();
				Integer precedence = Integer.MAX_VALUE;
				try {
					if(decl.getAttribute("precedence") != null) {
						precedence = Integer.valueOf(decl.getAttribute("precedence"));
					}
				} catch(NumberFormatException e) {
					log(new Status(IStatus.ERROR, PLUGIN_ID, -1, clusterersExtensionPoint + " extension from " + 
							decl.getNamespaceIdentifier() + " specifies invalid precedence", null));
				}
				
				boolean supported = true;
				if(children.length > 1) { 
					log(new Status(IStatus.ERROR, PLUGIN_ID, -1, clusterersExtensionPoint + " extension from " + 
						decl.getNamespaceIdentifier() + " should have only a single child expression", null));
					continue;
				} else if(children.length == 1) {
					Expression expression = ExpressionConverter.getDefault().perform(children[0]);
				    for(T o : objects.getElements()) {
				    	if(expression.evaluate(new EvaluationContext(null, o))
			    				== EvaluationResult.FALSE) {
				    		supported = false;
				    		break;
				    	}
				    }
				}
			    if(!supported) { continue; }
				IClusteringsFactory<T> factory = (IClusteringsFactory<T>)decl.createExecutableExtension("class");
				for(Clustering<T> c : factory.build(objects)) {
					clusterings.put(factory, c);
				}
			} catch (CoreException e) {
				log(e);
			}
		}
		
		// And now sort the list by the precedence values
//		List<Clustering<T>> result = new ArrayList<Clustering<T>>(clusterings.keySet());;
//		Collections.sort(result, new Comparator<Clustering<T>>() {
//			public int compare(Clustering<T> o1, Clustering<T> o2) {
//				return clusterings.get(o1).compareTo(clusterings.get(o2));
//			}});
//		return result;
		return clusterings;
	}

	public static void log(Exception e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, -1, "Exception occurred: " + e.getMessage(), e));
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public IAttributeSourceManager getAttributeSourceManager() {
		return attributeSourceManager;
	}
}
