package ca.ubc.cs.ferret.history;
import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import ca.ubc.cs.ferret.FerretPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class FerretHistoryPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static FerretHistoryPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public FerretHistoryPlugin() {
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
        FerretPlugin.getDefault().dropSphereHelper(HistorySphereHelper.getDefault());
		HistorySphereHelper.getDefault().stop();
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static FerretHistoryPlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("ca.ubc.cs.ferret.history", path);
	}
}
