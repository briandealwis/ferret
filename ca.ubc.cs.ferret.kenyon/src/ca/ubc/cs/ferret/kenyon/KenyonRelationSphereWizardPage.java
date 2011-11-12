package ca.ubc.cs.ferret.kenyon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.progress.IProgressConstants;

import ca.ubc.cs.ferret.ui.Association;
import ca.ubc.cs.ferret.ui.ListModel;
import ca.ubc.cs.ferret.ui.ListModelContentProvider;
import ca.ubc.cs.ferret.ui.WorkbenchAdapterLabelProvider;
import edu.se.evolution.kenyon.KenyonActivator;
import edu.se.evolution.kenyon.Project;

public class KenyonRelationSphereWizardPage extends WizardPage {
	protected KenyonSphereFactory factory;

	protected ListModel<Project> kenyonProjects = new ListModel<Project>();
	protected ListModel<IProject> eclipseProjects = new ListModel<IProject>();
	protected ListModel<Association<IProject,Project>> associatedProjects = 
		new ListModel<Association<IProject,Project>>();

	protected ListViewer eclipseProjectsViewer;
	protected ListViewer kenyonProjectsViewer;
	protected ListViewer associationsViewer;
	protected Button refreshListButton;
	
	public KenyonRelationSphereWizardPage(KenyonSphereFactory f) {
		super(f.getId());
		factory = f;
		associatedProjects.setElements(factory.getAssociations());
	}
	
	public void handleEvent() {
		factory.setAssociations(associatedProjects.getElements());
		IStatus result = factory.canCreate();
		if(result.isOK()) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
//			setErrorMessage(result.getMessage());	commented out: too distracting
		}
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);		
		setControl(composite);
		setTitle("Kenyon Project Mappings");
		setMessage("Select the appropriate project bindings.");

		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		
		Label instructions = new Label(composite, SWT.WRAP | SWT.READ_ONLY);
		GridData instructionsData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		instructionsData.widthHint = 300;
		instructionsData.minimumWidth = 100;
		instructions.setLayoutData(instructionsData);;
		instructions.setText("Configure kenyon project mappings");
		
		Composite control = new Composite(composite, SWT.NONE);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		control.setLayout(new GridLayout(4, false));
		
		Composite eclipseProjectsPanel = new Composite(control, SWT.NONE);
		Composite kenyonProjectsPanel = new Composite(control, SWT.NONE);
		Composite selectionButtonsPanel = new Composite(control, SWT.NONE);
		Composite associationsPanel = new Composite(control, SWT.NONE);
		eclipseProjectsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		kenyonProjectsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		associationsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		eclipseProjectsPanel.setLayout(new GridLayout(1, false));
		Label eclipseProjectsLabel = new Label(eclipseProjectsPanel, SWT.CENTER | SWT.READ_ONLY);
		eclipseProjectsLabel.setText("Eclipse Projects");
		eclipseProjectsLabel.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		eclipseProjectsViewer = new ListViewer(eclipseProjectsPanel,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		eclipseProjectsViewer.setComparator(new ViewerComparator());
		eclipseProjectsViewer.setContentProvider(new ListModelContentProvider<IProject>());
		eclipseProjectsViewer.setLabelProvider(new WorkbenchLabelProvider());
		eclipseProjectsViewer.setInput(eclipseProjects);
		{
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.minimumHeight = 100;
			gd.minimumWidth = 10;
			gd.widthHint = 160;
			gd.heightHint = 160;
			eclipseProjectsViewer.getControl().setLayoutData(gd);
		}

		kenyonProjectsPanel.setLayout(new GridLayout(1, false));
		Label kenyonProjectsLabel = new Label(kenyonProjectsPanel, SWT.CENTER | SWT.READ_ONLY);
		kenyonProjectsLabel.setText("Kenyon Projects");
		kenyonProjectsLabel.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		kenyonProjectsViewer = new ListViewer(kenyonProjectsPanel,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		kenyonProjectsViewer.setComparator(new ViewerComparator());
		kenyonProjectsViewer.setContentProvider(new ListModelContentProvider<Project>());
		kenyonProjectsViewer.setLabelProvider(new WorkbenchAdapterLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Project) {
					return ((Project)element).getProjectName();
				}
				return super.getText(element);
			}
			
		});
		kenyonProjectsViewer.setInput(kenyonProjects);
		{
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.minimumHeight = 100;
			gd.minimumWidth = 10;
			gd.widthHint = 160;
			gd.heightHint = 160;
			kenyonProjectsViewer.getControl().setLayoutData(gd);
		}

		associationsPanel.setLayout(new GridLayout(1, false));
		Label associationsLabel = new Label(associationsPanel, SWT.CENTER | SWT.READ_ONLY);
		associationsLabel.setText("Associated Projects");
		associationsLabel.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		associationsViewer = new ListViewer(associationsPanel,
				SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		associationsViewer.setComparator(new ViewerComparator());
		associationsViewer.setContentProvider(new ListModelContentProvider<Association<IProject,Project>>());
		associationsViewer.setLabelProvider(new WorkbenchAdapterLabelProvider()  {
			@Override
			public String getText(Object element) {
				if(element instanceof Association) {
					Association<IProject,Project> assoc = (Association<IProject,Project>)element;
					return assoc.getFrom().getName() + " -- " + assoc.getTo().getProjectName();
				}
				return super.getText(element);
			}
			
		});
		associationsViewer.setInput(associatedProjects);
		{
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.minimumHeight = 100;
			gd.minimumWidth = 10;
			gd.widthHint = 160;
			gd.heightHint = 160;
			associationsViewer.getControl().setLayoutData(gd);
		}
		associationsViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection sel = event.getSelection();
				Object objs[] = getSelectedObjects(sel);
				breakProjectAssociation(objs);
				handleEvent();
			}});
		
		selectionButtonsPanel.setLayout(new GridLayout());
		Composite selectionButtons = new Composite(selectionButtonsPanel, SWT.NONE);
		selectionButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER| GridData.GRAB_VERTICAL));
		{
			RowLayout l = new RowLayout(SWT.VERTICAL);
			l.fill = true;
			selectionButtons.setLayout(l);
		}
		final Button pairProjectsButton = new Button(selectionButtons, SWT.PUSH);
		final Button unpairProjectsButton = new Button(selectionButtons, SWT.PUSH);
		final Button unpairAllProjectsButton = new Button(selectionButtons, SWT.PUSH);
		pairProjectsButton.setText("->");
		pairProjectsButton.setToolTipText("Pair currently selected projects");
		unpairProjectsButton.setText("<-");
		unpairProjectsButton.setToolTipText("Unpair currently selected project pairs");
		unpairAllProjectsButton.setText("<<");
		unpairAllProjectsButton.setToolTipText("Remove all pairings");

		pairProjectsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetDefaultSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				Project kp = (Project)getSingleSelectedObject(kenyonProjectsViewer.getSelection());
				IProject ep = (IProject)getSingleSelectedObject(eclipseProjectsViewer.getSelection());
				if(kp == null || ep == null) { return; }
				pairProjectAssociation(kp, ep);
				handleEvent();
			}	
		});
		unpairProjectsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Object[] assocs = getSelectedObjects(associationsViewer);
				breakProjectAssociation(assocs);
				handleEvent();
			}	
		});
		unpairAllProjectsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				breakProjectAssociation(associatedProjects.getElements().toArray());
				handleEvent();
			}
		});
		
		ISelectionChangedListener singleProjectsSelectionListener = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				pairProjectsButton.setEnabled(
						((IStructuredSelection)eclipseProjectsViewer.getSelection()).size() == 1
					&& ((IStructuredSelection)kenyonProjectsViewer.getSelection()).size() == 1);
			}			
		};
		eclipseProjectsViewer.addSelectionChangedListener(singleProjectsSelectionListener);
		kenyonProjectsViewer.addSelectionChangedListener(singleProjectsSelectionListener);
		associationsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				unpairProjectsButton.setEnabled(!associationsViewer.getSelection().isEmpty());
				unpairAllProjectsButton.setEnabled(!associatedProjects.isEmpty());
			}});
		
		Composite refreshPanel = new Composite(composite, SWT.NONE);
//		GridData refreshPanelData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
//		refreshPanelData.heightHint = 15;
//		refreshPanelData.minimumHeight = 10;
//		refreshPanel.setLayoutData(refreshPanelData);;
		refreshPanel.setLayout(new GridLayout());
		refreshListButton = new Button(refreshPanel, SWT.PUSH);
		refreshListButton.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		refreshListButton.setText("Refresh Lists");
		refreshListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshLists();
			}
		});

//		refreshLists();	done in setVisible() instead
	}

	protected void breakProjectAssociation(Object[] associations) {
		for(Object obj : associations) {
			if(obj instanceof Association) {
				Association<IProject,Project> assoc = (Association<IProject,Project>)obj; 
				associatedProjects.remove(assoc);
//				kenyonProjects.add(assoc.getTo());
				eclipseProjects.add(assoc.getFrom());
//				factory.unmap(assoc.getFrom(), assoc.getTo());
			}
		}
	}

	protected void pairProjectAssociation(Project kp, IProject ep) {
		Association<IProject,Project> assoc = new Association<IProject,Project>(ep, kp); 
		associatedProjects.add(assoc);
//		kenyonProjects.remove(kp);
		eclipseProjects.remove(ep);
//		factory.map(kp, ep);
	}

	protected boolean refreshLists() {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				refreshListButton.setText("Refreshing...");
				refreshListButton.setEnabled(false);
			}});
        Job j = new Job("Retrieving project lists") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IStatus result;
				try {
					Set<Project> kps = new HashSet<Project>(Project.retrieveProjects(KenyonSphereHelper.getDefault().getSession()));
					Set<IProject> eps = new HashSet<IProject>();
					for(IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
						eps.add(p);
					}
//					ArrayList<Association<IProject,Project>> associations = new ArrayList<Association<IProject,Project>>();
//					Map<IProject,Project> factoryMappings = factory.getMappings();
//					for(IProject ep : factoryMappings.keySet()) {
//						eps.remove(ep);
//						kps.remove(factoryMappings.get(ep));
//						associations.add(new Association<IProject,Project>(ep, factoryMappings.get(ep)));
//					}
					for(Association<IProject, Project> a : factory.getAssociations()) {
						eps.remove(a.getFrom());	
					}
					kenyonProjects.setElements(new ArrayList<Project>(kps));
					eclipseProjects.setElements(new ArrayList<IProject>(eps));
					associatedProjects.setElements(factory.getAssociations());
					result = Status.OK_STATUS;
				} catch(final Throwable e) {
					if(getShell() != null && getShell().getDisplay() != null) {
						getShell().getDisplay().asyncExec(new Runnable() {
							public void run() {
								setErrorMessage("An error occurred when retrieving project lists from Kenyon; see log for details");
							}});
					}
					result = new Status(IStatus.ERROR, KenyonActivator.PLUGIN_ID, 
							"Exception retrieving projects lists", e);
				}
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						refreshListButton.setText("Refresh List.");
						refreshListButton.setEnabled(true);
					}});
				return result;
			}
        };
        j.setPriority(Job.INTERACTIVE);
        j.setUser(true);
        j.setProperty(IProgressConstants.NO_IMMEDIATE_ERROR_PROMPT_PROPERTY, Boolean.TRUE);
        j.schedule();
        return true;
	}

	protected Object[] getSelectedObjects(ISelectionProvider provider) {
		return getSelectedObjects(provider.getSelection());
	}
	
	protected Object[] getSelectedObjects(ISelection sel) {
		if(sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
			return new Object[0];
		}
		return ((IStructuredSelection)sel).toArray();
	}

	protected Object getSingleSelectedObject(ISelection sel) {
		if(sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
			return null;
		}
		if(((IStructuredSelection)sel).size() != 1) { return null; }
		return ((IStructuredSelection)sel).getFirstElement();
	}

	@Override
	public void setVisible(boolean visible) {
		if(visible) { refreshLists(); }
		super.setVisible(visible);
	}


}
