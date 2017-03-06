package ca.ubc.cs.ferret.sphereconfig;

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphereCompositorFactory;
import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.model.SphereHelper;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class PickSpheresWizardPage extends WizardPage {
	public static final String pageId = "Pick Spheres";

	protected List<ISphereFactory> inputRoot = new ArrayList<ISphereFactory>();
	
	protected IOPFLabelProvider labelProvider;
	protected TreeViewer configurationViewer;
	protected ITreeContentProvider contentProvider;

	protected Button moveUpButton;
	protected Button moveDownButton;
	protected Button removeButton;
	protected Button clearAllButton;
	
	protected Composite compositionFunctionsPanel;
	protected Composite sphereFactoriesPanel;
	
	protected List<Button> compositionFunctionButtons;
	protected List<Label> compositionFunctionLabels;
	protected List<Button> sphereFactoryButtons;
	protected List<Label> sphereFactoryLabels;
	
	public static final String ATTR_CLASS = "class";

	protected PickSpheresWizardPage() {
		super(pageId);
	}

	@Override
	public IWizardPage getNextPage() {
		getWizard().addPages();
		saveDialogSettings();
		return super.getNextPage();
	}

	protected void addSphereFactory(final ISphereFactory f) {
		final ISphereCompositorFactory compositor = getDestinationCompositor();
		ISphereFactory root = getRoot();
		if(compositor == null && root != null) {
			return;
		}
		if(!showFactoryConfigurationDialog(f, false)) {
			return;
		}
		if(root == null) {
			setRoot(f);
			refresh();
		} else {
			Preconditions.checkNotNull(compositor);
			compositor.add(f);
			refresh(compositor);
		}
		handleConfigurationSelection(configurationViewer.getSelection());
	}
	
	/**
	 * Pop up the factory configuration dialog, if any.
	 * @return true if the factory was successfully configured, false
	 */ 
	protected boolean showFactoryConfigurationDialog(ISphereFactory f, boolean explicitlyRequested) {
		IWizardPage p = (IWizardPage)f.getAdapter(IWizardPage.class);
		if(p != null) {
			if(FerretPlugin.hasDebugOption("debug/showConfigurationDetails")) {
				System.out.println("Provoking sphere factory wizard page for " + f.getId());
			}
			WizardDialog dialog = new WizardDialog(getShell(), new IndividualConfigurationWizard(p));
			dialog.setHelpAvailable(true);
			int result = dialog.open();
			if(result == WizardDialog.CANCEL) { return false; }
		} else if(explicitlyRequested){
			// if explicitly requested then inform the user there was no configuration dialog
			MessageDialog.openInformation(getShell(), f.getDescription(),
					"This factory has no configuration dialog.");
		}
		return true;
	}

	protected void addSphereComposition(ISphereCompositorFactory c) {
		ISphereCompositorFactory compositor = getDestinationCompositor();
		ISphereFactory root = getRoot();
		if (compositor == null && root != null) {
			return;
		}
		if (root == null) {
			setRoot(c);
			refresh();
		} else {
			Preconditions.checkNotNull(compositor);
			compositor.add(c);
			refresh(compositor);
		}
		select(c);
	}
	
	protected ISphereCompositorFactory getDestinationCompositor() {
		ISelection selection = configurationViewer.getSelection();
		if(!(selection instanceof IStructuredSelection)) { return null; }
		if(((IStructuredSelection)selection).size() != 1) { return null; }
		Object o = ((IStructuredSelection)selection).getFirstElement();
		if(o instanceof ISphereCompositorFactory) {
			return (ISphereCompositorFactory)o;
		}
		return null;
	}

	protected void saveDialogSettings() {
//		if(sphereFactories == null) { return; }
//		List<String> selected = new LinkedList<String>();
//		for(ISphereFactory f : sphereFactories.getSelected()) {
//			selected.add(f.getId());
//		}
//		getDialogSettings().put(DS_SELECTED_FACTORIES,
//				selected.toArray(new String[selected.size()]));
	}

	public void createControl(Composite parent) {
		// sphereFactories.addListener(this);
		setDescription("Configure Ferret sphereHelpers");

//		String ids[] = getDialogSettings().getArray(DS_SELECTED_FACTORIES);
//		if(ids != null) {
//			for(String id : ids) {
//				sphereFactories.select(id);
//			}
//		}
		Composite control = new Composite(parent, SWT.NONE);
		setControl(control);
		control.setLayout(new GridLayout(1, false));
		
		labelProvider = new IOPFLabelProvider();

		Label instructions = new Label(control, SWT.WRAP | SWT.READ_ONLY);
		GridData instructionsData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		instructionsData.widthHint = 300;
		instructionsData.minimumWidth = 100;
		instructions.setLayoutData(instructionsData);;
		instructions.setText(
				"Ferret must be configured with a set of sphereHelpers. " +
				"These sphereHelpers provide suites of low-level queries used by Ferret's " +
				"conceptual queries.  Spheres may be composed using the composition functions; " +
				"these functions have different composition semantics, and the " +
				"sphere order may matter (and may be adjusted using the Move Up and Move Down buttons).");
		
		control = new Composite(control, SWT.NONE);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		control.setLayout(new GridLayout(2, false));

		// This goes on the left side
		createConfigurationViewControls(new Composite(control, SWT.NONE));
		
		Composite selectionControlsPanel = new Composite(control, SWT.NONE);
		selectionControlsPanel.setLayout(new GridLayout(1, false));
		selectionControlsPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		createSphereFactoriesPanel(new Composite(selectionControlsPanel, SWT.NONE));
		createCompositingButtonsPanel(new Composite(selectionControlsPanel, SWT.NONE));
		
		
		//		tableViewer.setSorter(new ViewerSorter() {
//			@Override
//			public int compare(Viewer viewer, Object e1, Object e2) {
//				return ((ConfigurationItem)e1).compareTo((ConfigurationItem)e2);
//			}});
//		tableViewer.addCheckStateListener(new ICheckStateListener() {
//			public void checkStateChanged(CheckStateChangedEvent event) {
//				handleCheckedEvent((ConfigurationItem)event.getElement(), event.getChecked());
//				tableViewer.refresh();
//				setPageComplete(validatePage());
//				getWizard().addPages();
//			}});

		configurationViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleConfigurationSelection(event.getSelection());
			}});

		configurationViewer.setInput(inputRoot);
		configurationViewer.setSelection(getRoot() == null ? new StructuredSelection()
			: new StructuredSelection(getRoot()));
	}

	protected void handleConfigurationSelection(ISelection selection) {
		if(getRoot() == null) {
			// if we don't have a root, then allow the operations, but no movement operations
			for(Button b : compositionFunctionButtons) { b.setEnabled(true); }
			for(Label l : compositionFunctionLabels) { l.setEnabled(true); }
			for(Button b : sphereFactoryButtons) { b.setEnabled(true); }
			for(Label l : sphereFactoryLabels) { l.setEnabled(true); }
			moveUpButton.setEnabled(false);
			moveDownButton.setEnabled(false);
			removeButton.setEnabled(false);
			handleEvent();
			return;
		}

		Object selected[] = getSelectedObjects(selection);
		removeButton.setEnabled(selected.length > 0);
		
		boolean single = selected.length == 1;
		boolean someSelected = selected.length > 0;
		boolean areCompositors = true;
		boolean notRoot = true;
		for(Object o : selected) { 
			areCompositors = areCompositors && o instanceof ISphereCompositorFactory;
			notRoot = notRoot && !o.equals(getRoot());
		}
		
		for(Button b : compositionFunctionButtons) { b.setEnabled(single && areCompositors); }
		for(Label l : compositionFunctionLabels) { l.setEnabled(single && areCompositors); }
		for(Button b : sphereFactoryButtons) { b.setEnabled(single && areCompositors); }
		for(Label l : sphereFactoryLabels) { l.setEnabled(single && areCompositors); }
		moveUpButton.setEnabled(someSelected && notRoot);
		moveDownButton.setEnabled(someSelected && notRoot);
		handleEvent();		
	}

	protected void createCompositingButtonsPanel(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Group compositingButtonsPanel = new Group(parent, SWT.SHADOW_ETCHED_IN);
		compositingButtonsPanel.setText("Composition functions:");
		compositingButtonsPanel.setLayout(new GridLayout(2, false));
		compositingButtonsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//		 compositingButtonsPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER| GridData.GRAB_VERTICAL));

		compositionFunctionButtons = new ArrayList<Button>();
		compositionFunctionLabels = new ArrayList<Label>();
		
        for(final IConfigurationElement element : 
    			RegistryFactory.getRegistry().getConfigurationElementsFor(FerretPlugin.pluginID, FerretPlugin.scfsExtensionPointId)) {
    		ISphereCompositorFactory cf;
        	try {
        		cf = (ISphereCompositorFactory)element.createExecutableExtension(ATTR_CLASS);
        	} catch(CoreException e) {
        		FerretPlugin.log(e);
        		continue;
        	}
        	final String description = cf.getDescription();
        	Button tcfButton = new Button(compositingButtonsPanel, SWT.CENTER | SWT.PUSH);
        	compositionFunctionButtons.add(tcfButton);
        	tcfButton.setImage(labelProvider.getImage(cf.getImageDescriptor()));
        	Label tcfLabel = new Label(compositingButtonsPanel, SWT.LEFT | SWT.WRAP | SWT.READ_ONLY);
        	tcfLabel.setText(description);
    		tcfLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        	compositionFunctionLabels.add(tcfLabel);
        	if(cf.getHelpContextId() != null) {
        		FerretPlugin.getDefault().setHelp(tcfButton, cf.getHelpContextId());
        		FerretPlugin.getDefault().setHelp(tcfLabel, cf.getHelpContextId());
        	}
		
        	tcfButton.addSelectionListener(new SelectionAdapter() {
        		public void widgetSelected(SelectionEvent e) {
        			try {
	        			Object cf = element.createExecutableExtension(ATTR_CLASS);
	        			if(cf instanceof ISphereCompositorFactory) {
	        				addSphereComposition((ISphereCompositorFactory)cf);
	        				handleEvent();
	        			} else {
	        				ErrorDialog.openError(getShell(), "Configuration error",
	        						"There has been a configuration error for plug-in " + element.getNamespaceIdentifier(),
	        						new Status(IStatus.ERROR, element.getNamespaceIdentifier(),
	        								FerretErrorConstants.CONFIGURATION_ERROR,
	        								"The implementing class for the composition function '" + description + 
	        									"' is not an instance of " + ISphereCompositorFactory.class.getName(),
	        									null));		
	        			}
        			} catch(CoreException e2) {
        				ErrorDialog.openError(getShell(), "Configuration error",
        						"There has been a configuration error for plug-in " + element.getNamespaceIdentifier(),
        						new Status(IStatus.ERROR, element.getNamespaceIdentifier(),
        								FerretErrorConstants.CONFIGURATION_ERROR,
        								"An error occurred while instantiating the class for the composition function '" + description,
        								e2));
        			}
        		}	
        	});
        }
	}

	protected void createSphereFactoriesPanel(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Group factoriesButtonsPanel = new Group(parent, SWT.SHADOW_ETCHED_IN);
		factoriesButtonsPanel.setText("Available sphere types:");
		factoriesButtonsPanel.setLayout(new GridLayout(2, false));
		// factoriesButtonsPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER| GridData.GRAB_VERTICAL));
		factoriesButtonsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		sphereFactoryButtons = new ArrayList<Button>();
		sphereFactoryLabels = new ArrayList<Label>();
		
		for (SphereHelper s : FerretPlugin.getSphereHelpers()) {
			for (final ISphereFactory f : s.getSphereFactories()) {
				Button button = new Button(factoriesButtonsPanel, SWT.CENTER | SWT.PUSH);
				sphereFactoryButtons.add(button);
	        	button.setImage(labelProvider.getImage(f.getImageDescriptor()));
				Label label = new Label(factoriesButtonsPanel, SWT.LEFT | SWT.WRAP | SWT.READ_ONLY);
				label.setText(f.getDescription());
				label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
				sphereFactoryLabels.add(label);

	        	if(f.getHelpContextId() != null) {
	        		FerretPlugin.getDefault().setHelp(button, f.getHelpContextId());
	        		FerretPlugin.getDefault().setHelp(label, f.getHelpContextId());
	        	}
	        	button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						addSphereFactory(f.clone());
						handleEvent();
					}
				});
			}
		}		
	}

	protected void createConfigurationViewControls(Composite configurationPanel) {
		configurationPanel.setLayout(new GridLayout(1, false));
		configurationPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		configurationViewer = new TreeViewer(configurationPanel,
				SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		configurationViewer.setContentProvider(contentProvider = new SphereNetworkContentProvider());
		configurationViewer.setLabelProvider(labelProvider);
		configurationViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		{
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.minimumHeight = 100;
			gd.minimumWidth = 10;
			gd.widthHint = 160;
			gd.heightHint = 160;
			configurationViewer.getControl().setLayoutData(gd);
		}
		configurationViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection sel = event.getSelection();
				Object objs[] = getSelectedObjects(sel);
				if(objs.length == 1 && objs[0] instanceof ISphereFactory) {
					showFactoryConfigurationDialog((ISphereFactory)objs[0], true);
				}
				handleEvent();
			}});		

		Composite movementButtonsPanel = new Composite(configurationPanel, SWT.NONE);
		movementButtonsPanel.setLayout(new GridLayout());
		Composite movementButtons = new Composite(movementButtonsPanel, SWT.NONE);
//		movementButtons.setLayoutData(
//				new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL));
		{
			RowLayout l = new RowLayout(SWT.HORIZONTAL);
			l.fill = true;
			movementButtons.setLayout(l);
		}

		moveUpButton = new Button(movementButtons, SWT.ARROW | SWT.UP );
		moveUpButton.setEnabled(false);
		moveUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedSpheresUp();
				handleEvent();
			}	
		});
		moveDownButton = new Button(movementButtons, SWT.ARROW | SWT.DOWN);
		moveDownButton.setEnabled(false);
		moveDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedSpheresDown();
				handleEvent();
			}	
		});

		removeButton = new Button(movementButtons, SWT.CENTER | SWT.PUSH);
		removeButton.setToolTipText("Remove");
		removeButton.setImage(labelProvider.getImage(
				FerretPlugin.getImageDescriptor("icons/delete_obj.gif")));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelectedSpheres();
				handleEvent();
			}	
		});
		// sure would be nice to bind this to ActionFactory.DELETE?
		configurationViewer.getControl().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                    if (event.character == SWT.DEL && 
                            event.stateMask == 0 && 
                            removeButton.isEnabled()) {
        				removeSelectedSpheres();
        				handleEvent();
                    }}});

		clearAllButton = new Button(movementButtons, SWT.CENTER | SWT.PUSH);
		clearAllButton.setToolTipText("Clear All");
		clearAllButton.setImage(labelProvider.getImage(
				FerretPlugin.getImageDescriptor("icons/clear_co.gif")));
		clearAllButton.setEnabled(true);
		clearAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAllSpheres();
				handleEvent();
			}	
		});

	}

	protected ISphereCompositorFactory getParentCompositor(ISphereFactory tb) {
		Object parent = getParent(tb);
		if(parent instanceof ISphereCompositorFactory) {
			return (ISphereCompositorFactory)parent;
		}
		return null;
	}
	
	protected Object getParent(Object child) {
		for(TreePath tp : configurationViewer.getExpandedTreePaths()) {
			Object leaf = tp.getSegment(tp.getSegmentCount() - 1);
			if(contains(contentProvider.getChildren(leaf), child)) {
				return leaf;
			}
			// we use `i > 0' as the root doesn't have a parent
			for(int i = tp.getSegmentCount() - 1; i > 0; i--) {
				if(tp.getSegment(i).equals(child)) {
					return tp.getSegment(i - 1);
				}
			}
		}
		return null;
	}

	private static boolean contains(Object[] arr, Object sought) {
		for(Object o : arr) {
			if(o.equals(sought)) {
				return true;
			}
		}
		return false;
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
	
	protected void moveSelectedSpheresUp() {
		final Set<Object> reqRefreshing = new HashSet<Object>();
		for(ISphereFactory tb : getSelectedSpheres()) {
			ISphereCompositorFactory parent = getParentCompositor(tb);
			if(parent != null) {
				parent.moveSphereUp(tb);
				reqRefreshing.add(parent);
			}
		}
		refresh(reqRefreshing);
	}
	
	protected void moveSelectedSpheresDown() {
		final Set<Object> reqRefreshing = new HashSet<Object>();
		for(ISphereFactory tb : getSelectedSpheres()) {
			ISphereCompositorFactory parent = getParentCompositor(tb);
			if(parent != null) {
				parent.moveSphereDown(tb);
				reqRefreshing.add(parent);
			}
		}
		refresh(reqRefreshing);
	}
	
	protected void removeSelectedSpheres() {
		final Set<Object> reqRefreshing = new HashSet<Object>();
		for(ISphereFactory tb : getSelectedSpheres()) {
			ISphereCompositorFactory parent = getParentCompositor(tb);
			if(parent != null) {
				parent.remove(tb);
				reqRefreshing.add(parent);
			} else if(tb.equals(getRoot())) {
				setRoot(null);
				reqRefreshing.add(inputRoot);
			}
		}
		refresh(reqRefreshing);
	}

	protected void clearAllSpheres() {
		setRoot(null);
		handleConfigurationSelection(new StructuredSelection());
		refresh();
	}

	protected Collection<ISphereFactory> getSelectedSpheres() {
		Object[] selected = getSelectedObjects(configurationViewer);
		List<ISphereFactory> tbs = new ArrayList<ISphereFactory>(selected.length);
		for(Object o : selected) {
			if(o instanceof ISphereFactory) {
				tbs.add((ISphereFactory)o);
			}
		}
		return tbs;
	}

	public void handleEvent() {
		if(validatePage()) {
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	protected boolean validatePage() {
		if(getRoot() == null) {
			// unnecessary: setErrorMessage("No spheres configured");
			return false;
		}
		IStatus status = getRoot().canCreate();
		if(!status.isOK()) {
			IStatus detail = null;
			Collection<ISphereFactory> sel = getSelectedSpheres();
			if(!sel.isEmpty()) {
				for(ISphereFactory tf : sel) {
					detail = tf.canCreate();
					if(!detail.isOK()) {
						while(detail.isMultiStatus()) {
							for(IStatus s : ((MultiStatus)detail).getChildren()) {
								if(!s.isOK()) { detail = s; break; }
							}
						}
						break;
					}
				}
			}
			if(detail == null || detail.isOK()) {
				detail = status;
				while(detail.isMultiStatus()) {
					for(IStatus s : ((MultiStatus)detail).getChildren()) {
						if(!s.isOK()) { detail = s; break; }
					}
				}
			}
			setErrorMessage(status.getMessage()
					+ (detail == null || detail == status? "" : ": " + detail.getMessage()));
		}
		return status.isOK();
	}

	protected void refresh(final Collection<?> modelObjects) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				for(Object o : modelObjects) { 
					configurationViewer.refresh(o);
					configurationViewer.expandToLevel(o, 1);					
				}
			}});
	}
	
	protected void refresh(final Object modelObject) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if(configurationViewer.getControl().isDisposed()) { return; }
				configurationViewer.refresh(modelObject);
				configurationViewer.expandToLevel(modelObject, 1);
			}});
	}

	protected void refresh() {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if(configurationViewer.getControl().isDisposed()) { return; }
				configurationViewer.refresh();
			}});
	}

	protected void select(final ISphereCompositorFactory c) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if(configurationViewer.getControl().isDisposed()) { return; }
				configurationViewer.expandToLevel(c, 0);
				configurationViewer.setSelection(new StructuredSelection(c), true);
			}});
	}

	public ISphereFactory getRoot() {
		if(inputRoot.isEmpty()) { return null; }
		return inputRoot.get(0);
	}
	
	public void setRoot(ISphereFactory r) {
		inputRoot.clear();
		if(r != null) { inputRoot.add(r); }
	}

	@Override
	public void performHelp() {
//		PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
		PlatformUI.getWorkbench().getHelpSystem().displayHelp("ca.ubc.cs.ferret.configuration");
	}
}
