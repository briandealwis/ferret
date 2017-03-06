package ca.ubc.cs.ferret;

import ca.ubc.cs.ferret.model.SphereHelper;
import ca.ubc.cs.ferret.preferences.IFerretPreferenceConstants;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.TypesConversionManager;
import ca.ubc.cs.ferret.util.JobManager;
import ca.ubc.cs.ferret.views.DossierConstants;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class FerretPlugin extends AbstractUIPlugin implements IRegistryChangeListener {

	/**
	 * This plug-in's ID
	 */
	public final static String pluginID = "ca.ubc.cs.ferret";

	/**
	 * The extension point id for providing conceptual queries.
	 */
	public final static String conceptualQueriesExtensionPointId = "cqs";
	
	/**
	 * Extension point id for sphere helper definitions.
	 */
    public final static String sphereHelpersExtensionPointId = "sphereHelpers";
	private static final String SPHERE_HELPER_ELEMENT = "sphereHelper";
    
    /**
     * Extension point id for sphere composition functions.
     */
	public final static String scfsExtensionPointId = "scfs";

	/**
	 * Command id for query-ferret for selected objects.
	 */
	public final static String commandQueryWithFerret =
		"ca.ubc.cs.ferret.commands.askFerret";

	/**
	 * Command id for expand-top-level-conceptual-queries.
	 */
	public static final String commandExpandCQs = "ca.ubc.cs.ferret.commands.expandCQs";
    
	private static FerretPlugin plugin;       // The shared instance.

	private ResourceBundle resourceBundle; // Resource bundle.
	
    // FIXME: Hook registry-changed events to rebuild <sphereHelpers> as necessary
    protected SphereHelper[] sphereHelpers = null;
    
    protected JobManager jobManager;

	public FerretPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("ca.ubc.cs.ferret.FerretPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

    /**
     * Returns the shared instance.
     */
    public static FerretPlugin getDefault() {
        return plugin;
    }
    
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
    RegistryFactory.getRegistry().addRegistryChangeListener(this, pluginID);
//        Consultancy.getDefault().activate();
		if(getPreferenceStore().contains(IFerretPreferenceConstants.PREF_DOSSIER_FONT)) {
			FontData fontData[] = PreferenceConverter.getFontDataArray(getPreferenceStore(), IFerretPreferenceConstants.PREF_DOSSIER_FONT);
			if(fontData != PreferenceConverter.FONTDATA_ARRAY_DEFAULT_DEFAULT) {
				JFaceResources.getFontRegistry().put(IFerretPreferenceConstants.PREF_DOSSIER_FONT,
						fontData);
			}
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		if(plugin == this) {
			plugin = null;
		}
		RegistryFactory.getRegistry().removeRegistryChangeListener(this);
        Consultancy.shutdown();
        // getSpheres() may recreate the sphereHelpers, causing problems on
        // shutdown.  Downstream plugins are responsible for shutting down
        // their sphereHelpers in their respective stop() methods.
		// for(SphereHelper s : getSpheres()) { s.stop(); }
		sphereHelpers = null;
		if(jobManager != null) {
			jobManager.shutdown();
			jobManager = null;
		}
		super.stop(context);
	}

	public JobManager getJobManager() {
		if(jobManager == null) {
			jobManager = new JobManager("Ferret Job Manager");
			jobManager.reset();
		}
		return jobManager;
	}

	public static void log(IStatus status) {
		if(plugin == null) { return; }
		plugin.getLog().log(status);
	}
    
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		if(plugin == null) { return key; }
		ResourceBundle bundle = plugin.getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public IDialogSettings getDialogSettings(String sectionName) {
		IDialogSettings sub = getDialogSettings().getSection(sectionName);
		if(sub == null) { sub = getDialogSettings().addNewSection(sectionName); }
		return sub;
	}

    public static String compactPrettyPrint(ISelection selection) {
    	if(selection == null) {
    		return "<<null selection>>";
    	} else if(selection instanceof IStructuredSelection) {
            return compactPrettyPrint(((IStructuredSelection)selection).toArray());
        } else if(selection instanceof ITextSelection) {
            ITextSelection ts = (ITextSelection)selection;
            return "[" + ts.getStartLine() + "-" + ts.getEndLine() + ": offset=" + ts.getOffset() + " length=" + 
                ts.getLength() + "]{" + ts.getText() + "}";
        }
        return selection.toString();
    }

    public static String compactPrettyPrint(Object[] objects) {
        StringBuffer desc = new StringBuffer();
        for(int i = 0; i < objects.length; i++) {
            desc.append(compactPrettyPrint(objects[i]));
            if(i < objects.length - 1) { desc.append(','); }
        }
        return desc.toString();
    }

    public static String compactPrettyPrint(Object object) {
    	if(object == null) { return "(null)"; }
    	if(object instanceof Object[]) { return compactPrettyPrint((Object[])object); }
        String label;
        for(SphereHelper sphere : getSphereHelpers()) {
            if((label = sphere.getMinimalLabel(object)) != null && label.length() > 0) {
                return label;
            }
        }
        IWorkbenchAdapter wa = getAdapter(object, IWorkbenchAdapter.class);
        if(wa != null && (label = wa.getLabel(object)) != null) { return label; } 
        return object.toString();
    }

    public static String prettyPrint(Object[] objects) {
        StringBuffer desc = new StringBuffer();
        for(int i = 0; i < objects.length; i++) {
            desc.append(prettyPrint(objects[i]));
            if(i < objects.length - 1) { desc.append(','); }
        }
        return desc.toString();
    }


    public static String prettyPrint(Object object) {
    	if(object == null) { return "(null)"; }
    	if(object instanceof Object[]) { return prettyPrint((Object[])object); }
    	if(object instanceof Collection<?>) { 
    		return prettyPrint(((Collection<?>)object).toArray());
    	}
        String label;
        for(SphereHelper sphere : getSphereHelpers()) {
            if((label = sphere.getLabel(object)) != null && label.length() > 0) { 
                return label;
            }
        }
        IWorkbenchAdapter wa = getAdapter(object, IWorkbenchAdapter.class);
        if(wa != null && (label = wa.getLabel(object)) != null) { return label; } 
        return object.toString();
    }

    public static ImageDescriptor getImage(Object object) {
    	if(object == null) { return null; }
        for(SphereHelper sphere : getSphereHelpers()) {
            ImageDescriptor id = sphere.getImage(object);
            if(id != null) { return id; }
        }
        IWorkbenchAdapter wa = getAdapter(object, IWorkbenchAdapter.class);
        if(wa != null) {
            ImageDescriptor id = wa.getImageDescriptor(object);
            if(id != null) { return id; } 
        }
        return null;
    }

    public static String getHandleIdentifier(Object object) {
    	if(object == null) { return "(null)"; }
    	if(object instanceof Object[]) { return getHandleIdentifier((Object[])object); }
        String label;
        for(SphereHelper sphere : getSphereHelpers()) {
            if((label = sphere.getHandleIdentifier(object)) != null && label.length() > 0) {
                return label;
            }
        }
        // Could do a getAdapter(IHandleIdentifer.class) or something to that effect
//        IWorkbenchAdapter wa = getAdapter(object, IWorkbenchAdapter.class);
//        if(wa != null && (label = wa.getLabel(object)) != null) { return label; } 
        return "unknown:" + compactPrettyPrint(object);
    }

    public static String getHandleIdentifier(Object[] objects) {
        StringBuffer desc = new StringBuffer();
        for(int i = 0; i < objects.length; i++) {
            desc.append(getHandleIdentifier(objects[i]));
            if(i < objects.length - 1) { desc.append(','); }
        }
        return desc.toString();
    }


    public static void log(Throwable e) {
        log(new Status(IStatus.INFO, pluginID, FerretErrorConstants.EXCEPTION_HANDLED, 
                "An exception occurred but was handled", e));
    }


//	public static boolean isActivated() {
//		return PlatformUI.getPreferenceStore()
//				.getString(IPreferenceConstants.PLUGINS_NOT_ACTIVATED_ON_STARTUP)
//				.indexOf(pluginID) >= 0;
//	}

    public static SphereHelper[] getSphereHelpers() {
        if(plugin == null) {
            return new SphereHelper[0];
        }
        return plugin.basicGetSphereHelpers();
    }
	
    protected SphereHelper[] basicGetSphereHelpers() {
        if(sphereHelpers != null) { return sphereHelpers; }
        
        List<SphereHelper> foundSphereHelpers = new ArrayList<SphereHelper>();
        for(IConfigurationElement element : 
        		RegistryFactory.getRegistry().getConfigurationElementsFor(pluginID, sphereHelpersExtensionPointId)) {
        	if(!element.getName().equals(SPHERE_HELPER_ELEMENT)) {
        		FerretPlugin.log(new Status(IStatus.ERROR, element.getNamespaceIdentifier(),
        				FerretErrorConstants.CONFIGURATION_ERROR,
        				"invalid configuration element named " + element.getName() + " for extension point " +
        				sphereHelpersExtensionPointId, null));
        		continue;
        	}
        	try {
        		// support enablement expressions?
//      		Expression expression= ExpressionConverter.getDefault().perform(enablementElement);
        		if(FerretPlugin.hasDebugOption("debug/showExtensionQueries")) {
        			System.out.println("getSpheres(): creating instance for " + element.getNamespaceIdentifier() + ":" +
        					element.getName() + " => " + element.getAttribute("class"));
        		}
        		SphereHelper created = (SphereHelper)element.createExecutableExtension("class");
        		if(FerretPlugin.hasDebugOption("debug/showExtensionQueries")) {
        			System.out.println("Created " + created);
        		}
        		foundSphereHelpers.add(created);
        	} catch(ClassCastException e) {
        		if(FerretPlugin.hasDebugOption("debug/showExtensionQueries")) {
        			System.out.println("EXCEPTION: " + e);
        		}
        		FerretPlugin.log(new Status(IStatus.ERROR,
        				element.getNamespaceIdentifier(),
        				FerretErrorConstants.CONTRACT_VIOLATION,
        				"sphere class does not inherit from "
        				+ SphereHelper.class.getName(), e));
        	} catch(CoreException e) {
        		if(FerretPlugin.hasDebugOption("debug/showExtensionQueries")) {
        			System.out.println("EXCEPTION: " + e);
        		}
        		FerretPlugin.log(e.getStatus());
        	}
        }
  
        return sphereHelpers = foundSphereHelpers.toArray(new SphereHelper[foundSphereHelpers.size()]);
//        Arrays.sort(sphereHelpers, new Comparator<SphereHelper>() {
//			public int compare(SphereHelper o1, SphereHelper o2) {
//				return o1.getClass().getName().compareTo(o2.getClass().getName());
//			}});
    }
    

    /**
     * Return true if this plug-in has debugging enabled and the provided option enabled. 
     * @param optionName the option name (will be prefixed by this plugin id)
     * @return true if the debugging option is enabled
     */
    public static boolean hasDebugOption(String optionName) {
        return hasDebugOption(pluginID, optionName);
    }
    
    /**
     * Return true if this plug-in has debugging enabled and the provided option enabled. 
     * @param optionName the option name
     * @return true if the debugging option is enabled
     */
    public static boolean hasDebugOption(String id, String optionName) {
        if(plugin == null || !plugin.isDebugging()) { return false; }
        String value = Platform.getDebugOption(id + "/" + optionName);
        if(value == null) { return false; }
        return value.equals("true") || value.equals("TRUE") || value.equals("yes") || value.equals("1");
    }

	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta deltas[] = event.getExtensionDeltas(pluginID, sphereHelpersExtensionPointId);
		if(deltas == null || deltas.length == 0) { return; }
		rebuildSphereHelpers();
	}
	
	public void rebuildSphereHelpers() {
		sphereHelpers = null;
	}

	public void dropSphereHelper(SphereHelper sph) {
		if(sphereHelpers == null) { return; }
		for(int i = 0; i < sphereHelpers.length; i++) {
			if(sphereHelpers[i] == sph) {
				sphereHelpers = arraycopyExcept(sphereHelpers,
						new SphereHelper[sphereHelpers.length - 1], i);
				return;
			}
		}
	}

	/**
	 * Helper function: copy all entries from <TT>previousArray</TT> to 
	 * <TT>newArray</TT> except for the entry at <TT>exceptIndex</TT>.
	 * @param <T> the type of the arrays
	 * @param previousArray the source array
	 * @param newArray the new array
	 * @param droppedIndex the index of the item in previousArray to be dropped
	 * @return newArray
	 */
	public static <T> T[] arraycopyExcept(T[] previousArray, T[] newArray, int droppedIndex) {
		System.arraycopy(previousArray, 0, newArray, 0, droppedIndex);
		if(previousArray.length - droppedIndex - 1 > 0) {
			System.arraycopy(previousArray, droppedIndex + 1, newArray, droppedIndex, 
					previousArray.length - droppedIndex - 1);
		}
		return newArray;
	}
	
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(IFerretPreferenceConstants.PREF_AUTO_EXPAND, false);
		store.setDefault(IFerretPreferenceConstants.PREF_MAX_BACKGROUND_COUNT, 5);
		store.setDefault(IFerretPreferenceConstants.PREF_SHOW_FACTS, true);
		store.setDefault(IFerretPreferenceConstants.PREF_SHOW_HEADER, true);
		store.setDefault(IFerretPreferenceConstants.PREF_SHOW_EMPTY_CQS, false);
		store.setDefault(IFerretPreferenceConstants.PREF_SHOW_ONLY_COMPLETED_CQS, false);
		store.setDefault(IFerretPreferenceConstants.PREF_RESPOND_TO_SELECTIONS, true);
		store.setDefault(IFerretPreferenceConstants.PREF_BACKGROUND_JOBS_AS_USER, false);
		store.setDefault(IFerretPreferenceConstants.PREF_VERBOSE_JOB_TITLES, false);
		store.setDefault(IFerretPreferenceConstants.PREF_MIN_ELEMENTS_FOR_CLUSTERING, 5);
		store.setDefault(IFerretPreferenceConstants.PREF_RECENT_CONSULTATIONS_LIMIT, 10);
		store.setDefault(IFerretPreferenceConstants.PREF_HISTORY_RESTORE_NAVIGATION_TO_QUERY_POINT, true);
		store.setDefault(IFerretPreferenceConstants.PREF_SOLUTION_RESTORE_NAVIGATION_TO_QUERY_POINT, false);
		store.setDefault(IFerretPreferenceConstants.PREF_HONOUR_OPEN_PREFERENCE, true);
		store.setDefault(IFerretPreferenceConstants.PREF_REISSUE_CURRENT_QUERY_ON_CHANGE, true); 
		store.setDefault(IFerretPreferenceConstants.PREF_CACHE_TIMEOUTS, 30); 
		// This shouldn't be necessary -- should it?
//		Font font= JFaceResources.getTextFont();
//		if (font != null) {
//			FontData[] data= font.getFontData();
//			if (data != null && data.length > 0) {
//				PreferenceConverter.setDefault(store,
//						IFerretPreferenceConstants.PREF_DOSSIER_FONT, data[0]);
//			}		
//		}
	}

	public static boolean shouldSupportBackgroundRelatedQueries() {
		return getMaximumBackgroundCount() > 0;
	}

	public static boolean shouldRespondToUserSelections() {
		return getDefault().getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_RESPOND_TO_SELECTIONS);
	}

	public static boolean shouldHonourOpenPreference() {
		return getDefault().getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_HONOUR_OPEN_PREFERENCE);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(DossierConstants.IMG_QUERY,
				imageDescriptorFromPlugin(pluginID, "icons/query.png"));
		reg.put(DossierConstants.IMG_QUERY_IN_PROGRESS,
				imageDescriptorFromPlugin(pluginID, "icons/query-busy.png"));
		/*
		 * Sadly this didn't work: the composited image had big blocky chunks
		 * for some reason.  So we manually composited the images by hand
		 * into query-busy.png.  Perhaps it was the PNG?  But using GIF
		 * produced a horrible-looking, blocky image.  Sigh.
		 * reg.put(DossierConstants.IMG_HOURGLASS,
		 * 		imageDescriptorFromPlugin(pluginID, "icons/hourglass.png"));
		 * ImageDescriptor [][] overlays = new ImageDescriptor[1][1];
		 * overlays[0][0] = reg.getDescriptor(DossierConstants.IMG_HOURGLASS);
		 * reg.put(DossierConstants.IMG_QUERY_IN_PROGRESS,
		 * 	new OverlayIcon(reg.getDescriptor(DossierConstants.IMG_QUERY),
		 * 	overlays));
		 */
	}

	public static Object fetchField(Object instance, String fieldName) {
	    try {
	        Field field = instance.getClass().getDeclaredField(fieldName);
	        field.setAccessible(true);
	        return field.get(instance);
	    } catch (Exception ex) {
	        log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 1,  
	                "exception while reflectively getting field '" + fieldName + "' in "
	                    + instance.getClass() + "." + fieldName,  ex));
	        return null;
	    }
	}

	public static void setField(Object instance, String fieldName, Object value) {
		Class<?> current = instance.getClass();
		while(current != null) {
		    try {
		        Field field;
		        try {
		        	field = current.getDeclaredField(fieldName);
		        } catch(NoSuchFieldException e) {
		        	field = current.getField(fieldName);
		        }
		        field.setAccessible(true);
		        field.set(instance, value);
		        return;
		    } catch (Exception ex) {
		    	// we continue
		    }
		    current = current.getSuperclass();
		}
        log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 1,  
                "exception while reflectively getting field '" + fieldName + "' in "
                    + instance.getClass() + "." + fieldName,  null));
	}


	public static boolean skipEmptyCQs() {
		return !getDefault().getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_SHOW_EMPTY_CQS);
	}

	public static boolean showOnlyCompletedCQs() {
		return getDefault().getPreferenceStore().getBoolean(IFerretPreferenceConstants.PREF_SHOW_ONLY_COMPLETED_CQS);
	}

	public static int getMaximumBackgroundCount() {
		return getDefault().getPreferenceStore().getInt(IFerretPreferenceConstants.PREF_MAX_BACKGROUND_COUNT);
	}
	
	public static int getRecentConsultationCount() {
		return getDefault().getPreferenceStore().getInt(IFerretPreferenceConstants.PREF_RECENT_CONSULTATIONS_LIMIT);
	}

	public static int getMinimumElementsForClustering() {
		return getDefault().getPreferenceStore().getInt(IFerretPreferenceConstants.PREF_MIN_ELEMENTS_FOR_CLUSTERING);
	}

	public static int getCacheTimeouts() {
		return getDefault().getPreferenceStore().getInt(IFerretPreferenceConstants.PREF_CACHE_TIMEOUTS);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAdapter(Object object, Class<T> clazz) {
        if(clazz.isInstance(object)) { return (T)object; }
        if(object instanceof IAdaptable) {
        	T adapter = (T)((IAdaptable)object).getAdapter(clazz);
        	if(adapter != null) { return adapter; }
        }
        return TypesConversionManager.getAdapter(object, clazz, Fidelity.Approximate);
	}


	public static Object invokeMethod(Object instance, String methodName,
            Class<?> argTypes[], Object arguments[]) {
        Class<?> clas = instance.getClass();
        try {
        	Method method = null;
        	try {
        		method = clas.getMethod(methodName, argTypes);
        	} catch(NoSuchMethodException e) {
        		method = clas.getDeclaredMethod(methodName, argTypes);
        	}
            method.setAccessible(true);
            return method.invoke(instance, arguments);
        } catch (Exception ex) {
            log(new Status(IStatus.ERROR, pluginID, 1,
                    "exception during reflective invocation of "
                            + clas.getName() + "." + methodName, ex));
            return null;
        }
    }

	public static void logHandledException(Exception e) {
		FerretPlugin.log(new Status(IStatus.INFO, pluginID,
				FerretErrorConstants.EXCEPTION_HANDLED, "handled unexpected exception", e));
	}

	public static String debugPrint(Object[] objects) {
		StringBuffer desc = new StringBuffer();
		for(int i = 0; i < objects.length; i++) {
			desc.append(debugPrint(objects[i]));
			if(i < objects.length - 1) { desc.append(','); }
		}
		return desc.toString();
	}
	
	public static String debugPrint(Object object) {
    	if(object instanceof FerretObject) { 
    		return ((FerretObject)object).toString();
    	}

		return prettyPrint(object) + " (" + object.getClass().getName() + ")";
	}

	public static Object getParent(Object object) {
        Object parent;
        for(SphereHelper sphere : getSphereHelpers()) {
            if((parent = sphere.getParent(object)) != null) {
                return parent;
            }
        }
        IWorkbenchAdapter wa = getAdapter(object, IWorkbenchAdapter.class);
        if(wa != null && (parent = wa.getParent(object)) != null) { return parent; } 
		return null;
	}

	public void setHelp(Control control, String helpContextId) {
		getWorkbench().getHelpSystem().setHelp(control, helpContextId);		
	}

	public static ImageDescriptor getImageDescriptor(String fileName) {
		return imageDescriptorFromPlugin(pluginID, fileName);
	}

	/**
	 * Ask sphereHelpers whether this is considered to be a common element (e.g.,
	 * an element that is not really of interest to developers). 
	 * @param o the object to check
	 * @return true if reported as a common element
	 */
	public static boolean isCommonElement(Object o) {
   		for(SphereHelper s : getSphereHelpers()) {
   			if(s.isCommonElement(o)) { return true; }
		}
		return false;
	}

}
