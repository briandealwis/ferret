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
package ca.ubc.cs.ferret.tptp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.hyades.models.hierarchy.TRCMonitor;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.trace.internal.ui.PDContentProvider;
import org.eclipse.hyades.trace.internal.ui.PDLabelProvider;
import org.eclipse.hyades.trace.internal.ui.PDProjectExplorer;
import org.eclipse.hyades.trace.views.internal.FilteringUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.tptp.ui.ContentProviderWrapper;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class TptpSphereWizardPage extends WizardPage {
	protected TptpSphereFactory factory;
	protected CheckboxTreeViewer treeViewer;
	protected IAction launchImportWizard;
	protected Combo fidelityCombo;
	
	public TptpSphereWizardPage(TptpSphereFactory f) {
		super(f.getId());
		factory = f;
	}
	
	public void createControl(final Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);		
		setControl(control);
		setTitle("TPTP Profile Selection");
		setMessage("Select TPTP binding profiles(s) to be used " +
		"as the source of dynamic run-time information.  ");
		if(getMonitors() == null) {
			setErrorMessage("You have no binding profiles loaded.");
//					"No TPTP profiles loaded: must either capture a profile using TPTP " +
//					"monitoring tools or load / import a profile using the Profiling Monitor.",
		}

		GridLayout layout = new GridLayout();
		control.setLayout(layout);

		treeViewer =  new CheckboxTreeViewer(control, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setUseHashlookup(true);
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		treeViewer.setContentProvider(new ContentProviderWrapper(new PDContentProvider(new PDProjectExplorer())));
		treeViewer.setLabelProvider(new PDLabelProvider());
		treeViewer.setInput(getMonitors());
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {		
				configureFactory();
			}
		});
//		treeViewer.expandAll();
		treeViewer.setCheckedElements(factory.getSources());
//		for(EObject source : factory.getSources()) {
//			treeViewer.expandToLevel(source, 0);
//			treeViewer.setChecked(source, true);
//		}
		
		fidelityCombo = new Combo(control, SWT.DROP_DOWN);
		for(Fidelity value : Fidelity.values()) {
			fidelityCombo.add(value.toString());
		}
		fidelityCombo.select(ArrayUtils.indexOf(Fidelity.values(), 
				factory.fidelity != null ? factory.fidelity : Fidelity.Approximate));
		fidelityCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				configureFactory();
			}});
		
		Link launchButton = new Link(control, SWT.NONE);
		launchButton.setText("Launch <A HREF=\"tptpWizard\">TPTP Profile Import Wizard</A>");
		launchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				treeViewer.expandAll();
				final Set<Object> initiallyAvailable = getAllChildren(treeViewer);
				launchTraceImportWizard(getShell());
				// poor man's continuation!  need to sleep to give the import a chance to fetch
				getShell().getDisplay().timerExec(1000, new Runnable() {
					public void run() {
						// check that we're still valid -- the user may have cancelled the wizard
						if(treeViewer.getControl() == null || treeViewer.getControl().isDisposed()) { return; }
						treeViewer.setInput(getMonitors());
						treeViewer.expandAll();
						Set<Object> newlyAvailable = getAllChildren(treeViewer);
						Collection<Object> added = CollectionUtils.subtract(newlyAvailable, initiallyAvailable);
						for(Object add : added) {
							treeViewer.setChecked(add, true);
						}
						// setCheckedElements doesn't trigger any state-checked listeners
						configureFactory();
					}});
			}});
		
		configureFactory();
	}

	protected Set<Object> getAllChildren(CheckboxTreeViewer tv) {
		Set<Object> leaves = new HashSet<Object>();
		Set<Object> seen = new HashSet<Object>();
		LinkedList<Object> remaining = new LinkedList<Object>();
		ITreeContentProvider cp = (ITreeContentProvider)tv.getContentProvider();
		if(tv.getInput() instanceof Collection<?>) {
			remaining.addAll((Collection<?>)tv.getInput());
		} else if(tv.getInput() instanceof Object[]) {
			Collections.addAll(remaining, (Object[])tv.getInput());
		} else {
			remaining.add(tv.getInput());
		}
		while(!remaining.isEmpty()) {
			Object parent = remaining.remove();
			Object kiddies[] = cp.getChildren(parent);
			if(kiddies == null || kiddies.length == 0) {
				leaves.add(parent);
			} else {
				for(Object child : kiddies) {
					if(!seen.contains(child)) {
						seen.add(child);
						remaining.add(child);
					}
				}
			}
		}
		return leaves;
	}

	protected Object getMonitors() {
//		PDProjectExplorer xp = new PDProjectExplorer();
//		PDContentProvider cp = new PDContentProvider(xp);
		List<TRCMonitor> monitors = new ArrayList<TRCMonitor>();
		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			for(Object o : (List<?>)PDContentProvider.getMonitors(project)) {
				if(o instanceof TRCMonitor) {
					monitors.add((TRCMonitor)o);
				}
			}
		}
//		cp.dispose();
//		xp.dispose();
		return monitors;
//		if(PDContentProvider.getMonitors() == null || PDContentProvider.getMonitors().isEmpty()) {
//			return null;
//		}
//		return PDContentProvider.getMonitors().get(0);
	}

	protected void configureFactory() {
		setErrorMessage(null);
		Object checked[] = treeViewer.getCheckedElements();
		Collection<EObject> appropriate = new ArrayList<EObject>();
		for(int i = 0; i < checked.length; i++) {
			if(checked[i] instanceof EObject) {
				appropriate.add((EObject)checked[i]);
			}
		}
		EObject sources[] = appropriate.toArray(new EObject[appropriate.size()]);
		if(sources.length > 0 && !hasGraphicalFlow(sources)) {
			setErrorMessage("One or more of the selected binding profile(s) are missing "
					+ "the required execution flow graphical detail.  This is usually due to misconfiguration when importing a profile.");
			setPageComplete(false);
			return;
		}
		factory.setSources(sources);
		factory.setFidelity(Fidelity.values()[fidelityCombo.getSelectionIndex()]);
		IStatus result = factory.canCreate();
		if(result.isOK()) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
//			setErrorMessage(result.getMessage());	commented out: too distracting
		}
	}

	protected boolean hasGraphicalFlow(EObject[] sources) {
		EObject processes[] = TptpSphereFactory.getProcesses(sources);
		for(Object o : FilteringUtil.getFilteredMethods(null, processes)) {
			if(!(o instanceof TRCMethod)) { continue; }
			TRCMethod m = (TRCMethod)o;
			if(!m.getInvocations().isEmpty()) { 
				return true;
			}
		}
		return false;
	}


	protected IStatus launchTraceImportWizard(Shell shell) {
        IExtensionPoint point = RegistryFactory.getRegistry().getExtensionPoint("org.eclipse.ui.importWizards");
        MultiStatus errorStatus = new MultiStatus(TptpPlugin.pluginID, 0,
        		"Unable to find TPTP ImportTraceXMLWizard", null);
        for(IExtension extension : point.getExtensions()) {
            for(IConfigurationElement element : extension.getConfigurationElements()) {
                if(element.getName().equals("wizard") && "ImportTraceXMLWizard".equals(element.getAttribute("id"))) {
					try {
						IWizard wizard = (IWizard)element.createExecutableExtension("class");
						if(wizard instanceof IWorkbenchWizard) {
							((IWorkbenchWizard)wizard).init(TptpPlugin.getDefault().getWorkbench(), new StructuredSelection());
						}
						WizardDialog dialog = new WizardDialog(getShell(), wizard);
						dialog.open();
	                	return Status.OK_STATUS;
					} catch (CoreException e) {
						IStatus s = new Status(IStatus.WARNING,
								TptpPlugin.pluginID, -1,
								"Problems instantiating class for " + element.getNamespaceIdentifier() + "/" + element.getName(), e);
						errorStatus.add(s);
						FerretPlugin.log(s);
					} catch(ClassCastException e) {
						IStatus s = new Status(IStatus.WARNING,
								TptpPlugin.pluginID, -1,
								"Expected instantiated class to be IWizardPage for " + element.getNamespaceIdentifier() + "/" + element.getName(), e);
						errorStatus.add(s);
						FerretPlugin.log(s);
					}
                }
            }
        }
		ErrorDialog.openError(shell, "Unable to launch import wizard",
				"Unable to launch TPTP profile import wizard", errorStatus);
		return errorStatus;
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp("ca.ubc.cs.ferret.tptp.importing");
	}
}
