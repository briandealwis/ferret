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
package ca.ubc.cs.objhdl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ObjectMapping extends Plugin implements IRegistryChangeListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.ubc.cs.objhdl";

	public static final String mappersExtensionPoint = PLUGIN_ID + ".mappers";

	// The shared instance
	private static ObjectMapping plugin;
	
	protected IObjectMapper mapper;
	
	/**
	 * The constructor
	 */
	public ObjectMapping() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		RegistryFactory.getRegistry().removeRegistryChangeListener(this);
		mapper = null;
		ClassLookupCache.stop();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ObjectMapping getDefault() {
		return plugin;
	}

	public static IObjectMapper getPlatformMapper() {
		return getDefault().internalGetPlatformMapper();
	}
	
	public IObjectMapper internalGetPlatformMapper() {
		if(mapper == null) {
			RegistryFactory.getRegistry().addRegistryChangeListener(this, PLUGIN_ID);
			mapper = buildPlatformMapper(); 
		}
		return mapper;
	}

	protected IObjectMapper buildPlatformMapper() {
		CompositeMapper newMapper = new CompositeMapper();
		for(IConfigurationElement ce : RegistryFactory.getRegistry().getConfigurationElementsFor(mappersExtensionPoint)) {
			if(!"mapper".equals(ce.getName())) {
				getLog().log(new Status(IStatus.WARNING, ce.getNamespaceIdentifier(), -1,
						"Unknown configuration element '" + ce.getName() + "' for " + mappersExtensionPoint, null));
				continue;
			}
			try {
				for(IConfigurationElement mapping : ce.getChildren("mapping")) {
					String handleType = mapping.getAttribute("handle");
					String classType = mapping.getAttribute("class");
					if(handleType == null || handleType.trim().length() == 0) {
						getLog().log(new Status(IStatus.WARNING, ce.getNamespaceIdentifier(), -1,
								"Invalid handle type '" + handleType + "' for " + mappersExtensionPoint, null));
					} else if(classType == null || classType.trim().length() == 0) {
						getLog().log(new Status(IStatus.WARNING, ce.getNamespaceIdentifier(), -1,
								"Invalid class type '" + handleType + "' for " + mappersExtensionPoint, null));
					} else {
						IObjectMapper m = (IObjectMapper) ce.createExecutableExtension("class");
						newMapper.add(m, handleType, classType);
					}
				}
			} catch (CoreException e) {
				getLog().log(new Status(IStatus.WARNING, ce.getNamespaceIdentifier(), -1,
						"Unable to create mapper of type ''" + ce.getAttribute("class") + "' for " + mappersExtensionPoint, e));
			} catch (ClassCastException e) {
				getLog().log(new Status(IStatus.ERROR, ce.getNamespaceIdentifier(), -1,
						"Mapper ''" + ce.getAttribute("class") + "' for " + mappersExtensionPoint 
						+ " does not implement " + IObjectMapper.class.getName(), e));
			}
		}
		return newMapper;
	}

	public void registryChanged(IRegistryChangeEvent event) {
		mapper = null;
	}

	public static String describe(Object object) {
		String d[] = getPlatformMapper().describe(object);
		if(d == null) { return null; }
		return d[0] + ':' + d[1];
	}
	
	public static Object resolve(String handleIdentifier) {
		int colonIndex = handleIdentifier.indexOf(':');
		if(colonIndex < 0) { return null; }
		return getPlatformMapper().resolve(handleIdentifier.substring(0, colonIndex), 
				handleIdentifier.substring(colonIndex + 1));
	}
}
