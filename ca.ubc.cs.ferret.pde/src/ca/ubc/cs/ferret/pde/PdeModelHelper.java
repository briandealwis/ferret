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
package ca.ubc.cs.ferret.pde;

import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.references.AbstractReference;
import ca.ubc.cs.ferret.references.FileReference;
import ca.ubc.cs.ferret.references.ZipEntryReference;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.eclipse.pde.core.IIdentifiable;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IFragmentModel;
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.ISharedPluginModel;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.FeatureModelManager;
import org.eclipse.pde.internal.core.IPluginModelListener;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDEState;
import org.eclipse.pde.internal.core.PluginModelDelta;
import org.eclipse.pde.internal.core.PluginModelManager;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeatureImport;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.plugin.ExternalFragmentModel;
import org.eclipse.pde.internal.core.plugin.ExternalPluginModel;
import org.eclipse.pde.internal.core.plugin.ExternalPluginModelBase;
import org.eclipse.pde.internal.core.plugin.PluginHandler;

public class PdeModelHelper implements IPluginModelListener, IRegistryChangeListener {
	protected static PdeModelHelper singleton;

	public Map<String,IPluginModelBase> models = null;
	public Map<String,Multimap<IPluginExtension,IPluginObject>> extensions = null;
	public Map<String, IFeatureModel> features = null;

	protected PdeModelHelper() {
		init();
	}

	public static PdeModelHelper getDefault() {
		if(singleton == null) {
			singleton = new PdeModelHelper();
		}
		return singleton;
	}

	public static void stop() {
		if(singleton == null) { return; }
		singleton.dispose();
		singleton = null;
	}

	public void init() {
		getPluginModelManager().addPluginModelListener(this);
		getExtensionRegistry().addRegistryChangeListener(this);
	}

	public void dispose() {
		getPluginModelManager().removePluginModelListener(this);
		getExtensionRegistry().removeRegistryChangeListener(this);
		models = null;
		extensions = null;
		features = null;
	}

	private IExtensionRegistry getExtensionRegistry() {
		return RegistryFactory.getRegistry();
	}
	
	public void modelsChanged(PluginModelDelta delta) {
		dispose();
	}

	public void registryChanged(IRegistryChangeEvent event) {
		dispose();
	}

	/**
	 * Return URLs to projects in the workspace that have a manifest file (MANIFEST.MF
	 * or plugin.xml)
	 * COPIED FROM org.eclipse.pde.internal.core.WorkspacePluginModelManager.getPluginPaths()
	 * @return an array of URLs to workspace plug-ins
	 */
	protected URL[] getWorkspacePluginPaths(PDEState state) {
		ArrayList<URL> list = new ArrayList<URL>();
		for(IPluginModelBase pmb : PluginRegistry.getWorkspaceModels()) {
			try {
				list.add(new File(pmb.getInstallLocation()).toURL());
			} catch (MalformedURLException e) {}
		}
//		IProject[] projects = PDECore.getWorkspace().getRoot().getProjects();
//		for (int i = 0; i < projects.length; i++) {
//			if (WorkspacePluginModelManager.isPluginProject(projects[i])) {			
//				try {
//					IPath path = projects[i].getLocation();
//					if (path != null) {
//						list.add(path.toFile().toURL());
//					}
//				} catch (MalformedURLException e) {
//				}
//			}
//		}
		return (URL[])list.toArray(new URL[list.size()]);
	}

	protected URL[] getTargetPluginPaths(PDEState state) {
		// was: ConfiguratorUtils.getCurrentPlatformConfiguration().getPluginPath(),
		// except that doesn't deal with plugins overridden from the local workspace 
		ArrayList<URL> list = new ArrayList<URL>();
		for(IPluginModelBase pmb : state.getTargetModels()) {
			try {
				list.add(new File(pmb.getInstallLocation()).toURL());
			} catch (MalformedURLException e) {}
		}
		return (URL[])list.toArray(new URL[list.size()]);

	}
	
	protected void verifyModelCaches() {
		// models = null; extensions = null;
		if(models != null && extensions != null && features != null) { return; }
		Map<String,IPluginModelBase> models = new HashMap<String, IPluginModelBase>();
		Map<String,Multimap<IPluginExtension,IPluginObject>> extensions = new HashMap<>();
		Map<String, IFeatureModel> features = new HashMap<>();

		// We create our own state to ensure that extensions are properly resolved
		// Pieced together from PDECore.findPluginInHost(String), TargetPlatformHelper,
		// WorkspacePluginModelManager, PDEState
//		PDEState state = new PDEState(
//				getWorkspacePluginPaths(TargetPlatformHelper.getPDEState()),
//				getTargetPluginPaths(TargetPlatformHelper.getPDEState()),
//				true, new NullProgressMonitor());
		// Following doesn't work: doesn't resolve extensions
//		PDEState state = TargetPlatformHelper.getPDEState();
		// TargetPlatformHelper.getPDEState().getTargetModels()
		for(IPluginModelBase pluginModel : getExternalPluginModels()) {
			String pluginId = getPluginId(pluginModel);
			models.put(pluginId, pluginModel);
			Multimap<IPluginExtension,IPluginObject> xm =
				loadExtensions(pluginId, pluginModel);
//			for(IPluginExtensionPoint extpt : pluginModel.getExtensions().getExtensionPoints()) {
//				if(!extpt.getFullId().startsWith(pluginId)) {
//					System.out.println(pluginId + ": extpt " + extpt.getId() + " has incorrect name?");
//				}
//			}
//			for(IPluginExtension ext : xm.keySet()) {
//				if(xm.get(ext).isEmpty()) {
//					System.out.println(pluginId + ": extension " + ext.getId() + " to " + ext.getPoint() + " has no children!");
//				}
//			}
			extensions.put(pluginId, xm);
		}
		
		for(IPluginModelBase pluginModel : getPluginModelManager().getWorkspaceModels()) {
			String pluginId = getPluginId(pluginModel);
			models.put(pluginId, pluginModel);
			Multimap<IPluginExtension,IPluginObject> xm =
				loadExtensions(pluginId, pluginModel);
//			for(IPluginExtensionPoint extpt : pluginModel.getExtensions().getExtensionPoints()) {
//				if(!extpt.getFullId().startsWith(pluginId)) {
//					System.out.println(pluginId + ": extpt " + extpt.getId() + " has incorrect name?");
//				}
//			}
//			for(IPluginExtension ext : xm.keySet()) {
//				if(xm.get(ext).isEmpty()) {
//					System.out.println(pluginId + ": extension " + ext.getId() + " to " + ext.getPoint() + " has no children!");
//				}
//			}
			extensions.put(pluginId, xm);
		}
		for(IFeatureModel featureModel : getFeatureModelManager().getExternalModels()) {
			String featureId = getFeatureId(featureModel);
			features.put(featureId, featureModel);
		}
		for(IFeatureModel featureModel : getFeatureModelManager().getWorkspaceModels()) {
			String featureId = getFeatureId(featureModel);
			features.put(featureId, featureModel);
		}
		this.models = models;
		this.extensions = extensions;
		this.features = features;
	}

	public IPluginModelBase findPluginModel(String pluginId) {
		verifyModelCaches();
		return models.get(pluginId);
	}

	public IFeatureModel findFeatureModel(String id) {
		verifyModelCaches();
		return features.get(id);
	}

	public IPluginModelBase findPluginModel(IProject project) {
		try {
			if(!project.hasNature(PDE.PLUGIN_NATURE)) { return null; }
			IPluginModelBase pmb = getPluginModelManager().findModel(project);
			if(pmb == null) { return null; }
			return findPluginModel(pmb.getPluginBase().getId());
		} catch(CoreException e) {
			return null;
		}
	}

	public IPluginModelBase findPluginModel(IProjectNature object) {
		return findPluginModel(object.getProject());
	}
		
	/**
	 * Find the IPluginModelBase that has the given location.
	 */
	public IPluginModelBase locatePluginModel(String bundleLocation) {
		for (IPluginModelBase modelBase : getActiveModels()) {
			if (bundleLocation.equals(modelBase.getInstallLocation())) {
				return modelBase;
			}
		}
		return null;
	}

	public Collection<IPluginModelBase> getActiveModels() {
		verifyModelCaches();
		return models.values();
	}

	public IPluginExtensionPoint findExtensionPoint(String pointId) {
		if(pointId == null) { return null; }
		int pointIndex = pointId.lastIndexOf('.');
		if (pointIndex < 0) { return null; }
		verifyModelCaches();
		IPluginModelBase pmb = findPluginModel(pointId.substring(0, pointIndex));
		if (pmb == null) { return null; }
		for (IPluginExtensionPoint pep : getExtensionPoints(pmb)) {
			if (pointId.equals(getFullId(pep))) {
				return pep;
			}
		}
		for(IPluginModelBase pluginModel : models.values()) {
			for(IPluginExtensionPoint extpt : pluginModel.getPluginBase()
					.getExtensionPoints()) {
				if(pointId.equals(getFullId(extpt))) { return extpt; }
			}
		}
		return null;
	}

	public Multimap<IPluginExtension,IPluginObject> getExtensions(IPluginExtensionPoint point) {
		verifyModelCaches();
		String pointId = getFullId(point);
		Multimap<IPluginExtension,IPluginObject> xts = HashMultimap.create();
		for (Multimap<IPluginExtension,IPluginObject> exts : extensions.values()) {
			for(IPluginExtension ext : exts.keySet()) {
				if (pointId.equals(ext.getPoint())) {
					xts.putAll(ext, exts.get(ext));
				}
			}
		}
		return xts;
	}

	public Multimap<IPluginExtension,IPluginObject> getExtensions(IPluginModelBase pluginModel) {
		verifyModelCaches();
		String pluginId = getPluginId(pluginModel);
		if(pluginId == null) { return null; }
		Multimap<IPluginExtension,IPluginObject> exts = extensions.get(pluginId);
		if(exts == null) { return null; }
		return exts;
	}

	/** Return the features that specify the provided plugin */
	public Collection<IFeatureModel> getFeaturesPackaging(IPluginModelBase plugin) {
		verifyModelCaches();
		String pluginId = getPluginId(plugin);
		if(pluginId == null) { return Collections.emptyList(); }
		Collection<IFeatureModel> results = new ArrayList<IFeatureModel>();
		for(IFeatureModel featureModel : features.values()) {
			if(!featureModel.isValid()) {
				continue;
			}
			IFeature feature = featureModel.getFeature();
			for(IFeaturePlugin p : feature.getPlugins()) {
				if(pluginId.equals(p.getId())) {
					results.add(featureModel);
					break;
				}
			}
		}
		return results;
	}

	/** Return the features that include the provided feature */
	public Collection<IFeatureModel> getFeaturesIncluding(IFeatureModel source) {
		verifyModelCaches();
		String featureId = getFeatureId(source);
		if(featureId == null) { return Collections.emptyList(); }
		Collection<IFeatureModel> results = new ArrayList<IFeatureModel>();
		for(IFeatureModel featureModel : features.values()) {
			if(!featureModel.isValid()) {
				continue;
			}
			IFeature feature = featureModel.getFeature();
			for(IFeatureChild p : feature.getIncludedFeatures()) {
				if(featureId.equals(p.getId())) {
					results.add(featureModel);
					break;
				}
			}
		}
		return results;
	}

	/**
	 * Return the features that require (or import) the provided feature /
	 * plugin
	 */
	public Collection<IFeatureModel> getFeaturesRequiring(IModel source) {
		verifyModelCaches();
		String sourceId = getId(source);
		if(sourceId == null) { return Collections.emptyList(); }
		Collection<IFeatureModel> results = new ArrayList<IFeatureModel>();
		for(IFeatureModel featureModel : features.values()) {
			if(!featureModel.isValid()) {
				continue;
			}
			IFeature feature = featureModel.getFeature();
			for(IFeatureImport p : feature.getImports()) {
				if(sourceId.equals(p.getId())) {
					results.add(featureModel);
					break;
				}
			}
		}
		return results;
	}

	private String getId(IModel model) {
		if(model instanceof ISharedPluginModel) { return getPluginId((ISharedPluginModel)model); }
		if(model instanceof IFeatureModel) { return getFeatureId((IFeatureModel)model); }
		return ((IIdentifiable)model).getId();
	}

	public Collection<IPluginExtensionPoint>  getExtensionPoints(IPluginModelBase pluginModel) {
		verifyModelCaches();
		assert getPluginId(pluginModel) != null;
		return Arrays.asList(pluginModel.getPluginBase().getExtensionPoints());
	}

	public String getFullId(IPluginExtensionPoint point) {
		String id = point.getFullId();
		if(id.indexOf('.') >= 0) { return id; }
		String pluginId = getPluginId(point.getModel());
		return pluginId + '.' + id;
	}

	public String getPluginId(ISharedPluginModel model) {
		if(model instanceof IIdentifiable) {
			String id = ((IIdentifiable)model).getId();
			if(id != null && id.length() > 0) { return id; }
		}
		if(model instanceof IPluginModelBase) {
			String id = ((IPluginModelBase)model).getPluginBase().getId();
			if(id != null && id.length() > 0) { return id; }
			if(((IPluginModelBase)model).getBundleDescription() != null) {
				id = ((IPluginModelBase)model).getBundleDescription().getSymbolicName();
			}
			if(id != null && id.length() > 0) { return id; }
		}
		IResource resource = model.getUnderlyingResource();
		if(resource != null) {
			IPluginModelBase modelBase = getPluginModel(resource);
			String id = modelBase.getPluginBase().getId();
			if(id != null && id.length() > 0) { return id; }
		}
		String installationLocation = model.getInstallLocation();
		for(IPluginModelBase modelBase : getExternalPluginModels()) {
			if(modelBase.getInstallLocation().equals(installationLocation)) {
				String id = modelBase.getPluginBase().getId();
				if(id != null && id.length() > 0) { return id; }
			}
		}
		throw new FerretFatalError("cannot determine plugin id");
	}

	private IPluginModelBase getPluginModel(IResource resource) {
		return getPluginModelManager().findModel(resource.getProject());
	}

	private IPluginModelBase[] getExternalPluginModels() {
		return getPluginModelManager().getExternalModels();
	}
	
	private String getFeatureId(IFeatureModel featureModel) {
		if(featureModel instanceof IIdentifiable) {
			String id = ((IIdentifiable)featureModel).getId();
			if(id != null && id.length() > 0) { return id; }
		}
		String id = ((IFeatureModel)featureModel).getFeature().getId();
		if(id != null && id.length() > 0) { return id; }

		IResource resource = featureModel.getUnderlyingResource();
		if(resource != null) {
			IFeatureModel model =
					getFeatureModelManager()
							.getFeatureModel(resource.getProject());
			id = model.getFeature().getId();
			if(id != null && id.length() > 0) { return id; }
		}
		String installationLocation = featureModel.getInstallLocation();
		for(IFeatureModel model : getFeatureModelManager()
				.getExternalModels()) {
			if(model.getInstallLocation().equals(installationLocation)) {
				id = model.getFeature().getId();
				if(id != null && id.length() > 0) { return id; }
			}
		}
		throw new FerretFatalError("cannot determine feature id");
	}

	protected Multimap<IPluginExtension,IPluginObject> loadExtensions(String pluginId, IPluginModelBase pmb) {
		Multimap<IPluginExtension,IPluginObject> result = HashMultimap.create();
		if(pmb.getExtensions().getExtensions().length == 0) {
			// nothing to do, so don't bother
			return result;
		}
		try {
			InputStream stream = null;
			try {
		        String xmlFileName = pmb.isFragmentModel() ? "fragment.xml" : "plugin.xml";
		        if (pmb instanceof IBundlePluginModelBase
						&& ((IBundlePluginModelBase) pmb).getExtensionsModel() != null) {
					IFile file = (IFile) ((IBundlePluginModelBase)pmb).getExtensionsModel().getUnderlyingResource();
					stream = file.getContents();
				} else if (pmb.getInstallLocation() != null) {
					stream = openFile(pmb.getInstallLocation(), xmlFileName);
				} else {
					throw new FerretFatalError("not sure what to do now!");
				}
				if(stream == null) {
//					System.out.println(getPluginId(pmb) + ": " + xmlFileName + " not found");
					return result;
		        }

				// use ExternalPluginModel: it doesn't care whether we have a fragment or plugin
				ExternalPluginModelBase apmb = pmb.isFragmentModel() ? new ExternalFragmentModel()  
						: new ExternalPluginModel();
				apmb.setInstallLocation(pmb.getInstallLocation());
				// load using standard plugin handler w/o abbreviation
				apmb.load(stream, false, new PluginHandler(false));	// causes reset
//				apmb.load(pmb.getBundleDescription(), TargetPlatformHelper.getPDEState());
				apmb.setBundleDescription(pmb.getBundleDescription());
				IPluginExtension sources[] = pmb.getExtensions().getExtensions();
				IPluginExtension cooked[] = apmb.getExtensions().getExtensions();
				assert sources.length == cooked.length;
				for(int i = 0; i < sources.length; i++) {
					for(IPluginObject child : cooked[i].getChildren()) {
						result.put(sources[i], child);
					}
				}
			} catch (CoreException e) {
				FerretPlugin.log(e);
			} finally {
				if (stream != null) { stream.close(); }
			}
		} catch (IOException e) {
			FerretPlugin.log(e);
		}
		return result;
	}

	public  InputStream openFile(String container, String fileName) throws IOException {
		File containerLocation = new File(container);
		if(!containerLocation.exists()) { return null; }
		if(containerLocation.isDirectory()) {
			File foundFile = new File(containerLocation, fileName);
			return foundFile.exists() ? new FileInputStream(foundFile): null;
		}
		ZipFile file = new ZipFile(container);
		ZipEntry entry = file.getEntry(fileName);
		return entry != null ? file.getInputStream(entry) : null;
	}

	public Object getValidationContext() {
		return this;
	}

	public boolean isValid(Object context) {
		return context == this;
	}

	public Collection<IPluginModelBase> getDependents(IPluginModelBase pluginModel) {
		verifyModelCaches();
		String pluginId = getPluginId(pluginModel);
		LinkedList<IPluginModelBase> dependents = new LinkedList<IPluginModelBase>();
		for(IPluginModelBase pmb : models.values()) {
			IPluginImport[] imports = pmb.getPluginBase().getImports();
			for(IPluginImport dep : imports) {
				if(pluginId.equals(dep.getId())) {
					dependents.add(models.get(getPluginId(dep.getPluginModel())));
				}
			}
		}
		return dependents;
	}

	public Collection<IPluginModelBase> getBundlesImporting(JavaPackage pkg) {
		verifyModelCaches();
		LinkedList<IPluginModelBase> importers = new LinkedList<IPluginModelBase>();
		for(IPluginModelBase pmb : models.values()) {
			ImportPackageSpecification[] imports =
					pmb.getBundleDescription().getImportPackages();
			for(ImportPackageSpecification dep : imports) {
				if (pkg.getPackage().equals(dep.getName())) {
					importers.add(pmb);
				}
			}
		}
		return importers;
	}

	public Collection<IPluginModelBase> getBundlesExporting(JavaPackage pkg) {
		verifyModelCaches();
		LinkedList<IPluginModelBase> exporters = new LinkedList<IPluginModelBase>();
		for(IPluginModelBase pmb : models.values()) {
			ExportPackageDescription[] exports =
					pmb.getBundleDescription().getExportPackages();
			for(ExportPackageDescription dep : exports) {
				if (pkg.getPackage().equals(dep.getName())) {
					exporters.add(pmb);
				}
			}
		}
		return exporters;
	}

	/**
	 * Generate an AbstractReference for the plugin.xml/fragment.xml
	 * @param pluginModel
	 * @return the reference
	 */
	public AbstractReference generateReference(ISharedPluginModel model) {
		try {
			String manifestName = model instanceof IFragmentModel ? "fragment.xml" : "plugin.xml";

			File location = new File(model.getInstallLocation());

			if(!location.exists()) { return null; }
			if(location.isDirectory()) {
				File foundFile = new File(location, manifestName);
				if(!foundFile.exists()) { return null; }
				IPath p = Path.fromOSString(foundFile.getPath());
				return new FileReference(p);
			}
			// Otherwise assume it's a jar file (XXX: perhaps we should assume it's a zip?) 
			// XXX: Trap ZipException?
			ZipFile file = new ZipFile(location);
			ZipEntry pluginxml = file.getEntry(manifestName);
			return pluginxml != null ? new ZipEntryReference(location.toString(), manifestName) : null;
		} catch(IOException e) {
			FerretPlugin.log(e);
			return null;
		}
	}

	public IDocument readFileContents(IFile file) {
		try {
			InputStream stream = file.getContents();
			try {
				return readFileContents(file.getContents(), file.getCharset());
			} finally {
					stream.close();
			}
		} catch(CoreException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	public IDocument readFileContents(InputStream stream, String charset) {
		IDocument document = new Document();
		try {
			InputStreamReader in = null;
			in = new InputStreamReader(new BufferedInputStream(stream), charset);
			StringBuffer buffer = new StringBuffer();
			char[] readBuffer = new char[8 * 1024];
			int n;
			while ((n = in.read(readBuffer)) > 0) {
				buffer.append(readBuffer, 0, n);
			}
			document.set(buffer.toString());
		} catch (IOException e) {
			/* do nothing */
		}
		return document;
	}

	public IPluginModelBase getDefiningModel(IPluginObject object) {
		IPluginModelBase definer = object.getPluginModel();
		if(definer == null) { return null; }
		return findPluginModel(getPluginId(definer));
	}

	public static String generateXPath(IPluginAttribute attr, IPluginExtension extension) {
		String notableAttributes[] = { "point", "id", "targetId" };
		// IPluginObject current = attr;
		// while(current != extension) {
		// elements.add(current);
		// current = current.getParent();
		// }
		// elements.add(extension);
		//
		StringBuffer generatedName = new StringBuffer();
		IPluginObject parent = attr.getParent();
		if(parent instanceof IPluginElement) {
			IPluginElement e = (IPluginElement)parent;
			List<String> attributes = new LinkedList<String>();
			generatedName.append('<');
			generatedName.append(parent.getName());
			for(String notableAttribute : notableAttributes) {
				if(e.getAttribute(notableAttribute) != null) {
					attributes.add(notableAttribute + "='"
							+ e.getAttribute(notableAttribute).getValue() + "'");
				}
			}
			if(!attributes.isEmpty()) {
				for(Iterator<String> iterator = attributes.iterator(); iterator.hasNext();) {
					generatedName.append(' ');
					generatedName.append(iterator.next());
				}
			}
			generatedName.append('>');
		}

		return generatedName.toString();
	}

	private PluginModelManager getPluginModelManager() {
		return PDECore.getDefault().getModelManager();
	}

	private FeatureModelManager getFeatureModelManager() {
		return PDECore.getDefault().getFeatureModelManager();
	}
}
