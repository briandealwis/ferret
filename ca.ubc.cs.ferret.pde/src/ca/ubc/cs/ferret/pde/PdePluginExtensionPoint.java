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

import java.io.PrintWriter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;

public class PdePluginExtensionPoint extends PdeObject implements IPluginExtensionPoint {
	IPluginExtensionPoint extpt;

	public PdePluginExtensionPoint(IPluginModelBase plugin, IPluginExtensionPoint extpt) {
		super(plugin);
		this.extpt = extpt;
	}

	@Override
	protected IPluginObject getWrappedObject() {
		return extpt;
	}

	public String getFullId() {
		return extpt.getFullId();
	}

	public String getSchema() {
		return extpt.getSchema();
	}

	public void setSchema(String schema) throws CoreException {
		extpt.setSchema(schema);
	}

	public String getName() {
		return extpt.getName();
	}

	public IPluginObject getParent() {
		return extpt.getParent();		// FIXME: should be wrapped?
	}

	public void setName(String name) throws CoreException {
		extpt.setName(name);
	}

	public void write(String indent, PrintWriter writer) {
		extpt.write(indent, writer);
	}

	public String getId() {
		return extpt.getId();
	}

	public void setId(String id) throws CoreException {
		extpt.setId(id);
	}
}
