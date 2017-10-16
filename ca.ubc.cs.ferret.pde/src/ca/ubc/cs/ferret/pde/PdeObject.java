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
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.ISharedPluginModel;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelProvider;

/**
 * A wrapper around PDE Plugin Objects.  Sadly necessary as PDE
 * doesn't provide a populated model, so we have to spoof it up.  This breaks the
 * links between the plugin elements and the defining plugin.
 */
public abstract class PdeObject implements IPluginObject {
	public IPluginModelBase plugin;

	public PdeObject(IPluginModelBase plugin) {
		this.plugin = plugin;
	}

	public ISharedPluginModel getModel() {
		return plugin;
	}

	public IPluginBase getPluginBase() {
		IPluginModelBase pluginModel = getPluginModel();
		return pluginModel != null ? pluginModel.getPluginBase() : null;
	}

	public IPluginModelBase getPluginModel() {
		if (plugin instanceof IBundlePluginModelProvider)
			return ((IBundlePluginModelProvider)plugin).getBundlePluginModel();
		
		return plugin instanceof IPluginModelBase? (IPluginModelBase)plugin : null;
	}

	public String getResourceString(String key) {
		return plugin.getResourceString(key);
	}

	public String getTranslatedName() {
		return getResourceString(getName());
	}

	public boolean isInTheModel() {
		return true;
	}

	public void setInTheModel(boolean inModel) {		
	}

	public boolean isValid() {
		return true;
	}
	
	public <T> T getAdapter(Class<T> adapter) {
		return getWrappedObject().getAdapter(adapter);
	}


	protected abstract IPluginObject getWrappedObject();

	public static IPluginObject wrap(IPluginModelBase plugin, IPluginObject po) {
		if(po instanceof PdeObject) { return po; }
		if(po instanceof IPluginAttribute) {
			return new PdePluginAttribute(plugin, (IPluginAttribute)po);
		} else if(po instanceof IPluginExtension) {
			return new PdePluginExtension(plugin, (IPluginExtension)po);
		} else if (po instanceof IPluginExtensionPoint) {
			return new PdePluginExtensionPoint(plugin, (IPluginExtensionPoint)po);
		}
		throw new FerretFatalError("unhandled PDE IPluginObject: " + po.getClass().getName());
	}

	public static IPluginObject[] wrap(IPluginModelBase pmb, IPluginObject po[]) {
		// FIXME: should build this using reflection so we build the right type...
		IPluginObject newPO[] = new IPluginObject[po.length];
		for(int i = 0; i < po.length; i++) {
			newPO[i] = wrap(pmb, po[i]);
		}
		return newPO;
	}

	public static <T extends IPluginObject> T unwrap(T po) {
		while(po instanceof PdeObject) {	// use "while" instead of "if" just in case... 
			po = (T)((PdeObject)po).getWrappedObject();
		}
		return po;
	}

}
