package ca.ubc.cs.ferret.sphereconfig;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.AbstractSphereFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ISphereCompositor;
import ca.ubc.cs.ferret.model.ISphereCompositorFactory;
import ca.ubc.cs.ferret.model.ISphereFactory;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public abstract class AbstractSphereCompositorFactory 
		extends AbstractSphereFactory
		implements ISphereCompositorFactory, IExecutableExtension {
	protected List<ISphereFactory> sphereFactories = new ArrayList<ISphereFactory>();
	protected IConfigurationElement definingCE;
	public static final String ATTR_ICON = "icon";
	public static final String ATTR_HELP_CONTEXT_ID = "helpContextId";

	public AbstractSphereCompositorFactory() {}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		definingCE = config;
	}

	public abstract String getDescription();
	
	public void add(ISphereFactory factory) {
		sphereFactories.add(factory);
	}

	public List<ISphereFactory> getComposedSphereFactories() {
		return sphereFactories;
	}

	public void moveSphereDown(ISphereFactory t) {
		int i = sphereFactories.indexOf(t);
		if(i < 0 || i + 1 >= sphereFactories.size()) { return; }
		sphereFactories.remove(i);
		sphereFactories.add(i + 1, t);
	}

	public void moveSphereUp(ISphereFactory t) {
		int i = sphereFactories.indexOf(t);
		if(i <= 0 || i >= sphereFactories.size()) { return; }
		sphereFactories.remove(i);
		sphereFactories.add(i - 1, t);
	}

	public boolean remove(ISphereFactory factory) {
		return sphereFactories.remove(factory);
	}

	public IStatus canCreate() {
		MultiStatus status = new MultiStatus(FerretPlugin.pluginID, FerretErrorConstants.CONFIGURATION_ERROR,
				"Error validating spheres", null);
		status.add(canCreateCompositor());
		for(ISphereFactory f : sphereFactories) {
			status.add(f.canCreate());
		}
		return status;
	}
	
	public abstract IStatus canCreateCompositor();

	public String getId() {
		return getClass().getName();
	}
	
	public String toString() {
		return getClass().getName() + "(" + FerretPlugin.compactPrettyPrint(sphereFactories) + ")";
	}

	public ISphere createSphere(IProgressMonitor monitor)
			throws FerretConfigurationException {
		monitor.beginTask("Creating sphere " + getId(), sphereFactories.size());
		ISphereCompositor tb = createCompositor();
		for(ISphereFactory f : sphereFactories) {
			tb.add(f.createSphere(new SubProgressMonitor(monitor,1)));
		}
		monitor.done();
		return tb;
	}

	protected abstract ISphereCompositor createCompositor();

	public IConfigurationElement getDefiningConfigurationElement() {
		return definingCE;
	}

	public ImageDescriptor getImageDescriptor() {
		if(definingCE == null) { return null; }
		String iconFile = definingCE.getAttribute(ATTR_ICON);
		if(iconFile == null || iconFile.length() == 0) { return null; }
		return AbstractUIPlugin.imageDescriptorFromPlugin(definingCE.getNamespaceIdentifier(), iconFile);
	}
	
	public String getHelpContextId() {
		if(definingCE == null) { return null; }
		return definingCE.getAttribute(ATTR_HELP_CONTEXT_ID);
	}

	@Override
	public ISphereFactory clone() {
		AbstractSphereCompositorFactory cl = (AbstractSphereCompositorFactory)super.clone();
		cl.sphereFactories = new ArrayList<ISphereFactory>(sphereFactories.size());
		for(ISphereFactory tf : sphereFactories) {
			cl.sphereFactories.add(tf.clone());
		}
		return cl;
	}

}
