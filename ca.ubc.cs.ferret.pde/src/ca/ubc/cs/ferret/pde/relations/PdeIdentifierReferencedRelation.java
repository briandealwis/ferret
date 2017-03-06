package ca.ubc.cs.ferret.pde.relations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.IPluginParent;

import com.google.common.collect.Multimap;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.pde.PdeIdentifier;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import ca.ubc.cs.ferret.pde.PdeObject;

public class PdeIdentifierReferencedRelation extends
		AbstractCollectionBasedRelation<PdeIdentifier> {

	public PdeIdentifierReferencedRelation() {}

	@Override
	protected Class<PdeIdentifier> getInputType() {
		return PdeIdentifier.class;
	}

	@Override
	protected Collection<?> realizeCollection(PdeIdentifier input) {
		// FIXME: What about references in schemas?!
	    	Collection<Object> results = new HashSet<Object>();
	    	Collection<IPluginModelBase> pluginModels = PdeModelHelper.getDefault().getActiveModels();
		monitor.beginTask("Searching plugin.xmls for references to " + input.getIdentifier(), 3 * pluginModels.size());
	    	for (IPluginModelBase pluginModel : pluginModels) {
	    		String pluginId = PdeModelHelper.getDefault().getPluginId(pluginModel);
	    		monitor.subTask("Examining extensions in " + pluginId);
	
	    		Multimap<IPluginExtension,IPluginObject> extensions =
	    			PdeModelHelper.getDefault().getExtensions(pluginModel);
	    		for(IPluginExtension ext : extensions.keySet()) {
	    			for(IPluginObject child : extensions.get(ext)) {
	    				checkExtensionElement(results, input.getIdentifier(), child, pluginModel, ext);
	    			}
	    		}
			monitor.worked(1);
			
			String location = pluginModel.getInstallLocation();
			if(location != null) {
				File bundleLoc = new File(location);
				if(bundleLoc.exists()) {
					processOSGIINF(input, bundleLoc, results);
				}
			}
			monitor.worked(1);
			
//			processSchemas(pluginModel, results);
			monitor.worked(1);
	    	}
	    	monitor.done();
	    	return results;
	}

	private void processOSGIINF(PdeIdentifier input, File bundleLoc,
			Collection<Object> results) {
		if (bundleLoc.isFile() && bundleLoc.getName().endsWith(".jar")) {
			try {
				ZipFile zp = new ZipFile(bundleLoc);
				ZipEntry osgiDir = zp.getEntry("OSGI-INF");
				if (!osgiDir.isDirectory()) {
					return;
				}
				Enumeration<? extends ZipEntry> entries = zp.entries();
				while (entries.hasMoreElements()) {
					ZipEntry ze = entries.nextElement();
					if (ze.getName().startsWith("OSGI-INF/")
							&& ze.getName().endsWith(".xml")) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(zp.getInputStream(ze)));
						try {
							if (checkMention(input, br)) {
								results.add(ze);
							}
						} finally {
							br.close();
						}
					}
				}
			} catch (IOException e) {
				// log it
			}
		} else if (bundleLoc.isDirectory()) {
			// OSGI-INF is merely a convention, but it seems widely advocated
			// and followed
			File osgiDir = new File(bundleLoc, "OSGI-INF"); // $NON-NLS-1$
			if (!osgiDir.isDirectory()) {
				return;
			}
			for (File compXml : osgiDir.listFiles()) {
				try {
					if (compXml.isFile() && compXml.getName().endsWith(".xml")) {
						BufferedReader br = new BufferedReader(new FileReader(
								compXml));
						try {
							if (checkMention(input, br)) {
								results.add(compXml);
							}
						} finally {
							br.close();
						}
					}
				} catch (IOException e) {
					// log it
				}
			}
		}
	}

	private boolean checkMention(PdeIdentifier input, BufferedReader br) throws IOException {
		String pattern = '\"' + input.getIdentifier() + '\"';
		String line;
		while((line = br.readLine()) != null) {
			if(((String)line).contains(pattern)) {
				return true;
			}
		}
		return false;
	}

	protected void checkExtensionElement(Collection<Object> results, String identifier,
	    		IPluginObject po, IPluginModelBase pluginModel, IPluginExtension extension) {
	    	if(po instanceof IPluginElement) {
	    		for(IPluginAttribute attr : ((IPluginElement)po).getAttributes()) {
	    			checkExtensionElement(results, identifier, attr, pluginModel, extension);
	    		}
	    	}
	    	if(po instanceof IPluginAttribute) {
	    		IPluginAttribute attr = (IPluginAttribute)po; 
	    		if(identifier.equals(attr.getValue().trim())) {
	    			if(attr instanceof PdeObject) {
	    				results.add(PdeObject.wrap(pluginModel, attr));	// shouldn't need the pluginModel any more
	    			}
	    		}
	    	}
	    	if(po instanceof IPluginParent) {
	    		for(IPluginObject child : ((IPluginParent)po).getChildren()) {
	    			checkExtensionElement(results, identifier, child, pluginModel, extension);
	    		}
	    	}
    }

}
