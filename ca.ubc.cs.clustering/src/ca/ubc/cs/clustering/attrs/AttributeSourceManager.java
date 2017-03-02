package ca.ubc.cs.clustering.attrs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import ca.ubc.cs.clustering.ClusteringPlugin;

/**
 * FIXME: this doesn't properly handle classifiers that differ on per-instance
 * features (i.e., those specifying core.expression guards in the plugin.xml).
 * @author Brian de Alwis
 */
public class AttributeSourceManager implements IAttributeSourceManager,
	IRegistryChangeListener {

	protected Map<String,IAttributeSource> attributeSourceCache;
	
	/**
	 * Return an IAttributeSource for the provided element consisting of all of
	 * its possible attribute sources.
	 * @param element
	 * @return
	 */
	public synchronized IAttributeSource getAttributeSource(Object element) {
		IAttributeSource result;
		if(attributeSourceCache == null) {
			attributeSourceCache = new HashMap<String,IAttributeSource>();
		} else if((result = attributeSourceCache.get(element.getClass().getName())) != null) {
			return result;
		}
		if((result = ClusteringPlugin.getAdapter(element, IAttributeSource.class)) != null) {
			return result;
		}
		Multimap<String,ClassifierDescription> classifierCache = cacheClassifiers();
	    AttributeSource source = new AttributeSource();
	    processClassifiers(source, element, element.getClass(), classifierCache);
	    attributeSourceCache.put(element.getClass().getName(), source);
	    return source;
	}
	
	protected Multimap<String,ClassifierDescription> cacheClassifiers() {
		Multimap<String,ClassifierDescription> classifierCache = 
			MultimapBuilder.hashKeys().linkedHashSetValues().build();
		for(IConfigurationElement decl :  getExtensionRegistry().getConfigurationElementsFor(ClusteringPlugin.classifiersExtensionPoint)) {
			try {
				String objectType = decl.getAttribute("objectType");
				ClassifierDescription cd = new ClassifierDescription();
				IConfigurationElement[] children = decl.getChildren();
				if(children.length > 1) { 
					ClusteringPlugin.log(new Status(IStatus.ERROR, ClusteringPlugin.PLUGIN_ID, -1,
							ClusteringPlugin.classifiersExtensionPoint + " extension from " + 
						decl.getNamespaceIdentifier() + " should have only a single child expression", null));
					continue;
				} else if(children.length == 1) {
				    cd.setGuard(ExpressionConverter.getDefault().perform(children[0]));					
				}
				cd.setId(decl.getAttribute("id"));
				cd.setDescription(decl.getAttribute("description"));
			    cd.setDeclaration(decl);
			    classifierCache.put(objectType, cd);
			} catch (CoreException e) {
				ClusteringPlugin.log(e);
			}
		}
		return classifierCache;
	}
	
	private IExtensionRegistry getExtensionRegistry() {
		return RegistryFactory.getRegistry();
	}

	protected void processClassifiers(AttributeSource source, Object element,
			Class<?> clazz, Multimap<String,ClassifierDescription> classifierCache) {
		Class<?> superclazz = clazz.getSuperclass();
		// Start from Object and work our way up, thus overwriting any classifiers
		// with the most specific classifiers applicable to objects of type clazz.
		if(superclazz != null) {
			processClassifiers(source, element, superclazz, classifierCache);
		}
		for(Class<?> iface : clazz.getInterfaces()) {
			processTypeClassifier(source, element, iface, classifierCache);
		}
		processTypeClassifier(source, element, clazz, classifierCache);
	}

	protected void processTypeClassifier(AttributeSource source, Object element,
			Class<?> type, Multimap<String, ClassifierDescription> classifierCache) {
		Collection<ClassifierDescription> cds = classifierCache.get(type.getName());
		if(cds == null) { return; }
		for(ClassifierDescription cd : cds) {
			try {
				if(cd.getGuard() != null && 
						cd.getGuard().evaluate(new EvaluationContext(null, element))
							!= EvaluationResult.TRUE) {
					continue;
				}
				source.add((IClassifier)cd.getDeclaration().createExecutableExtension("class"),
						cd.getId(), cd.getDescription());
			} catch (CoreException e) {
				ClusteringPlugin.log(new Status(IStatus.ERROR, ClusteringPlugin.PLUGIN_ID, -1,
						"error creating classifier " + cd.getId(), e));
			}
		}
	}

	public synchronized void registryChanged(IRegistryChangeEvent event) {
		attributeSourceCache = null;
	}

}
