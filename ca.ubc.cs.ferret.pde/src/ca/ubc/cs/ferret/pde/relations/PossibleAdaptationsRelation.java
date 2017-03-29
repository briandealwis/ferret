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
package ca.ubc.cs.ferret.pde.relations;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.pde.AdaptationSpecification;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IType;
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;

public abstract class PossibleAdaptationsRelation extends
		AbstractCollectionBasedRelation<IType> {

	protected final static String adapterFactoryExtensionPoint =
        "org.eclipse.core.runtime.adapters";

	protected Collection<AdaptationSpecification> results;
	protected Set<String> relevantTypeNames;

	@Override
	protected void init() {
		super.init();
		relevantTypeNames = new HashSet<String>();
	}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected IType checkInput(IType input) {
		relevantTypeNames.add(input.getFullyQualifiedName());
		return super.checkInput(input);
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
        monitor.beginTask("Finding adaptable situation", 13);
        
        try {
            if(monitor.isCanceled()) { throw new OperationCanceledException(); }
            IPluginExtensionPoint extpt = PdeModelHelper.getDefault().findExtensionPoint(adapterFactoryExtensionPoint);
            if(extpt == null) { return Collections.EMPTY_LIST; }
            monitor.worked(5);
            results = new HashSet<AdaptationSpecification>();
            Multimap<IPluginExtension,IPluginObject> extensions =
            	PdeModelHelper.getDefault().getExtensions(extpt);
            for(IPluginExtension extension : extensions.keySet()) {
                if(monitor.isCanceled()) { throw new OperationCanceledException(); }
            	/* Adapter factories have config element named "factory"
            	 * with two fields: class is the factory clas, and adaptableType
            	 * is the input type (which should match one of those found
            	 * above in possibleTypes[]).  Each factory configuration then lists
            	 * the types it can adapt adaptableType to; we gather this list. 
            	 *     	<factory
            	 *    		adaptableType="org.eclipse.jdt.core.IJavaElement"
            	 *     		class="ca.ubc.cs.ferret.jdt.JdtDisplayObjectFactory">
            	 *     			<adapter type="ca.ubc.cs.ferret.display.IDisplayObject" />
            	 *    	</factory> */
            	for(IPluginObject child : extensions.get(extension)) {
            		if(!child.getName().equals("factory") || !(child instanceof IPluginElement)) { continue; }
            		IPluginElement factoryElement = (IPluginElement)child;
            		IPluginAttribute adapterFactoryClassAttribute = factoryElement.getAttribute("class");
            		IPluginAttribute sourceTypeAttribute = factoryElement.getAttribute("adaptableType");
            		if(adapterFactoryClassAttribute == null || sourceTypeAttribute == null) { continue; }
            		
            		if(!checkFactory(extension.getPluginModel(), extension, adapterFactoryClassAttribute, 
            				sourceTypeAttribute)) { continue; }

            		for(IPluginObject factoryChild : factoryElement.getChildren()) {
            			if(!factoryChild.getName().equals("adapter") || !(factoryChild instanceof IPluginElement)) {
            				continue;
            			}
            			IPluginElement adapterElement = (IPluginElement)factoryChild;
            			IPluginAttribute destinationTypeAttribute = adapterElement.getAttribute("type");
            			if(destinationTypeAttribute == null) { continue; }
                        if(monitor.isCanceled()) { throw new OperationCanceledException(); }

            			if(isRelevantSpecification(extension.getPluginModel(), extension, factoryElement, 
            					adapterFactoryClassAttribute, sourceTypeAttribute, destinationTypeAttribute)) {
	        		        results.add(new AdaptationSpecification(sourceTypeAttribute.getValue(), 
	        		        		destinationTypeAttribute.getValue(),
	        		        		PdeModelHelper.getDefault().getDefiningModel(child),
	        		        		adapterFactoryClassAttribute.getValue(), 
	        		        		PdeModelHelper.generateXPath(sourceTypeAttribute, extension),
	        		        		PdeModelHelper.generateXPath(destinationTypeAttribute, extension)));
            			}
            		}
            	}
            }
        } finally { monitor.done(); }
        return results;
	}

	/**
     * Should this factory specification be checked?
     * @param pluginModel the plugin with the specification
     * @param extension the extension with the specification
     * @param adaptorFactoryClassAttribute the class responsible for performing the adaptation
     * @param sourceTypeAttribute the source type for adaptations
     * @return true if the factory should be checked, false otherwise
     */
    protected abstract boolean checkFactory(IPluginModelBase pluginModel,
			IPluginExtension extension, 
			IPluginAttribute adaptorFactoryClassAttribute,
			IPluginAttribute sourceTypeAttribute);

	/**
	 * Does this adaptation spec pass muster?
	 * @param pluginModel the plugin with the specification
	 * @param extension	the extension with the specification
	 * @param factoryElement the factory specification element
	 * @param adaptorFactoryClassAttribute	the class responsible for performing the adaptation 
	 * @param sourceTypeAttribute the source type for adaptations
	 * @param destinationTypeAttribute the resulting type of an adaptation
	 * @return true if this specification meets the muster and should be reported
	 */
	protected abstract boolean isRelevantSpecification(IPluginModelBase pluginModel,
			IPluginExtension extension, IPluginElement factoryElement,
			IPluginAttribute adaptorFactoryClassAttribute,
			IPluginAttribute sourceTypeAttribute,
			IPluginAttribute destinationTypeAttribute);


}
