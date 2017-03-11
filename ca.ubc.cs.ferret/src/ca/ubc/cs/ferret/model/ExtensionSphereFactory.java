/*******************************************************************************
 * Copyright (c) 2017 Manumitting Technologies Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manumitting Technologies Inc - initial API and implementation
 *******************************************************************************/

package ca.ubc.cs.ferret.model;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretPlugin;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.menus.MenuHelper;

/**
 * A sphere factory driven from the
 * {@code ca.ubc.cs.ferret.sphereConfigurations} extension point.
 */
public class ExtensionSphereFactory extends AbstractSphereFactory {
	private IConfigurationElement configElement;
	private ISphereFactory wrapped;

	public ExtensionSphereFactory(IConfigurationElement ce) {
		this.configElement = ce;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == ISphereFactory.class) {
			try {
				return adapter.cast(getWrapped());
			} catch (CoreException ex) {
				FerretPlugin.log(ex);
				return null;
			}
		}
		return super.getAdapter(adapter);
	}

	@Override
	public String getId() {
		return configElement.getAttribute("id");
	}

	@Override
	public String getDescription() {
		return configElement.getAttribute("description");
	}

	@Override
	public IStatus canCreate() {
		try {
			return getWrapped().canCreate();
		} catch (CoreException ex) {
			FerretPlugin.log(ex);
			return ex.getStatus();
		}
	}

	private ISphereFactory getWrapped() throws CoreException {
		if (wrapped != null) {
			return wrapped;
		}
		return wrapped = (ISphereFactory) configElement.createExecutableExtension("class");
	}

	@Override
	public ISphere createSphere(IProgressMonitor monitor) throws FerretConfigurationException {
		try {
			return getWrapped().createSphere(monitor);
		} catch (CoreException ex) {
			throw new FerretConfigurationException(ex.getStatus());
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		String uri = MenuHelper.getIconURI(configElement, "icon");
		if (uri == null) {
			return null;
		}
		try {
			return ImageDescriptor.createFromURL(new URL(uri));
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	@Override
	public String getHelpContextId() {
		return configElement.getAttribute("helpContextId");
	}
}
