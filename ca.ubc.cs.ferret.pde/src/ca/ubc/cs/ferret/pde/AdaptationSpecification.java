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

import org.eclipse.pde.core.plugin.IPluginModelBase;

/**
 * Represent an adaptation specification.
 * @author Brian de Alwis
 */
public class AdaptationSpecification {
	protected IPluginModelBase plugin;
	protected String providedType;
	protected String desiredType;
	protected String adapterType;
	protected String sourceXPath;
	protected String destXPath;

	public AdaptationSpecification(String pt, String dt, IPluginModelBase plg,
			String at, String sXP, String dXP) {
		providedType = pt;
		desiredType = dt;
		adapterType = at;
		plugin = plg;
		sourceXPath = sXP;
		destXPath = dXP;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AdaptationSpecification)) { return false; }
		AdaptationSpecification other = (AdaptationSpecification)obj;
		return providedType.equals(other.providedType)
		&& desiredType.equals(other.desiredType)
		&& adapterType.equals(other.adapterType)
		&& sourceXPath.equals(other.sourceXPath)
		&& destXPath.equals(other.destXPath)
		&& PdeModelHelper.getDefault().getPluginId(plugin)
			.equals(PdeModelHelper.getDefault().getPluginId(other.plugin));
	}

	@Override
	public int hashCode() {
		return ((providedType.hashCode() * 37 + desiredType.hashCode()) * 37
				+ adapterType.hashCode()) * 37 + PdeModelHelper.getDefault().getPluginId(plugin).hashCode();
	}

	public String getProvidedType() {
		return providedType;
	}

	public String getDesiredType() {
		return desiredType;
	}

	public String getAdapterType() {
		return adapterType;
	}

	public String getSourceXPath() {
		return sourceXPath;
	}

	public String getDestinationXPath() {
		return destXPath;
	}

	public IPluginModelBase getSpecifyingPlugin() {
		return plugin;
	}

	public String toString() {
		return adapterType + ": " + providedType + " -> " + desiredType;
	}

}
