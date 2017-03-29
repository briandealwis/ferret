/*******************************************************************************
 * Copyright (c) 2004 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.views;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.IClusteringsContainer;
import ca.ubc.cs.clustering.IClusteringsProvider;
import ca.ubc.cs.ferret.Consultancy;
import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.ICallback;
import ca.ubc.cs.ferret.IConsultancyClient;
import ca.ubc.cs.ferret.display.DwBaseObject;
import ca.ubc.cs.ferret.display.DwConceptualQuery;
import ca.ubc.cs.ferret.display.IDisplayObject;
import ca.ubc.cs.ferret.model.Consultation;
import ca.ubc.cs.ferret.model.ExtensionSphereFactory;
import ca.ubc.cs.ferret.model.IConceptualQuery;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.model.SphereHelper;
import ca.ubc.cs.ferret.preferences.FerretPreferencePage;
import ca.ubc.cs.ferret.preferences.IFerretPreferenceConstants;
import ca.ubc.cs.ferret.references.AbstractReference;
import ca.ubc.cs.ferret.sphereconfig.SphereConfigurationWizard;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.TypesConversionManager;
import ca.ubc.cs.ferret.ui.WorkbenchAdapterLabelProvider;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationHistory;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.NavigationHistory;
import org.eclipse.ui.part.ViewPart;


/**
 * A view on the results of the various conceptual queries issued against the current selection.
 * 
 * HACK HACK HACK: we implement ISearchResultViewPart so that the Java views retain their selection.
 * They have some horribly ugly code to remove their content if receiving a selectionChanged() with
 * a non-ISearchResultViewPart.  Ugh.
 */
public class QueriesDossierView extends ViewPart
		implements ISelectionListener, ISearchResultViewPart, IConsultancyClient {
	// implements ISelectionListener: uncertain if this is the right way to do it; how do we hook
	// into mapping text selection to java model object?

    public final static String viewID = "ca.ubc.cs.ferret.views.QueriesDossier";
    public final static String queryStimulantsExtensionPointId = "query_stimulants";
    protected final static String MEMENTO_COLUMN_WIDTH = "columnwidth";
    protected final static String columnWidthId = "ca.ubc.cs.ferret.dossier.columnWidth";
	protected static final int MIN_COMPUTED_COLUMN_WIDTH = 16;
	private static final String PREFERENCE_PAGE_XP = "org.eclipse.ui.preferencePages";
	
	protected ISphere sphere = null;
    protected Consultation consultation = null;
    protected LinkedList<Consultation> recentConsultations = new LinkedList<Consultation>();

    protected TreeViewer viewer;
    //  Order must match that in DossierConstants
    protected TreeColumn columns[];
    protected String columnNames[];
    protected int columnWidths[];
    protected int columnStyles[];
    protected ColumnLayoutData columnLayouts[]; 
    protected TextToolTip toolTip;
    protected DossierDragNDropAdapter dndadapter;
    protected IMemento viewStateMemento;
    protected ListenerList<IQueryListener> queryListeners = new ListenerList<>();
    protected DossierLabelProvider labelProvider;
    protected DossierContentProvider contentProvider;
    protected Object toBeExpanded[];
    
    protected IAction pinAction;
    protected IAction openPreferencesAction;
//	protected IAction openQuerySourceAction;
	protected IAction launchConfigWizardAction;
    protected IAction expandCQsAction;
    protected IAction collapseAllAction;
    protected IAction removeFromViewAction;
    protected IAction removeSiblingsFromViewAction;
    protected IAction debugResetSpheres;
    protected OpenElementAction openAction;
    protected OpenParentAction openParentAction;
    protected OpenParentToolbarAction openParentToolbarAction;
    protected QueryParentAction queryParentAction;
    protected SelectOneOfManyAction<Consultation> queryHistoryAction; 
    
    //	protected ISelectionChangedListener treeSelectionChangedListener;
    protected IExecutionListener selectionChangingCommandListener;
    protected ITreeViewerListener treeListener;
    protected Set<String> queryCommandIds = new HashSet<String>();
    protected boolean performTextQuery = false;
    protected boolean selectionServiceRegistered = false;
    protected IPropertyChangeListener preferencesChangeListener;
	protected ISphereFactory sphereFactory;

	class NameComparator extends ViewerComparator {

        @Override
        public int category(Object element) {
        	if(element instanceof DwConceptualQuery) {
        		element = ((DwConceptualQuery)element).getObject();
        	}
        	if(element instanceof IConceptualQuery) {
        		IConceptualQuery icq = (IConceptualQuery)element;
        		return icq.getConsultation().getCategoryRank(icq.getCategory());
        	} else if(element instanceof IDisplayObject) {
                return ((IDisplayObject)element).getImportance();
            }
            return super.category(element);
        }
        
	}

	/**
	 * The constructor.
	 */
	public QueriesDossierView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent) {
		getWorkbench().getHelpSystem().setHelp(parent, "ca.ubc.cs.ferret.dossier");

		Consultancy.getDefault().addClient(this);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	    viewer.setUseHashlookup(true);
	    viewer.getTree().setLinesVisible(true);
		viewer.setContentProvider(contentProvider = new DossierContentProvider());
		viewer.setLabelProvider(labelProvider = new DossierLabelProvider());
		viewer.setComparator(new NameComparator());
		viewer.setInput(null);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//				new GridData(GridData.FILL_HORIZONTAL));
		viewer.getControl().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
            	// FIXME: we should probably do this via commands and setActionDefinitionId(), shouldn't we?
            	if (event.character == SWT.DEL) {
            		if(event.stateMask == 0 && removeFromViewAction.isEnabled()) {
            			removeFromViewAction.run();
            		} else if(event.stateMask == SWT.MOD2 && removeSiblingsFromViewAction.isEnabled()) {
            			// MOD2 should be shift
            			removeSiblingsFromViewAction.run();
            		}}}});

		setupColumns();
		configureViewer();
		configureDragNDropSupport();
		configureToolTip();
		if(preferencesChangeListener == null) {
			preferencesChangeListener = new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					// Oh, I suppose we should check the properties, but who
					// can be bothered...
					Object expanded[] = viewer.getExpandedElements();
					configureViewer();
					viewer.refresh();
					viewer.setExpandedElements(expanded);
				}};
		}
		getPreferenceStore().addPropertyChangeListener(preferencesChangeListener);
        
		final ChildSelectionUnwrapper unwrapper = new ChildSelectionUnwrapper();
		UnwrappingSelectionProvider provider =
				new UnwrappingSelectionProvider(viewer, unwrapper);
        provider.enableSelectionChangedNotification();
        getSite().setSelectionProvider(provider);
        
        if(viewStateMemento != null) { restoreState(viewStateMemento); }
		makeActions();
		hookContextMenu();
		hookOpenAction();
		contributeToActionBars();
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = unwrapper.unwrapSelection(event.getSelection());
				boolean isStructuredSelection = selection instanceof IStructuredSelection;
				boolean isMultiObject =
						isStructuredSelection
								&& ((IStructuredSelection)selection).size() > 0;
				openAction.selectionChanged(selection);
				removeFromViewAction.setEnabled(isMultiObject);
				removeSiblingsFromViewAction.setEnabled(isMultiObject);
				openParentAction.selectionChanged(selection);
				queryParentAction.selectionChanged(selection);
			}
		});
		ICommandService cs = (ICommandService)getSite().getService(ICommandService.class);
		if(selectionChangingCommandListener == null) {
			configureQueryCommandIds();
			selectionChangingCommandListener = new IExecutionListener() {
				public void notHandled(String commandId, NotHandledException exception) {}
				public void postExecuteFailure(String commandId, ExecutionException exception) {}				
				public void postExecuteSuccess(String commandId, Object returnValue) {
					if(queryCommandIds.contains(commandId)) {
						if(FerretPlugin.hasDebugOption("debug/selectionChanged")) {
							System.out.println("Command " + commandId + " executed: causing query");
						}
						// abandoned as a test selection happens immediately after the command!
						performTextQuery = true;  
						IWorkbenchPart part = getSite().getWorkbenchWindow().getActivePage().getActivePart();
						// was: getSelectionService().getSelection(part.getSite().getId()) but doesn't
						// seem to work
						if(FerretPlugin.shouldRespondToUserSelections()) {
							ISelection selection = getSite().getWorkbenchWindow().getSelectionService().getSelection();
							if(selection != null) {	// null indicates an undefined selection
								selectionChanged(part, selection);
							}
						}
					}
				}
				public void preExecute(String commandId, ExecutionEvent event) {}
			};
		}
		cs.addExecutionListener(selectionChangingCommandListener);

		TreeResizeAdapter resizer = new TreeResizeAdapter(viewer.getTree());
		for(ColumnLayoutData cld : columnLayouts) {
			resizer.addColumnData(cld);
		}
		parent.addControlListener(resizer);

//        getViewSite().getActionBars().setGlobalActionHandler(
//                IWorkbenchActionConstants.PROPERTIES,
//                propertiesAction);
//		treeSelectionChangedListener = new AutoTreeViewerExpandingSelector();
//		getSelectionProvider().addSelectionChangedListener(treeSelectionChangedListener);
	}

	protected void setupColumns() {
	    columnNames = new String[DossierConstants.NUMBER_COLUMNS];
	    columnLayouts = new ColumnLayoutData[DossierConstants.NUMBER_COLUMNS];
		columns = new TreeColumn[DossierConstants.NUMBER_COLUMNS];
		columnWidths = new int[DossierConstants.NUMBER_COLUMNS];
		
		columns[DossierConstants.COLUMN_DESCRIPTION] =
			new TreeColumn(viewer.getTree(), SWT.LEFT);
		columns[DossierConstants.COLUMN_DESCRIPTION].setText("Description");
		columnLayouts[DossierConstants.COLUMN_DESCRIPTION] = 
			new ColumnWeightData(60, 60);

		columns[DossierConstants.COLUMN_CATEGORY] =
			new TreeColumn(viewer.getTree(), SWT.LEFT);
		columns[DossierConstants.COLUMN_CATEGORY].setText("Category");
		columnLayouts[DossierConstants.COLUMN_CATEGORY] =
			new ColumnWeightData(15, 25);

		columns[DossierConstants.COLUMN_ELEMENT_COUNT] = 
			new TreeColumn(viewer.getTree(), SWT.RIGHT);
		columns[DossierConstants.COLUMN_ELEMENT_COUNT].setText("#E");
		columnLayouts[DossierConstants.COLUMN_ELEMENT_COUNT] = 
			new ColumnWeightData(10, 20);

		columns[DossierConstants.COLUMN_CLUSTERING] = 
			new TreeColumn(viewer.getTree(), SWT.LEFT);
		columns[DossierConstants.COLUMN_CLUSTERING].setText("Clustering/Fidelity");
		columnLayouts[DossierConstants.COLUMN_CLUSTERING] = 
			new ColumnWeightData(30, 40);

		String columnNames[] = new String[DossierConstants.NUMBER_COLUMNS];
		for(int i = 0; i < columns.length; i++) {
			columnNames[i] = columns[i].getText();
			columns[i].setResizable(columnLayouts[i].resizable);
			columns[i].addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					for (int j = 0; j < columnWidths.length; j++) {
						if (columns[j].equals(e.getSource())) {
							columnWidths[j] = columns[j].getWidth();
						}
					}
				}
			});
		}
		viewer.setColumnProperties(columnNames);
	}

	protected int max(int... values) {
		int m = values[0];
		for(int i = 1; i < values.length; i++) {
			if(m < values[i]) { m = values[i]; }
		}
		return m;
	}
	
	protected void configureViewer() {
		viewer.setAutoExpandLevel(getAutoExpandCQsPref() ? 2 : 0);
		viewer.getTree().setHeaderVisible(shouldShowTableHeader());
        viewer.getControl().setFont(JFaceResources.getFont(IFerretPreferenceConstants.PREF_DOSSIER_FONT));
		// Listen for changes in the workbench selection
        if(FerretPlugin.shouldRespondToUserSelections()) {
        	if(!selectionServiceRegistered && getViewSite().getWorkbenchWindow() != null) {
        		getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        		selectionServiceRegistered = true;
            }
        } else {
        	if(selectionServiceRegistered && getViewSite().getWorkbenchWindow() != null) {
        		getViewSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
        		selectionServiceRegistered = false;
        	}
        }

	}

	protected void configureQueryCommandIds() {
		queryCommandIds = new HashSet<String>();
        IExtensionPoint point = RegistryFactory.getRegistry()
        	.getExtensionPoint(FerretPlugin.pluginID + "." + queryStimulantsExtensionPointId);
        for(IExtension extension : point.getExtensions()) {
            for(IConfigurationElement element : extension.getConfigurationElements()) {
                if(!element.getName().equals("command")) {
                    FerretPlugin.log(new Status(IStatus.ERROR, extension.getNamespace(), IStatus.OK,
                            "invalid configuration element named " + element.getName() + " for extension point " +
                            queryStimulantsExtensionPointId, null));
                    continue;
                }
                String commandId = element.getAttribute("id");
                if(commandId != null && commandId.length() > 0) {
                	queryCommandIds.add(commandId);
                }
            }
        }
	}
	
    protected void configureDragNDropSupport() {
        Transfer transfers[] = { LocalSelectionTransfer.getTransfer() };
        if(dndadapter == null) {
        	dndadapter = new DossierDragNDropAdapter(this, viewer);
        }
        viewer.addDropSupport(DND.DROP_COPY | DND.DROP_LINK | DND.DROP_DEFAULT,
                transfers, dndadapter);  
        viewer.addDragSupport(DND.DROP_COPY | DND.DROP_LINK | DND.DROP_DEFAULT,
                transfers, dndadapter);  
    }

    protected void configureToolTip() {
    	toolTip = new TextToolTip(viewer.getControl());
		viewer.getControl().addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent e) {
				toolTip.hideToolTip();
			}

			public void mouseExit(MouseEvent e) {
				toolTip.hideToolTip();
			}

			public void mouseHover(MouseEvent e) {
				// FIXME: how to get the corresponding column?
				TreeItem item = viewer.getTree().getItem(new Point(e.x, e.y));
				if(item != null) {
					String text = null;
					if(item.getData() != null && item.getData() instanceof IDisplayObject) {
						text = ((IDisplayObject)item.getData()).getToolTip();
					}
					if(text == null) {
						// FIXME: test if there is any hidden text: if true, then don't bother
						// item.getTextBounds(index)
						// tree.gc.textExtent(item.getText()) ... 
						text = item.getText(); 
					}
					if(text != null && text.trim().length() > 0) {
						toolTip.showToolTip(text);
					} else {
						toolTip.hideToolTip();
					}
				} else {
					toolTip.hideToolTip();
				}
			}		
		});
    }
    
	public void dispose() {
		Consultancy.getDefault().removeClient(this);
//	    getSelectionProvider().removeSelectionChangedListener(treeSelectionChangedListener);
		if(consultation != null) {
			getConsultancy().abandon(consultation);
			consultation = null;
		}
		if(treeListener != null) {
			viewer.removeTreeListener(treeListener);
		}
		if(getViewSite().getWorkbenchWindow() != null) {
			getViewSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		}
		if(preferencesChangeListener != null) {
			getPreferenceStore().removePropertyChangeListener(preferencesChangeListener);
		}
		if(selectionChangingCommandListener != null) {
			ICommandService cs = (ICommandService)getViewSite().getWorkbenchWindow().getService(ICommandService.class); // 3.2
//			ICommandService cs = (ICommandService)getWorkbench().getAdapter(ICommandService.class); // 3.1
			cs.removeExecutionListener(selectionChangingCommandListener);
		}
		viewer = null;
		super.dispose();
	}

	public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		/*
		 *  hive off as an async runnable: we've had problems on startup where a selection
		 *  has been restored on startup, triggered this method, but as the UI (UISychronizer) is 
		 *  still in startup-mode, any of our asyncExecs are added to a pending queue,
		 *  and thus most of our model changes in the sphere-configuration UI are
		 *  never done.  (This is often triggered when the workbench has been saved with a
		 *  PDE editor as the active editor, such as the site editor; the active selection is restored
		 *  on startup, triggering selection listeners.)  
		 *  
		 *  One possible solution is to change some of the asyncExecs to syncExecs.
		 *  But I'd prefer to avoid that where possible.  So instead we fork this off as an
		 *  asyncExec, which will be handled in due course. 
		 */
		asyncExec(new Runnable() {
			public void run() {
				handleSelectionChanged(part, selection);
			}});
	}
	
	protected void handleSelectionChanged(IWorkbenchPart part, ISelection selection) {
//		This is where we hook into the current element
        if(FerretPlugin.hasDebugOption("debug/selectionChanged")) {
            System.out.println("SelectionChanged: selection={" + FerretPlugin.compactPrettyPrint(selection) + "} ["
                    + selection.getClass().getName() + "], part=" + part);
        }
        if(part == this) {
			// If this is us, then don't react
        	return;
    	}
		if (selection.isEmpty()) {
			return;
		}
		if(isPinned()) { return; }
		Consultation c = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			c = performQuery(sel.toArray());
		} else if (selection instanceof ITextSelection) {
			if(performTextQuery) {
				performTextQuery = false;
				System.out.println("Performing text query!");
				if(part instanceof IEditorPart) {
					for(SphereHelper sphere : FerretPlugin.getSphereHelpers()) {
						Object selected[] = sphere.getSelectedObjects((IEditorPart)part);
						if(selected != null && selected.length > 0) {
							c = performQuery(selected);
							break;
						}
					}
				}
			}
			if(c != null) { saveNavigationHistory(c, part); }
		}
	}

	protected void saveNavigationHistory(Consultation c, IWorkbenchPart part) {
		IWorkbenchPage page = part.getSite().getPage();
		INavigationHistory nh = page.getNavigationHistory();
		if(nh instanceof NavigationHistory) {
			XMLMemento memento = XMLMemento.createWriteRoot(IWorkbenchConstants.TAG_NAVIGATION_HISTORY);
			FerretPlugin.invokeMethod(nh, "saveState", new Class[] { IMemento.class },
					new Object[] { memento });
			c.setNavigationHistory(memento);
		}
	}
	
	protected void restoreNavigationState(Consultation c) {
		if(c.getNavigationHistory() == null) { return; }
		INavigationHistory nh = getSite().getPage().getNavigationHistory();
		if(nh instanceof NavigationHistory) {
			FerretPlugin.invokeMethod(nh, "restoreState", new Class[] { IMemento.class },
					new Object[] { c.getNavigationHistory()  });
		}
	}

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, getSelectionProvider());
	}
	
	/**
     * Return the currently selected element, providing there is exactly one
     * element selected.  If no element selected, or > 1 element selected,
     * then return null.   This method goes directly to the viewer as we have
     * installed a selection-provider that unwraps the result (to deal with issues
     * relating to non-adaptable objectContributions)
     * @return the single (possibly display-model) selected element, or null
	 */
    protected Object getViewerSelectedElement() {
        ISelection sel = viewer.getSelection();
        if(sel == null || !(sel instanceof IStructuredSelection)) { return null; }
        if(((IStructuredSelection)sel).size() != 1) { return null; }
        return ((IStructuredSelection)sel).getFirstElement();
    }

    protected void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager) {
		manager.add(pinAction);
        manager.add(expandCQsAction);
        manager.add(collapseAllAction);
        manager.add(new Separator());
		addSphereConfigurationSection(manager);

		manager.add(new Separator());
        manager.add(openPreferencesAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        if(FerretPlugin.getDefault().isDebugging()) {
            manager.add(new Separator());
			manager.add(debugResetSpheres);
        }
	}

	/**
	 * @param manager
	 */
	private void addSphereConfigurationSection(IMenuManager manager) {
		IMenuManager sphereDefinitionMenu = new MenuManager("Predefined Spheres");
		sphereDefinitionMenu.setRemoveAllWhenShown(true);
		sphereDefinitionMenu.addMenuListener(m -> {
			for (ISphereFactory factory : getConfiguredSphereFactories()) {
				Action action = new Action(factory.getDescription()) {
					@Override
					public void run() {
						try {
							setSphere(factory);
						} catch (FerretConfigurationException ex) {
							FerretPlugin.log(ex);
						}
					}
				};
				action.setChecked(Objects.equals(sphereFactory, factory));
				m.add(action);
			}
		});
		manager.add(sphereDefinitionMenu);
		manager.add(launchConfigWizardAction);
	}

	protected void setSphere(ISphereFactory factory) throws FerretConfigurationException {
		this.sphereFactory = factory;
		this.sphere = factory.createSphere(new NullProgressMonitor());
		consultancyReset();
	}

	private Collection<ISphereFactory> getConfiguredSphereFactories() {
		IExtensionRegistry registry = getExtensionRegistry();
		List<ISphereFactory> factories = new ArrayList<>();
		for (IConfigurationElement ce : registry.getConfigurationElementsFor("ca.ubc.cs.ferret.sphereConfigurations")) {
			if ("factory".equals(ce.getName())) {
				factories.add(new ExtensionSphereFactory(ce));
			}
		}
		return factories;
	}

	private IExtensionRegistry getExtensionRegistry() {
		return getSite().getService(IExtensionRegistry.class);
	}

	protected <T> void addClusteringActions(final IClusteringsContainer<T> container,
			String title, IContributionManager manager) {
		if (container.getAllClusterings() == null || container.getAllClusterings().isEmpty()) {
			return;
		}
        SelectOneOfManyAction<Clustering<T>> clusteringAction = 
        	new SelectOneOfManyAction<Clustering<T>>(title);
        clusteringAction.addSelectionCallback(new ICallback<Clustering<T>>() {
            public void run(Clustering<T> selected) {
//            	if(selected == null) { return; }
                container.setActiveClustering(selected);
                clusteringChanged(container, selected);
             }});
        clusteringAction.add("no clustering", null);
        Multimap<IClusteringsProvider<T>, Clustering<T>> clusterings =
        	container.getAllClusterings();
        for(IClusteringsProvider<T> cf : clusterings.keySet()) {
        	clusteringAction.addSeparator();
	        List<Clustering<T>> sortedClusters = 
	        	new ArrayList<Clustering<T>>(clusterings.get(cf));
	        Collections.sort(sortedClusters, new Comparator<Clustering<? extends T>>() {
	            public int compare(Clustering<? extends T> o1, Clustering<? extends T> o2) {
	                return o1.toString().compareTo(o2.toString());
	            }
	        });
	        for(Clustering<T> c : sortedClusters) {
	        	clusteringAction.add(c.toString() + " [" + c.getClusters().size() + " groups]", c);
	        }
        }
        clusteringAction.setSelected(container.getActiveClustering()); 
        manager.add(clusteringAction);
	}
    
	public void fillContextMenu(IMenuManager manager) {
//		manager.add(new Separator());
		if(toolTip != null) { toolTip.hideToolTip(); }
        Object value = getViewerSelectedElement();
        manager.add(openAction);
        manager.add(openParentAction);
        manager.add(queryParentAction);
//        manager.add(openQuerySourceAction);
        manager.add(removeFromViewAction);
        manager.add(removeSiblingsFromViewAction);
        if(value instanceof IClusteringsContainer) {
            addClusteringActions((IClusteringsContainer<?>)value, "Cluster results by...", manager);
        }
        if(FerretPlugin.getDefault().isDebugging()) {
        	manager.add(new Separator());
        	if(value instanceof DwConceptualQuery) {
        		Action resetCQAction = new Action() {
        			@Override
        			public void run() {
        				Object v = getViewerSelectedElement();
        				if(v instanceof DwConceptualQuery) {
        					DwConceptualQuery dcq = (DwConceptualQuery)v;
        					reevaluate((IConceptualQuery)dcq.getObject(), dcq);
        				}
        			}};
    			resetCQAction.setDescription("Re-evaluate current query");
    			resetCQAction.setText("Re-evaluate");
    			manager.add(resetCQAction);
        	}
        	Action refreshAction = new Action() {
        		@Override
        		public void run() {
        			Object v = getViewerSelectedElement();
        			if(v instanceof DwBaseObject && ((DwBaseObject)v).getParent() != null) {
        				((DwBaseObject)((DwBaseObject)v).getParent()).rebuildChildren();
        				((DwBaseObject)((DwBaseObject)v).getParent()).refresh();
        			}
        		}};
    		refreshAction.setText("Rebuild parent");
    		manager.add(refreshAction);
        }
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

    protected void reevaluate(IConceptualQuery icq, final IDisplayObject dcq) {
		icq.reset();
		performQuery(icq.getConsultation());
	}

	protected void fillLocalToolBar(IToolBarManager manager) {
//		manager.appendToGroup(IContextMenuConstants.GROUP_ADDITIONS,
//		manager.add(backgroundingAction);
//		manager.add(openQuerySourceAction);
		manager.add(pinAction);
        manager.add(expandCQsAction);
        manager.add(collapseAllAction);
		manager.add(new Separator());
		manager.add(queryHistoryAction);
		manager.add(openParentToolbarAction);
		manager.add(new Separator());
	}

	protected void makeActions() {
		pinAction = new Action("Pin dossier contents", IAction.AS_CHECK_BOX) {
			public void run() {
                viewer.refresh(true);
			}
		};
        pinAction.setChecked(false);
		pinAction.setToolTipText("Pin current contents of dossier");
        pinAction.setImageDescriptor(getWorkbench().getSharedImages()
                .getImageDescriptor(IWorkbenchGraphicConstants.IMG_ETOOL_PIN_EDITOR));

        openAction = new OpenElementAction();
        openParentAction = new OpenParentAction();
        queryParentAction = new QueryParentAction();
        openParentToolbarAction = new OpenParentToolbarAction();
        
//        openQuerySourceAction = new Action() {
//			public void run() {
//				if(consultation == null) { return; }
//				Object objs[] = consultation.getQueryElements(); 
//				if(objs.length != 1) { return; }
//				openInEditor(objs[0]);
//			}
//		};
//		openQuerySourceAction.setEnabled(false);	// enabled in performQuery()
//		openQuerySourceAction.setText("Re-open query item");
//		openQuerySourceAction.setToolTipText("Re-open original query item");
//		openQuerySourceAction.setImageDescriptor(
//				FerretPlugin.imageDescriptorFromPlugin(FerretPlugin.pluginID, "icons/elcl16/nav_home.gif"));
//		openQuerySourceAction.setDisabledImageDescriptor(
//				FerretPlugin.imageDescriptorFromPlugin(FerretPlugin.pluginID, "icons/dlcl16/nav_home.gif"));
		
		removeFromViewAction = new Action() {
			public void run() {
				removeSelectedElementsFromView();
			}
		};
		removeFromViewAction.setEnabled(true);
		removeFromViewAction.setText("Remove from view");
		removeFromViewAction.setToolTipText("Temporarily remove item from current view");

		removeSiblingsFromViewAction = new Action() {
			public void run() {
				removeSiblingsFromView();
			}
		};
		removeSiblingsFromViewAction.setEnabled(true);
		removeSiblingsFromViewAction.setText("Remove siblings from view");
		removeSiblingsFromViewAction.setToolTipText("Temporarily remove items' siblings from current view");

        expandCQsAction = new Action("Expand query results", Action.AS_PUSH_BUTTON) {
            public void run() {
            	expandCQs(false);
            }
        };
        expandCQsAction.setToolTipText("Expand query results");
        expandCQsAction.setImageDescriptor(
        		FerretPlugin.imageDescriptorFromPlugin(FerretPlugin.pluginID, "icons/expandCQs.gif"));
        collapseAllAction = new Action("Collapse All", Action.AS_PUSH_BUTTON) {
            public void run() {
                viewer.collapseAll();
            }
        };
        collapseAllAction.setToolTipText("Collapse All");
        collapseAllAction.setImageDescriptor(
        		FerretPlugin.imageDescriptorFromPlugin(FerretPlugin.pluginID, "icons/collapseall.gif"));

		debugResetSpheres = new Action("Reset") {
			public void run() {
				for(SphereHelper s : FerretPlugin.getSphereHelpers()) {
					s.reset();
				}
				TypesConversionManager.stop();
				getConsultancy().reset();	// will trigger our consultancyReset()
			}
		};
//		debugResetSpheres.setImageDescriptor(getWorkbench().getSharedImages()
//                .getImageDescriptor(IWorkbenchGraphicConstants.IMG_ETOOL_PIN_EDITOR));

		openPreferencesAction = new Action("Preferences...", Action.AS_PUSH_BUTTON) {
			@Override
			public void runWithEvent(Event event) {
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(event.display.getActiveShell(), 
						FerretPreferencePage.pageId, findPreferenceSubPages(FerretPreferencePage.pageId), null);
				dialog.open();
			}};
			
		launchConfigWizardAction = new Action("Configure spheres...", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				configureSpheres();
			}};
		launchConfigWizardAction.setToolTipText("Configure the spheres for queries.");
		
		queryHistoryAction = new SelectOneOfManyAction<Consultation>("History") {

			@Override
			protected void addActionsToMenu() {
				clear();
				for(Consultation c : recentConsultations) {
					add(FerretPlugin.prettyPrint(c.getOriginalElements()), 
							FerretPlugin.getImage(c.getOriginalElements()[0]), c);
				}
				super.addActionsToMenu();
			}

			@Override
			public void run() {
				if(consultation == null) { return; }
				if(shouldRestoreNavigationStateOnHistorySelection()) {
					restoreNavigationState(consultation);
				}
				openInEditor(consultation);
			}
			
		};
		queryHistoryAction.addSelectionCallback(new ICallback<Consultation>() {
			public void run(Consultation c) {
				performQuery(c);
				if(shouldRestoreNavigationStateOnHistorySelection()) {
					restoreNavigationState(c);
				}
				openInEditor(c);
			}});
		queryHistoryAction.setActionStyle(IAction.AS_PUSH_BUTTON);
		queryHistoryAction.setText("Re-open query item");
		queryHistoryAction.setToolTipText("Re-open original query item");
		queryHistoryAction.setImageDescriptor(
				FerretPlugin.imageDescriptorFromPlugin(FerretPlugin.pluginID, "icons/elcl16/nav_home.gif"));
		queryHistoryAction.setDisabledImageDescriptor(
				FerretPlugin.imageDescriptorFromPlugin(FerretPlugin.pluginID, "icons/dlcl16/nav_home.gif"));

	}
	
	public void expandCQs(final boolean toggleExpansion) {
		asyncExec(new Runnable() {
			public void run() {
				if(toggleExpansion) {
					Object prev[] = viewer.getExpandedElements();
			        viewer.expandToLevel(2);		
					Object curr[] = viewer.getExpandedElements();
					if(prev.length == curr.length) {
						viewer.collapseAll();
					}
				} else {
			        viewer.expandToLevel(2);
				}
			}});
	}

	protected void configureSpheres() {
	      SphereConfigurationWizard wizard = new SphereConfigurationWizard();
		if (sphereFactory != null) {
			// .getAdapter(ISphereFactory) will unwrap ExtensionSphereFactory
			wizard.setSphereFactoryRoot(sphereFactory.getAdapter(ISphereFactory.class));
		}

	      WizardDialog dialog = new WizardDialog(getShell(), wizard);
	      int result = dialog.open();
	      if(result == WizardDialog.CANCEL) { return; }
	      sphereFactory = wizard.getSphereFactoryRoot();
	      for(Object o : queryListeners.getListeners()) {
	    	  ((IQueryListener)o).reconfigured();
		}
		if ((sphere == wizard.getSphere()) && result == WizardDialog.OK) {
	    	  ErrorDialog.openError(getShell(), "Error", "Unable to create sphere", 
	    			  new Status(IStatus.ERROR, FerretPlugin.pluginID, -1,
	    					  wizard.getClass().getName() + ".getSphere() == null", null));
	      }
	      consultancyReset();
	}

	protected String[] findPreferenceSubPages(String prefPageId) {
		Set<String> displayIds = new HashSet<String>();
		displayIds.add(prefPageId);
		while(findPreferenceSubPages(displayIds)) {
			/* do nothing */
		}
		return displayIds.toArray(new String[displayIds.size()]);
	}
	protected boolean findPreferenceSubPages(Set<String> displayIds) {
		boolean additionsMade = false;
		for(IConfigurationElement elmt : RegistryFactory.getRegistry().getConfigurationElementsFor(PREFERENCE_PAGE_XP)) {
			if(elmt.getName().equals("page") && displayIds.contains(elmt.getAttribute("category"))
					&& !displayIds.contains(elmt.getAttribute("id"))) {
				displayIds.add(elmt.getAttribute("id"));
				additionsMade = true;
			}
		}
		return additionsMade;
	}

	protected void removeSelectedElementsFromView() {
        ISelection sel = viewer.getSelection();
        if(sel == null || !(sel instanceof IStructuredSelection)) { return; }
        for(Iterator<?> it = ((IStructuredSelection)sel).iterator(); it.hasNext();) {
        	Object current = it.next();
    		if(current instanceof IDisplayObject) {
    			IDisplayObject o = (IDisplayObject)current;
    			if(o.getParent() != null) { o.getParent().removeChild(o); }
    		}		
        }
        // IDisplayObject.removeChild() will cause a refresh if necessary
	}

	protected void removeSiblingsFromView() {
        ISelection sel = viewer.getSelection();
        if(sel == null || !(sel instanceof IStructuredSelection)) { return; }
        Multimap<IDisplayObject,Object> map = MultimapBuilder.hashKeys().arrayListValues().build();
        for(Iterator<?> it = ((IStructuredSelection)sel).iterator(); it.hasNext();) {
        	Object current = it.next();
    		if(current instanceof IDisplayObject) {
    			IDisplayObject o = (IDisplayObject)current;
    			if(o.getParent() != null) { map.put(o.getParent(), o); }
    		}		
        }
        for(IDisplayObject p : map.keySet()) {
        	Collection<Object> keep = map.get(p);
        	Collection<IDisplayObject> kids = new ArrayList<IDisplayObject>();
        	Collections.addAll(kids, p.getChildren());
        	for(IDisplayObject kid : kids) {
                // IDisplayObject.removeChild() will cause a refresh if necessary
        		if(!keep.contains(kid)) { p.removeChild(kid); }
        	}
        }
	}

	protected Shell getShell() {
		return getViewSite().getWorkbenchWindow().getShell();
	}
	
	protected IWorkbench getWorkbench() {
		return getViewSite().getWorkbenchWindow().getWorkbench();
	}

	protected IPreferenceStore getPreferenceStore() {
		return FerretPlugin.getDefault().getPreferenceStore();
	}
	
	public boolean getAutoExpandCQsPref() {
		return getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_AUTO_EXPAND);
	}
	
    protected int getRecentConsultationsLimit() {
		return getPreferenceStore().getInt(IFerretPreferenceConstants.PREF_RECENT_CONSULTATIONS_LIMIT);
	}

	protected boolean shouldShowTableHeader() {
		return getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_SHOW_HEADER);
	}
	
	protected void shouldShowTableHeader(boolean value) {
		getPreferenceStore().setValue(IFerretPreferenceConstants.PREF_SHOW_HEADER, value);
	}
	
    protected ISelectionProvider getSelectionProvider() {
        return getSite().getSelectionProvider();
    }
    
    protected Object getSelectedElement() {
    	ISelection selection = getSelection();
    	if(selection instanceof IStructuredSelection &&
    			((IStructuredSelection)selection).size() == 1) {
    		return ((IStructuredSelection)selection).getFirstElement();
    	}
    	return null;
    }
    
    protected ISelection getSelection() {
        return getSelectionProvider().getSelection();
    }

    protected boolean isOpenable(Object obj) {
		obj = unwrapObject(obj);
    	if(obj instanceof AbstractReference) { return true; }
    	for(SphereHelper sphere : FerretPlugin.getSphereHelpers()) {
    		if(sphere.canOpen(obj)) { return true; }
    	}
    	return false;
    }
    
	private Object unwrapObject(Object obj) {
		if(obj instanceof IDisplayObject) { return unwrapObject(((IDisplayObject)obj)
				.getObject()); }
		if(obj instanceof FerretObject) { return unwrapObject(((FerretObject)obj)
				.getPrimaryObject()); }
		return obj;
	}

	protected void openInEditor(final Consultation c) {
		if(c.getOriginalElements().length == 1) {
			openInEditor(c.getOriginalElements()[0]); 
			return;
		} else if(c.getOriginalElements().length <= 0) {
			return;
		}
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				ListDialog d = new ListDialog(getShell());
				d.setAddCancelButton(true);
				d.setBlockOnOpen(true);
				d.setInitialSelections(new Object[] { c.getOriginalElements()[0] });
				d.setInput(c.getOriginalElements());
				d.setLabelProvider(new DossierLabelProvider());
				d.setContentProvider(new ArrayContentProvider());
				d.setTitle("Open which element?");
				d.setMessage("Select element to open:");
				if(d.open() == Window.OK) {
					Object results[] = d.getResult();
					if(results.length > 0) {
						openInEditor(results[0]);
					}
				}
			}});
    }
    
	protected void openInEditor(Object obj) {
		if(obj instanceof AbstractReference) {
			((AbstractReference)obj).open();
		} else {
			for(SphereHelper sphere : FerretPlugin.getSphereHelpers()) {
				if(sphere.openObject(obj)) { return; }
			}
		}
	}
    
    protected void hookOpenAction() {
    	if(FerretPlugin.shouldHonourOpenPreference()) {
    		viewer.addOpenListener(new IOpenListener() {
				public void open(OpenEvent event) {
					openAction.run();
				}});
    	} else {
			viewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					openAction.run();
				}
			});
    	}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
    
    public boolean isPinned() {
        return pinAction.isChecked();
    }
    
    public void togglePinning() {
    	pinAction.setChecked(!pinAction.isChecked());
    }

	/* Assumes is called on UI thread */
	public Consultation performQuery(Object objects[]) {
        Consultation c = getConsultation(objects);
        if(c == null) { return null; }
		basicPerformQuery(c);
		return c;
	}
	
	public Consultation getConsultation(Object[] objects) {
		if(viewer == null) { return null; }
		ISphere tb = getSphere();
		if(tb == null) { return null; }
    	return getConsultancy().getConsultation(objects, tb);
	}

	/* Assumes is called on UI thread */
	public void performQuery(Consultation c) {
		if(viewer == null) { return; }
		ISphere tb = getSphere();
		if(tb == null) { return; }
		c.setSphere(tb);
		basicPerformQuery(c);
	}

	/* Assumes is called on UI thread */
	protected void basicPerformQuery(Consultation c) {
		if(c == consultation) {
			refresh(c);
			if((c.isDone() && c.isValid()) || c.isInProgress()) { return; }
		} else {
			consultation = c;
			toBeExpanded = null;
			addToRecentList(c);
			if(FerretPlugin.hasDebugOption("debug/issuingConsultations")) {
				System.out.println("performQuery(" + FerretPlugin.debugPrint(c.getOriginalElements()) + ")");
			}
	        setContentDescription(FerretPlugin.prettyPrint(c.getOriginalElements()));
	        setTitleToolTip(FerretPlugin.prettyPrint(c.getOriginalElements()));
			for(Object o : queryListeners.getListeners()) {
				((IQueryListener)o).queryInitiated(c);
			}
	        c.registerChangeCallback(getUpdateRunnable()); 
	        setInput(c);
		}
        getConsultancy().performConsultation(c, true);
        c.boostPriority();
    }

	protected void addToRecentList(Consultation c) {
		while(recentConsultations.contains(c)) {
			recentConsultations.remove(c);
		}
		while(recentConsultations.size() > getRecentConsultationsLimit()) {
			recentConsultations.removeLast();
		}
		recentConsultations.addFirst(c);
	}

	protected Consultancy getConsultancy() {
		return Consultancy.getDefault();
	}

	protected ISphere getSphere() {
    	if(sphere == null) {
    		launchConfigWizardAction.run();
    	}
		return sphere;
	}

	/* Assumes is called on UI thread */
	protected synchronized void setInput(Consultation c) {
		viewer.setInput(c);
        openParentToolbarAction.selectionChanged(
        		new StructuredSelection(c.getOriginalElements()));
        // Try to deal with this crazy resizing issue
//		for(int i = 0; i < columns.length; i++) {
//			columns[i].setWidth(columnWidths[i]);
//		}
    }

    public ICallback<Consultation> getUpdateRunnable() {   
        return new ICallback<Consultation>() {
        	public void run(final Consultation result) {
				if(result.isDone()) {
					for(Object o : queryListeners.getListeners()) {
						((IQueryListener)o).queryCompleted((Consultation)result);
					}
				}
        		asyncExec(new Runnable() { 
        					public void run() { 
        						// avoid unnecessary updates; this might happen if we have
        						// multiple consultations pending (e.g. a new one is started
        						// before a previous consultation is finished).
        						if(viewer != null && viewer.getInput() == result) {
        							viewer.refresh();
        						}
        						if(toBeExpanded != null && toBeExpanded.length > 0 && result.isDone()) {
        							viewer.setExpandedElements(toBeExpanded);
        							toBeExpanded = null;
        						}
        					}
        				});
        	}
    	};
    }

    public ISearchResultPage getActivePage() {
        /* here only to support the monstrous hack described in the class comment */
        return null;
    }

    public void updateLabel() {
        /* here only to support the monstrous hack described in the class comment */
    }
    
    protected void asyncExec(Runnable runnable) {
    	if(getSite() == null) { return; }
    	if(getSite().getShell() == null || getSite().getShell().isDisposed()) { return; }
    	getSite().getShell().getDisplay().asyncExec(runnable);
    }

    protected void refresh(Object object) {
    	refresh(object, false);
    }

    protected void refresh(final Object object, final boolean expand) {
        asyncExec(
                new Runnable() { 
                    public void run() { 
                    		if(viewer == null || viewer.getControl() == null || viewer.getControl().isDisposed()) {
                    			return;
                    		}
                        if(expand) { viewer.expandToLevel(object, 1); }
                        viewer.refresh(object, true);
                    }
                });
    }

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		viewStateMemento = memento;
		try {
			String sphereFactoryId = memento == null ? null : memento.getString("sphereFactory");
			for (ISphereFactory factory : getConfiguredSphereFactories()) {
				if (sphereFactoryId == null || sphereFactoryId.equals(factory.getId())) {
					setSphere(factory);
					break;
				}
			}
		} catch (FerretConfigurationException ex) {
			FerretPlugin.log(ex);
		}
	}
	
	public void saveState(IMemento memento) {
		IMemento columnMemento = memento.createChild(columnWidthId);
		for(int i = 0; i < columns.length; i++) {
			IMemento m = columnMemento.createChild("column" + i);
			m.putInteger(MEMENTO_COLUMN_WIDTH, columnWidths[i]);
		}
		if (sphereFactory != null && !Strings.isNullOrEmpty(sphereFactory.getId())) {
			memento.putString("sphereFactory", sphereFactory.getId());
		}
	}
	
	protected void restoreState(IMemento memento) {
		if(memento == null) { return; }
		IMemento columnMemento = memento.getChild(columnWidthId);
		if(columnMemento == null) { return; }
		for(int i = 0; i < DossierConstants.NUMBER_COLUMNS; i++) {
			IMemento m = columnMemento.getChild("column" + i);
			if(m == null) { continue; }
			int cw = m.getInteger(MEMENTO_COLUMN_WIDTH);
			columnWidths[i] = Math.max(cw, MIN_COMPUTED_COLUMN_WIDTH);
			columns[i].setWidth(columnWidths[i]);
		}
		viewer.refresh();
	}

	public void addQueryListener(IQueryListener listener) {
		if(viewer == null || viewer.getTree() == null) { return; }
		viewer.addTreeListener(listener);
		queryListeners.add(listener);
	}
	
	public void removeQueryListener(IQueryListener listener) {
		if(viewer == null || viewer.getTree() == null) { return; }
		viewer.removeTreeListener(listener);
		queryListeners.remove(listener);
	}

	public boolean isAwaiting(Consultation c) {
		return consultation == c;
	}

	public boolean shouldRestoreNavigationStateOnHistorySelection() {
		return getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_HISTORY_RESTORE_NAVIGATION_TO_QUERY_POINT);
	}

	public boolean shouldRestoreNavigationStateOnSolutionSelection() {
		return getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_SOLUTION_RESTORE_NAVIGATION_TO_QUERY_POINT);
	}

	public <T> void clusteringChanged(IClusteringsContainer<? extends T> object, Clustering<? extends T> selected) {
		for(Object o : queryListeners.getListeners()) {
			((IQueryListener)o).clusteredBy(object, selected);
		}
		if(object instanceof IDisplayObject) {
			IDisplayObject dobj = selected == null ? (IDisplayObject)object :
					((IDisplayObject)object).getDisplayObject(selected);
			if(dobj == null) { dobj = (IDisplayObject)object; }
			refresh(dobj, true);
		}
	}

	public class OpenElementAction extends Action {
		Object element;
		public OpenElementAction() {
			super("Open", IAction.AS_PUSH_BUTTON);
	        setToolTipText("Open");
		}

		@Override
		public void run() {
			if(shouldRestoreNavigationStateOnSolutionSelection()) {
				restoreNavigationState(consultation);
			}

			openInEditor(element != null ? element : getSelectedElement());
		}
		public void selectionChanged(ISelection sel) {
			setEnabled(false);
			setText("Open");
			element = null;
			if(sel.isEmpty() || !(sel instanceof IStructuredSelection)) { return; }
			IStructuredSelection ss = (IStructuredSelection)sel;
			if(ss.size() != 1) { return; }
			element = ss.getFirstElement(); 
			if(!isOpenable(element)) { return; }
			setEnabled(true);
			setText("Open: " + FerretPlugin.compactPrettyPrint(element));
			setImageDescriptor(FerretPlugin.getImage(element));
		}
	}

	public class OpenParentAction extends Action {
		Object parent;
		public OpenParentAction() {
			super("Open Parent", IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			if(shouldRestoreNavigationStateOnSolutionSelection()) {
				restoreNavigationState(consultation);
			}
			openInEditor(parent);
		}

		public void selectionChanged(ISelection sel) {
			setEnabled(false);
			parent = null;
			setText("Open Parent");
			if(sel.isEmpty() || !(sel instanceof IStructuredSelection)) { return; }
			IStructuredSelection ss = (IStructuredSelection)sel;
			if(ss.size() != 1) { return; }
			parent = FerretPlugin.getParent(ss.getFirstElement());
			if(parent == null) { return; }
			if(!isOpenable(parent)) { return; }
			setEnabled(true);
			setText("Open Parent: " + FerretPlugin.compactPrettyPrint(parent));
			setImageDescriptor(FerretPlugin.getImage(parent));
		}

	}

	public class QueryParentAction extends Action {
		Object parent;
		public QueryParentAction() {
			super("Query Parent", IAction.AS_PUSH_BUTTON);
			setToolTipText("Query Parent");
		}

		@Override
		public void run() {
			performQuery(new Object[] { parent });
		}

		public void selectionChanged(ISelection sel) {
			setEnabled(false);
			parent = null;
			setText("Query Parent");
			setToolTipText("Query Parent");
			if(sel.isEmpty() || !(sel instanceof IStructuredSelection)) { return; }
			IStructuredSelection ss = (IStructuredSelection)sel;
			if(ss.size() != 1) { return; }
			parent = FerretPlugin.getParent(ss.getFirstElement());
			if(parent == null) { return; }
			setEnabled(true);
			setText("Query Parent: " + FerretPlugin.compactPrettyPrint(parent));
			setImageDescriptor(FerretPlugin.getImage(parent));
		}
		
	}
	
	public class OpenParentToolbarAction extends Action {
		Object parent;
		public OpenParentToolbarAction() {
			super("Open Parent", IAction.AS_PUSH_BUTTON);
			setEnabled(false);
			setToolTipText("Open Parent");
			setImageDescriptor(FerretPlugin.imageDescriptorFromPlugin(
					FerretPlugin.pluginID, "icons/elcl16/open-parent.gif"));
			setDisabledImageDescriptor(FerretPlugin.imageDescriptorFromPlugin(
					FerretPlugin.pluginID, "icons/dlcl16/open-parent.gif"));
		}
		
		@Override
		public void run() {
			openInEditor(parent);
		}

		public void selectionChanged(ISelection sel) {
			setEnabled(false);
			parent = null;
			setText("Open Parent");
			setToolTipText("Open Parent");
			if(sel.isEmpty() || !(sel instanceof IStructuredSelection)) { return; }
			IStructuredSelection ss = (IStructuredSelection)sel;
			if(ss.size() != 1) { return; }
			parent = FerretPlugin.getParent(ss.getFirstElement());
			if(parent == null) { return; }
			setEnabled(true);
			setText("Open Parent: " + FerretPlugin.compactPrettyPrint(parent));
			setToolTipText("Open Parent: " + FerretPlugin.compactPrettyPrint(parent));
		}
	}

	public void consultancyReset() {
		if(consultation == null || isPinned() ||
				!getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_REISSUE_CURRENT_QUERY_ON_CHANGE)) {
			return;
		}
		asyncExec(new Runnable() {
			public void run() {
                                if(viewer == null) { return; }
				if(toBeExpanded == null) {
					Object expanded[] = viewer.getExpandedElements();
					if(expanded.length > 0) { toBeExpanded = expanded; }
				}
				consultation.reset();
				performQuery(consultation);
			}
		});
	}
	
	public Consultation getInput() {
		return consultation;
	}

	public void addToQuery(Object[] additions) {
		List<Object> nqe = new ArrayList<Object>();
		Collections.addAll(nqe, consultation.getOriginalElements());
		Collections.addAll(nqe, additions);
		performQuery(nqe.toArray());		
	}

	@Override
	protected void setContentDescription(String description) {
		super.setContentDescription(description.replace('\n', ' '));
	}

	public void promptForDesiredQueries(Object[] objects) {
        Consultation c = getConsultancy().createConsultation(objects, getSphere());
        c.buildConceptualQueries(new NullProgressMonitor());
        IConceptualQuery cqs[] = c.getConceptualQueries();
        viewer.getSorter().sort(viewer, cqs);

        ILabelProvider lp = new WorkbenchAdapterLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof IConceptualQuery) {
					IConceptualQuery icq = (IConceptualQuery)element;
					return "[" + icq.getCategory() + "]\t" + icq.getDescription();
				}
				return super.getText(element);
			}
        };
        ListSelectionDialog dialog = new ListSelectionDialog(getShell(), cqs,
        			new ArrayContentProvider(), lp,
        			"Select conceptual queries to be performed.");

        dialog.setTitle(FerretPlugin.compactPrettyPrint(objects));
        if(dialog.open() != Window.OK) { return; }
        Set<IConceptualQuery> selected = new HashSet<IConceptualQuery>();
        for(Object o : dialog.getResult()) {
	        	if(o instanceof IConceptualQuery) {
	        		selected.add((IConceptualQuery)o);
	        	}
        }
        c.setConceptualQueries(selected.toArray(new IConceptualQuery[selected.size()]));
        performQuery(c);
	}
}
