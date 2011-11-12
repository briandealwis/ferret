package ca.ubc.cs.ferret.pde;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ca.ubc.cs.ferret.FerretPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class FerretPdePlugin extends AbstractUIPlugin {
	public static final String pluginID = "ca.ubc.cs.ferret.pde";
	
	//The shared instance.
	private static FerretPdePlugin plugin;
	
	/**
	 * The constructor.
	 */
	public FerretPdePlugin() {
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
        FerretPlugin.getDefault().dropSphereHelper(PdeSphereHelper.getDefault());
		PdeSphereHelper.getDefault().stop();
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static FerretPdePlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path);
	}
}
