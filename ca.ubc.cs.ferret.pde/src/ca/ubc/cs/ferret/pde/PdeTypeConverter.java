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

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.AbstractTypeConverter;
import ca.ubc.cs.ferret.types.ConversionException;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.eclipse.pde.core.IIdentifiable;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.plugin.ImportObject;
import org.eclipse.pde.internal.core.text.bundle.PackageObject;

public class PdeTypeConverter extends AbstractTypeConverter {

	public PdeTypeConverter() {}

	public ConversionResult<?> convert(Object object,
			ConversionSpecification spec, ISphere sphere) throws ConversionException {
		try {
			if(spec.getDesiredClass() == IPluginModelBase.class) {
				if(object instanceof IPluginImport) {
					return wrap(spec, Fidelity.Exact, IPluginModelBase.class,
							PdeModelHelper.getDefault().findPluginModel(((IPluginImport)object).getId()));
				} else if(object instanceof IProject) {
					IPluginModelBase plugin =
						PdeModelHelper.getDefault().findPluginModel((IProject)object);
					if(plugin == null) { return null; }
					return wrap(spec, Fidelity.Exact, IPluginModelBase.class, plugin);
				} else if(object instanceof IProjectNature) {
					IPluginModelBase plugin =
						PdeModelHelper.getDefault().findPluginModel((IProjectNature)object);
					if(plugin == null) { return null; }
					return wrap(spec, Fidelity.Exact, IPluginModelBase.class, plugin);
				} else if(object instanceof IIdentifiable) {
					IPluginModelBase plugin =
							PdeModelHelper.getDefault().findPluginModel(
									((IIdentifiable)object).getId());
					return wrap(spec, Fidelity.Exact, IPluginModelBase.class, plugin);
				}
			} else if(spec.getDesiredClass() == IPluginImport.class) {
				// This is more here for proof-of-concept
				if(object instanceof ImportObject) {
					return wrap(spec, Fidelity.Exact, IPluginImport.class,
							((ImportObject)object).getImport());
				}
			} else if(spec.getDesiredClass() == PdeIdentifier.class) {
				// FIXME: PdeIdentifier should be a contextualized object, with JavaTypePdeIdentifier,
				// ResourcePdeIdentifier, etc.  The problem is that a resource is specified relative to
				// to its defining project.  Although I don't think there's a way to reference resources 
				// from other plugins, should such a capability be added, then it's likely there will be
				// a different syntax as compared to references within the plugin.
				if(object instanceof IType) {
					IType type = (IType)object;
			        if(!isPdeProject(type.getJavaProject().getProject())) { return null; }
					return wrap(spec, Fidelity.Exact, PdeIdentifier.class,
							new PdeIdentifier(type.getFullyQualifiedName()));
				} else if(object instanceof IResource) {
			        if(!isPdeProject(((IResource)object).getProject())) { return null; }
			        if(object instanceof IProject) {
			        	// FIXME: should find its plugin identifier
						String pathName = ((IResource)object).getFullPath().toPortableString();
						return wrap(spec, Fidelity.Equivalent, PdeIdentifier.class,
								new PdeIdentifier(pathName));
			        } else if(object instanceof IFile || object instanceof IFolder) {
			        	// This can result in false positives, but who cares.  I just want to graduate.
			        	String relativePathname = ((IResource)object).getFullPath().removeFirstSegments(1).toPortableString();
						return wrap(spec, Fidelity.Equivalent, PdeIdentifier.class,
								new PdeIdentifier(relativePathname));
			        }
				}
			} else if(spec.getDesiredClass() == IFeatureModel.class) {
				if(object instanceof IIdentifiable) {
					IFeatureModel feature =
							PdeModelHelper.getDefault().findFeatureModel(
									((IIdentifiable)object).getId());
					return wrap(spec, Fidelity.Exact, IFeatureModel.class, feature);
				}
			} else if(spec.getDesiredClass() == IModel.class) {
				if(object instanceof IModel) {
					return wrap(spec, Fidelity.Exact, IModel.class, (IModel)object);
				} else if(object instanceof IIdentifiable) {
					IFeatureModel feature =
							PdeModelHelper.getDefault().findFeatureModel(
									((IIdentifiable)object).getId());
					if(feature != null) { return wrap(spec, Fidelity.Exact,
							IFeatureModel.class, feature); }
					IPluginModelBase plugin =
							PdeModelHelper.getDefault().findPluginModel(
									((IIdentifiable)object).getId());
					if(plugin != null) { return wrap(spec, Fidelity.Exact,
							IPluginModelBase.class, plugin); }
				}
			} else if(spec.getDesiredClass() == JavaPackage.class) {
				if(object instanceof ImportPackageSpecification) {
					return wrap(
							spec,
							Fidelity.Equivalent,
							JavaPackage.class,
							new JavaPackage(((ImportPackageSpecification)object)
									.getName()));
				} else if(object instanceof ExportPackageDescription) {
					return wrap(spec, Fidelity.Equivalent, JavaPackage.class,
							new JavaPackage(((ExportPackageDescription)object).getName()));
				} else if(object instanceof PackageObject) {
					return wrap(spec, Fidelity.Equivalent, JavaPackage.class,
							new JavaPackage(((PackageObject)object).getName()));
				} else if(object instanceof IPackageFragment) { return wrap(spec,
						Fidelity.Equivalent, JavaPackage.class, new JavaPackage(
								((IPackageFragment)object).getElementName())); }
			}
		} catch(ClassNotFoundException e) {
			throw new ConversionException(e);
		}
		return null;
	}

	protected boolean isPdeProject(IProject project) {
        try {
        	return project.hasNature(PDE.PLUGIN_NATURE) 
        			|| project.hasNature(PDE.FEATURE_NATURE);
        } catch(CoreException e) {
        	FerretPlugin.log(e.getStatus());
    		return false;
        }
	}

}
