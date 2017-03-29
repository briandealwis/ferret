/*******************************************************************************
 * Copyright (c) 2005, 2017 Manumitting Technologies Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manumitting Technologies Inc - initial API and implementation
 *******************************************************************************/

package ca.ubc.cs.ferret.pde;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.model.AbstractSphereFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.Sphere;
import ca.ubc.cs.ferret.pde.relations.AdaptableFromRelation;
import ca.ubc.cs.ferret.pde.relations.AdaptableToRelation;
import ca.ubc.cs.ferret.pde.relations.PdeExtensionPointExtensions;
import ca.ubc.cs.ferret.pde.relations.PdeExtensionsExtensionPoint;
import ca.ubc.cs.ferret.pde.relations.PdeIdentifierReferencedRelation;
import ca.ubc.cs.ferret.pde.relations.PdePluginDeclaredExtensionPoints;
import ca.ubc.cs.ferret.pde.relations.PdePluginDeclaredExtensions;
import ca.ubc.cs.ferret.pde.relations.PdeTypesReferencedRelation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 *
 */
public class PdeSphereFactory extends AbstractSphereFactory {
	public String getId() {
		return getClass().getName();
	}

	public String getDescription() {
		return "Eclipse plugin-related queries (PDE)";
	}

	public IStatus canCreate() {
		return Status.OK_STATUS;
	}

	public ISphere createSphere(IProgressMonitor monitor)
			throws FerretConfigurationException {
		Sphere tb = new Sphere("Eclipse PDE target image information");
		tb.register(ObjectOrientedRelations.OP_TYPES_REFERENCED,
				new PdeTypesReferencedRelation());
		tb.register(PdeSphereHelper.OP_DECLARED_EXTENSION_POINTS,
				new PdePluginDeclaredExtensionPoints(),
				new PdeExtensionsExtensionPoint());
		tb.register(PdeSphereHelper.OP_DECLARED_EXTENSIONS, new PdePluginDeclaredExtensions(),
				new PdeExtensionPointExtensions());
		tb.register(PdeSphereHelper.OP_IDENTIFIER_REFERENCED,
				new PdeIdentifierReferencedRelation());
		tb.register(PdeSphereHelper.OP_EXTENDED_BY, new PdeExtensionPointExtensions());
		tb.register(PdeSphereHelper.OP_EXTENDS, new PdeExtensionsExtensionPoint());
		tb.register(PdeSphereHelper.OP_ADAPTABLE_FROM, new AdaptableFromRelation());
		tb.register(PdeSphereHelper.OP_ADAPTABLE_TO, new AdaptableToRelation());
		return tb;
	}

	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	public ImageDescriptor getImageDescriptor() {
		return FerretPdePlugin.getImageDescriptor("icons/pde-tb.gif");
	}

	public String getHelpContextId() {
		return PdeSphereHelper.HCI_PDE_TB;
	}
}
