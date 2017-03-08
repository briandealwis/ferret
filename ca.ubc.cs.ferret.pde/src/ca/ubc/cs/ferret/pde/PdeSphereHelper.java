/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.pde;

import ca.ubc.cs.ferret.FerretConfigurationException;
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
import ca.ubc.cs.ferret.views.ImageImageDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.ISharedPluginModel;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.plugin.PluginReference;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.feature.FeatureEditor;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;

public class PdeSphereHelper extends SphereHelper {

	public static final String HCI_PDE_TB = "ca.ubc.cs.ferret.pde.tb";
	public static final String OP_DECLARED_EXTENSION_POINTS =
			"PdeDeclaredExtensionPoints";
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
			return extension.getPoint() + " - "
					+ PdeModelHelper.getDefault().getPluginId(extension.getPluginModel()); //$NON-NLS-1$
		} else if(element instanceof ISharedPluginModel) { return PdeModelHelper
				.getDefault().getPluginId((ISharedPluginModel)element); }
		return getMeaningfulLabel(element, PDEPlugin.getDefault().getLabelProvider()
				.getText(element));
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
		if(provider == null) { return null; }
		ISelection selection = provider.getSelection();
		if(selection instanceof ITextSelection) {
			ITextSelection ts = (ITextSelection)selection;
			if(ts.getLength() > 0 || (ts = expandTextSelection(editor, ts)) != null) { return getSelectedObjects(ts); }
		}
		return null;
	}

	protected ITextSelection expandTextSelection(IEditorPart editor, ITextSelection ts) {
		return expandTextSelection(editor, ts, ch -> Character.isLetterOrDigit(ch) || ch == '.');
	}

	protected Object[] getSelectedObjects(ITextSelection ts) {
		List<Object> objects = new ArrayList<Object>();
		IPluginModelBase modelBase =
				PdeModelHelper.getDefault().findPluginModel(ts.getText());
		if(modelBase != null) {
			objects.add(modelBase);
		}
		IPluginExtensionPoint extPt =
				PdeModelHelper.getDefault().findExtensionPoint(ts.getText());
		if(extPt != null) {
			objects.add(extPt);
		}
		return objects.toArray();
	}

	public boolean canOpen(Object obj) {
		if(obj instanceof PluginReference || obj instanceof IPluginObject
				|| obj instanceof IPluginModelBase || obj instanceof IFeatureModel) { return true; }
		return super.canOpen(obj);
	}

	@Override
	public boolean openObject(Object element) {
		if (element instanceof IPluginObject && ManifestEditor.open(element, false) != null) {
			return true;
		}
		if (element instanceof IPluginModelBase
				&& ManifestEditor.openPluginEditor((IPluginModelBase) element) != null) {
			return true;
		}
		if (element instanceof PluginReference
				&& ManifestEditor.openPluginEditor(((PluginReference) element).getPlugin().getPluginModel()) != null) {
			return true;
		}

		if (element instanceof BundleDescription
				&& ManifestEditor.openPluginEditor((BundleDescription) element) != null) {
			return true;
		}
		if (element instanceof IFeatureModel) {
			FeatureEditor.openFeatureEditor((IFeatureModel) element);
			return true;
		}
		return super.openObject(element);
	}

	@Override
	public Object getParent(Object object) {
		if(object instanceof IPluginObject) { return ((IPluginObject)object)
				.getPluginModel(); }
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

			public ISphere createSphere(IProgressMonitor monitor)
					throws FerretConfigurationException {
				Sphere tb = new Sphere("Eclipse PDE target image information");
				tb.register(ObjectOrientedRelations.OP_TYPES_REFERENCED,
						new PdeTypesReferencedRelation());
				tb.register(OP_DECLARED_EXTENSION_POINTS,
						new PdePluginDeclaredExtensionPoints(),
						new PdeExtensionsExtensionPoint());
				tb.register(OP_DECLARED_EXTENSIONS, new PdePluginDeclaredExtensions(),
						new PdeExtensionPointExtensions());
				tb.register(OP_IDENTIFIER_REFERENCED,
						new PdeIdentifierReferencedRelation());
				tb.register(OP_EXTENDED_BY, new PdeExtensionPointExtensions());
				tb.register(OP_EXTENDS, new PdeExtensionsExtensionPoint());
				tb.register(OP_ADAPTABLE_FROM, new AdaptableFromRelation());
				tb.register(OP_ADAPTABLE_TO, new AdaptableToRelation());
				return tb;
			}

			@SuppressWarnings("unchecked")
			public <T> T getAdapter(Class<T> adapter) {
				return null;
			}

			public ImageDescriptor getImageDescriptor() {
				return FerretPdePlugin.getImageDescriptor("icons/pde-tb.gif");
			}

			public String getHelpContextId() {
				return HCI_PDE_TB;
			}
		} };

	}

}
