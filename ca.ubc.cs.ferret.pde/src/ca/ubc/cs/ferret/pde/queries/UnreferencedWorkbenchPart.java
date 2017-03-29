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
package ca.ubc.cs.ferret.pde.queries;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.SimpleSolution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.pde.internal.core.natures.PDE;

public class UnreferencedWorkbenchPart extends PdeSingleParmConceptualQuery<IType> {
    
    public UnreferencedWorkbenchPart() {
    }

    public boolean validateParameter(IType type) {
        try {
            if(!type.isClass()) { return false; }
            if(Flags.isAbstract(type.getFlags())) { return false; }
            IProject project = type.getJavaProject().getProject();
            	return project.hasNature(PDE.PLUGIN_NATURE)
            		|| project.hasNature(PDE.FEATURE_NATURE);
        } catch(CoreException e) {
        	FerretPlugin.log(e.getStatus());
        	return false;
        }
    }

    public String getDescription() {
        return "workbench part " + FerretPlugin.prettyPrint(parameter) + " unreferenced by any plugin.xml" ;
    }

    protected void internalRun(IProgressMonitor monitor) {
        try {
            monitor.beginTask(getClass().getName(), 10);
            if(isAbstract(parameter)) { return; }
            monitor.worked(1);
            if(!isWorkbenchPart(parameter, new SubProgressMonitor(monitor, 4))) { return; }
            if(isReferencedInPluginXml(parameter, new SubProgressMonitor(monitor, 4))) { return; }
            SimpleSolution s = new SimpleSolution(this, null);
            s.add("unreferenced", parameter);
            addSolution(s);
        } catch(JavaModelException e) {
            FerretPlugin.log(new Status(IStatus.WARNING, FerretPlugin.pluginID, 0,
                    "JavaModelException prevented completion of UnreferencedWorkbenchPart query", e));
        } finally {
            monitor.done();
        }
    }

    private boolean isAbstract(IType t) throws JavaModelException {
        return Flags.isAbstract(t.getFlags());
    }

    private boolean isWorkbenchPart(IType t, IProgressMonitor monitor) {
        monitor.beginTask("Determining if type is a workbench part", 5);
        try {
            IType supers[] = JavaModelHelper.getDefault().getSupertypes(t, new SubProgressMonitor(monitor, 3));
            for(int i = 0; i < supers.length; i++) {
                String fqn = supers[i].getFullyQualifiedName(); 
                if(fqn.equals("org.eclipse.ui.IWorkbenchPart")) { return true; }
            }
            return false;
        } finally { monitor.done(); }
    }

    /* Could definitely see some declarative infrastructure specifying 
     * {{interface names}, {{ extension point, "xpath expression" } ...}},
     */
    private String extensionPoints[] = { "org.eclipse.ui.editors", "org.eclipse.ui.views" };
    private boolean isReferencedInPluginXml(IType t, IProgressMonitor monitor) throws JavaModelException {
        try {
            String fqtn = t.getFullyQualifiedName();
            IExtensionRegistry registry = RegistryFactory.getRegistry();
            monitor.beginTask(getDescription(), extensionPoints.length);
            for (int i = 0; i < extensionPoints.length; i++) {
                IExtensionPoint point = registry.getExtensionPoint(extensionPoints[i]);
                IConfigurationElement configElements[] = point.getConfigurationElements(); 
                
                for (int j = 0; j < configElements.length; j++) {
                    if(checkConfigurationElementForReference(configElements[i], fqtn)) {
                        return true;
                    }
                }
                monitor.worked(1);
            }
            return false;
        } finally {
            monitor.done();
        }
    }

    private boolean checkConfigurationElementForReference(IConfigurationElement config, String fqtn) {
        String attributeNames[] = config.getAttributeNames();
        for (int index = 0; index < attributeNames.length; index++) {
            String value = config.getAttribute(attributeNames[index]);
            if (value != null && fqtn.equals(value)) {
                return true;
            }
        }
        IConfigurationElement children[] = config.getChildren();
        for (int index = 0; index < children.length; index++) {
            if(checkConfigurationElementForReference(children[index], fqtn)) {
                return true;
            }
        }
        return false;
    }
}
