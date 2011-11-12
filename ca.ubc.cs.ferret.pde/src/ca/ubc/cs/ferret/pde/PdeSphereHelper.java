/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.pde;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Predicate;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.ISharedPluginModel;
import org.eclipse.pde.internal.core.plugin.PluginReference;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.AbstractSphereFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.Sphere;
import ca.ubc.cs.ferret.model.SphereHelper;
import ca.ubc.cs.ferret.pde.relations.AdaptableFromRelation;
import ca.ubc.cs.ferret.pde.relations.AdaptableToRelation;
import ca.ubc.cs.ferret.pde.relations.PdeExtensionPointExtensions;
import ca.ubc.cs.ferret.pde.relations.PdeExtensionsExtensionPoint;
import ca.ubc.cs.ferret.pde.relations.PdeIdentifierReferencedRelation;
import ca.ubc.cs.ferret.pde.relations.PdePluginDeclaredExtensionPoints;
import ca.ubc.cs.ferret.pde.relations.PdePluginDeclaredExtensions;
import ca.ubc.cs.ferret.pde.relations.PdeTypesReferencedRelation;
import ca.ubc.cs.ferret.references.FileReference;
import ca.ubc.cs.ferret.references.ZipEntryReference;
import ca.ubc.cs.ferret.views.ImageImageDescriptor;

public class PdeSphereHelper extends SphereHelper {

	public static final String HCI_PDE_TB = "ca.ubc.cs.ferret.pde.tb";
	public static final String OP_DECLARED_EXTENSION_POINTS = "PdeDeclaredExtensionPoints";
	public static final String OP_DECLARED_EXTENSIONS = "PdeDeclaredExtensions";
	public static final String OP_IDENTIFIER_REFERENCED = "PdeIdentifierReferenced";
	public static final String OP_EXTENDED_BY = "PdeExtendedBy";
	public static final String OP_EXTENDS = "PdeExtends";
	public static final String OP_ADAPTABLE_TO = "PdeAdaptableTo";
	public static final String OP_ADAPTABLE_FROM = "PdeAdaptableFrom";
	protected static PdeSphereHelper singleton;

    private PdeSphereHelper() {}
    
    public static PdeSphereHelper getDefault() {
		if(singleton == null) {
			 singleton = new PdeSphereHelper();
			 singleton.start();
		}
		return singleton;
    }
    
    public void start() {}
    public void reset() {}
    public void stop() {
    	PdeModelHelper.stop();
    }

    @Override
    public ImageDescriptor getImage(Object element) {
    	Image img = PDEPlugin.getDefault().getLabelProvider().getImage(element);
    	return img != null ? new ImageImageDescriptor(img) : null;
    }

    @Override
    public String getLabel(Object element) {
    	if(element instanceof IPluginExtensionPoint) {
    		return PdeModelHelper.getDefault().getFullId((IPluginExtensionPoint)element);
    	} else if(element instanceof IPluginExtension) {
    		// stolen from PluginSearchResultPage
			IPluginExtension extension = (IPluginExtension)element;
			return extension.getPoint() + " - " + 
				PdeModelHelper.getDefault().getPluginId(extension.getPluginModel()); //$NON-NLS-1$
    	} else if(element instanceof ISharedPluginModel) {
    		return PdeModelHelper.getDefault().getPluginId((ISharedPluginModel)element);
    	}
    	return getMeaningfulLabel(element, 
    			PDEPlugin.getDefault().getLabelProvider().getText(element));
    }

    @Override
    public String getMinimalLabel(Object element) {
        return getLabel(element);
    }

    public String getHandleIdentifier(Object element) {
    	String pdeLabel = getLabel(element);
    	if(pdeLabel != null) { return "pde:" + pdeLabel; }
    	return super.getHandleIdentifier(element);
    }
    
    @Override
    public Object[] getSelectedObjects(IEditorPart editor) {
    	ISelectionProvider provider = editor.getSite().getSelectionProvider();
		if (provider == null) { return null; }
		ISelection selection = provider.getSelection();
		if(selection instanceof ITextSelection) {
			ITextSelection ts = (ITextSelection)selection;
			if(ts.getLength() > 0 || (ts = expandTextSelection(editor, ts)) != null) {
				return getSelectedObjects(ts);
			}
		}
		return null;
    }
    
	protected ITextSelection expandTextSelection(IEditorPart editor,
			ITextSelection ts) {
		return expandTextSelection(editor, ts, new Predicate<Character>() {
			public boolean evaluate(Character ch) {
				return Character.isLetterOrDigit(ch) || ch == '.';
			}});
	}

	protected Object[] getSelectedObjects(ITextSelection ts) {
		List<Object> objects = new ArrayList<Object>();
		IPluginModelBase modelBase = PdeModelHelper.getDefault().findPluginModel(ts.getText());
		if(modelBase != null) { objects.add(modelBase);  }
		IPluginExtensionPoint extPt = PdeModelHelper.getDefault().findExtensionPoint(ts.getText());
		if(extPt != null) { objects.add(extPt); }
		return objects.toArray();
	}

    public boolean canOpen(Object obj) {
    	if (obj instanceof PluginReference || obj instanceof IPluginObject
				|| obj instanceof IPluginModelBase) {
			return true;
		}
		return super.canOpen(obj);
    }
    
    @Override
    public boolean openObject(Object element) {
		if(element instanceof IPluginObject && ManifestEditor.open(element, false) != null) {
			return true;
		}
		IPluginModelBase model = null;
		if(element instanceof IPluginModelBase) {
			model = (IPluginModelBase)element;
		} else if(element instanceof PluginReference) {
    		model = ((PluginReference)element).getPlugin().getPluginModel();
    	}
		if(model != null) {
			String xmlFileName = model.isFragmentModel() ? "fragment.xml" : "plugin.xml";
			IPath path = null;
			// ExternalPluginModelBase.getLocalFile() is useful
			File f = new File(model.getInstallLocation());
			if(f.exists()) {
				if(f.isFile()) {
					// then ref is a .jar plugin
					try {
						ZipEntryReference jfr = new ZipEntryReference(f.getAbsolutePath(),
								xmlFileName, -1, -1);
						return jfr.open();
					} catch(IOException e) {
						return false;
					}
				} else if(f.isDirectory()) {
					path = Path.fromOSString(model.getInstallLocation());
					path = path.append(xmlFileName);
				}
			} else {
				path = model.getUnderlyingResource().getFullPath();
			}
			// Try to resolve the path as within the workspace: findFilesForLocation() works
			// for absolute files that reference files in the workspace
			IFile files[] = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
			for(IFile file : files) {
				if(file.exists()) {
					IWorkbenchPage p = FerretPdePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						if(IDE.openEditor(p, file, true) != null) { return true; }
					} catch (PartInitException e) {
						FerretPlugin.log(e);
					}
				}
			}
			// Open the file as an external-to-the-workspace file
			if(new FileReference(path).open()) { return true; }
		}
    	return super.openObject(element);
    }

	@Override
	public Object getParent(Object object) {
		if(object instanceof IPluginObject) {
			return ((IPluginObject)object).getPluginModel();
		}
		return null;
	}


	public ISphereFactory[] getSphereFactories() {
		return new ISphereFactory[] { new AbstractSphereFactory() {

			public String getId() {
				return getClass().getName();
			}

			public String getDescription() {
				return "Eclipse plugin-related queries (PDE)";
			}
			
			public IStatus canCreate() {
				return Status.OK_STATUS;
			}
			
			public ISphere createSphere(IProgressMonitor monitor) throws FerretConfigurationException {
				Sphere tb = new Sphere("Eclipse PDE target image information");
				tb.register(ObjectOrientedRelations.OP_TYPES_REFERENCED, new PdeTypesReferencedRelation());
				tb.register(OP_DECLARED_EXTENSION_POINTS, new PdePluginDeclaredExtensionPoints(),
						new PdeExtensionsExtensionPoint());
				tb.register(OP_DECLARED_EXTENSIONS, new PdePluginDeclaredExtensions(),
						new PdeExtensionPointExtensions());
				tb.register(OP_IDENTIFIER_REFERENCED, new PdeIdentifierReferencedRelation());
				tb.register(OP_EXTENDED_BY, new PdeExtensionPointExtensions());
				tb.register(OP_EXTENDS, new PdeExtensionsExtensionPoint());
				tb.register(OP_ADAPTABLE_FROM, new AdaptableFromRelation());
				tb.register(OP_ADAPTABLE_TO, new AdaptableToRelation());
				return tb;
			}

			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter) {
				return null;
			}

			public ImageDescriptor getImageDescriptor() {
				return FerretPdePlugin.getImageDescriptor("icons/pde-tb.gif");
			}

			public String getHelpContextId() {
				return HCI_PDE_TB;
			}}};

	}

}
