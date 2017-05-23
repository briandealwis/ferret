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
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.FerretPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class FerretJdtPlugin extends AbstractUIPlugin {
    public final static String pluginID = "ca.ubc.cs.ferret.jdt";

	//The shared instance.
	private static FerretJdtPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public FerretJdtPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		JdtSphereHelper.shutdown();
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static FerretJdtPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ca.ubc.cs.ferret.jdt", path);
	}

	public static boolean logJavaModelExceptions() {
//		return getDefault().getPreferenceStore().getBoolean(IFerretJavaPreferenceConstants.PREF_SHOW_);
		return FerretPlugin.hasDebugOption(pluginID, "debug/logJavaModelExceptions");
	}

	public static boolean suppressJDTNullPointerExceptions() {
		return !FerretPlugin.hasDebugOption(pluginID, "debug/logNullPointerExceptions");	
	}
}
