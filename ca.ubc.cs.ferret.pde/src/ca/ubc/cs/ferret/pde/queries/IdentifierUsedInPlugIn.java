/*******************************************************************************
 * Copyright (c) 2004 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.pde.queries;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.StupidlySimpleRelation;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.pde.PdeIdentifier;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import ca.ubc.cs.ferret.references.AbstractReference;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.IPluginParent;

/**
 * @author bsd
 */
public class IdentifierUsedInPlugIn extends PdeSingleParmConceptualQuery<PdeIdentifier> {

    protected Clustering<Object> byExtpts;
    protected Clustering<Object> byPlugins; 

    /**
     * @param details
     */
    public IdentifierUsedInPlugIn() {
        super();
    }

    protected void internalRun(IProgressMonitor monitor) {
        try {
        	byExtpts = new Clustering<Object>("extension points");
        	addClustering(byExtpts);
        	byPlugins = new Clustering<Object>("defining plug-in");
        	addClustering(byPlugins);

        	Collection<IPluginModelBase> pluginModels = PdeModelHelper.getDefault().getActiveModels();

			monitor.beginTask(getDescription(), pluginModels.size());
        	for (IPluginModelBase pluginModel : pluginModels) {
        		String pluginId = PdeModelHelper.getDefault().getPluginId(pluginModel);
        		monitor.subTask("Examining extensions in " + pluginId);

        		Multimap<IPluginExtension,IPluginObject> extensions =
        			PdeModelHelper.getDefault().getExtensions(pluginModel);
        		for(IPluginExtension ext : extensions.keySet()) {
        			for(IPluginObject child : extensions.get(ext)) {
        				checkExtensionElement(child, pluginModel, ext);
        			}
        			monitor.worked(1);
        		}
        	}
        } finally {
            pending = false;
            monitor.done();
        }

    }

    protected void checkExtensionElement(IPluginObject po, IPluginModelBase pluginModel,
    		IPluginExtension extension) {
    	if(po instanceof IPluginElement) {
    		for(IPluginAttribute attr : ((IPluginElement)po).getAttributes()) {
    			checkExtensionElement(attr, pluginModel, extension);
    		}
    	}
    	if(po instanceof IPluginAttribute) {
    		IPluginAttribute attr = (IPluginAttribute)po; 
    		if(parameter.getIdentifier().equals(attr.getValue())) {	
                try {
        			IPluginExtensionPoint point = PdeModelHelper.getDefault()
        				.findExtensionPoint(extension.getPoint()); 
                	SimpleSolution s = new SimpleSolution(this, this);
                	AbstractReference ref = createXMLWrapper(attr, pluginModel);
                	ref.setText(PdeModelHelper.generateXPath(attr, extension));
                	s.add("reference", ref);
                	s.setPrimaryEntityName("reference");
                	s.add(new StupidlySimpleRelation(ref, "extension of", point));
                	s.add(new StupidlySimpleRelation(ref, "defining plugin", pluginModel));
                	addSolution(s);
                	byExtpts.findCluster(point).add(s);
                	byPlugins.findCluster(pluginModel).add(s);
                } catch(IOException e) {
                    FerretPlugin.log(e);
                }
    		}
    	}
    	if(po instanceof IPluginParent) {
    		for(IPluginObject child : ((IPluginParent)po).getChildren()) {
    			checkExtensionElement(child, pluginModel, extension);
    		}
    	}
    }


    public String getDescription() {
        return "referenced in plug-in extensions";
    }

}
