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
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.model.SphereHelper;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.TypesConversionManager;
import com.google.common.base.Preconditions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class JdtSphereHelper extends SphereHelper {
    protected static JdtSphereHelper singleton;
    protected JavaElementLabelProvider javaMinimalLabelProvider;
    protected JavaElementLabelProvider javaLabelProvider;
	protected JavaElementLabelProvider javaPostQualifiedLabelProvider;
    
    protected JdtSphereHelper() {}
    
    public static void shutdown() {
		if (singleton != null) {
			FerretPlugin.getDefault().dropSphereHelper(singleton);
			singleton.stop();
			singleton = null;
        }
    }

    public static SphereHelper getDefault() {
		if(singleton == null) {
			 singleton = new JdtSphereHelper();
			 singleton.start();
		}
		return singleton;
    }
    
    public void start() {
    	if(javaMinimalLabelProvider == null) {
    		javaMinimalLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_BASICS);
	//      javaMinimalLabelProvider.turnOff(JavaElementLabelProvider.SHOW_OVERLAY_ICONS);
	        javaMinimalLabelProvider.turnOn(JavaElementLabelProvider.SHOW_TYPE);
	        javaMinimalLabelProvider.turnOff(JavaElementLabelProvider.SHOW_PARAMETERS);
	        javaMinimalLabelProvider.turnOff(JavaElementLabelProvider.SHOW_SMALL_ICONS);
    	}
    	if(javaLabelProvider == null) {
    		javaLabelProvider = new JavaElementLabelProvider();
	//        javaLabelProvider.turnOff(JavaElementLabelProvider.SHOW_OVERLAY_ICONS);
	        javaLabelProvider.turnOn(JavaElementLabelProvider.SHOW_TYPE);
	        javaLabelProvider.turnOff(JavaElementLabelProvider.SHOW_PARAMETERS);
	        javaLabelProvider.turnOff(JavaElementLabelProvider.SHOW_SMALL_ICONS);
	//        javaLabelProvider.turnOn(JavaElementLabelProvider.SHOW_QUALIFIED);
    	}
		if (javaPostQualifiedLabelProvider == null) {
			javaPostQualifiedLabelProvider = new JavaElementLabelProvider();
			// javaPostQualifiedLabelProvider.turnOff(JavaElementLabelProvider.SHOW_OVERLAY_ICONS);
			javaPostQualifiedLabelProvider.turnOn(JavaElementLabelProvider.SHOW_TYPE);
			javaPostQualifiedLabelProvider.turnOff(JavaElementLabelProvider.SHOW_PARAMETERS);
			javaPostQualifiedLabelProvider.turnOff(JavaElementLabelProvider.SHOW_SMALL_ICONS);
			javaPostQualifiedLabelProvider.turnOn(JavaElementLabelProvider.SHOW_QUALIFIED);
			javaPostQualifiedLabelProvider.turnOn(JavaElementLabelProvider.SHOW_POST_QUALIFIED);
		}
    }
    
    public void reset() {
    	JavaModelHelper.getDefault().reset();
    }
    public void stop() {
    	JavaModelHelper.getDefault().stop();
    	if(javaMinimalLabelProvider != null) { javaMinimalLabelProvider.dispose(); }
    	javaMinimalLabelProvider = null;
    	if(javaLabelProvider != null) { javaLabelProvider.dispose(); }
    	javaLabelProvider = null;
    	if(singleton == this) { singleton = null; }
    }

	@SuppressWarnings("restriction") // JavaEditor, SelectionConverter
	public Object[] getSelectedObjects(IEditorPart editor) {
    	if(editor instanceof JavaEditor) {
	        try {
	            return SelectionConverter.getStructuredSelection(editor).toArray();
	        } catch (JavaModelException e) {
	        	return null; 
        	}
        }


        ISelectionProvider provider = editor.getSite().getSelectionProvider();
		if (provider == null) { return null; }
		ISelection selection = provider.getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection ts = (ITextSelection) selection;
			if (ts.getLength() > 0
					|| (ts = expandTextSelection(editor, ts, JdtSphereHelper::isJavaIdentifier)) != null) {
				Preconditions.checkNotNull(ts);
				String text = ts.getText();
				IJavaElement javaContainer = getJavaContainer(editor);
				if (javaContainer != null) {
					IJavaProject javaProject = javaContainer instanceof IJavaProject ? (IJavaProject) javaContainer
							: javaContainer.getJavaProject();
					String sourceCompatibilityLevel = javaProject.getOption(JavaCore.COMPILER_SOURCE, true);
					String sourceComplianceLevel = javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);

					// try resolving as a type
					IStatus isType = JavaConventions.validateJavaTypeName(text, sourceCompatibilityLevel,
							sourceComplianceLevel);
					if (isType.isOK()) {
						IType type = JavaModelHelper.getDefault().resolveType(text, javaContainer);
						if (type != null) {
							return new Object[] { type };
						}
					}

					// try resolving as a package
					IStatus isPackage = JavaConventions.validatePackageName(text, sourceCompatibilityLevel,
							sourceComplianceLevel);
					if (isPackage.isOK()) {
						IPackageFragment pkg = JavaModelHelper.getDefault().resolvePackage(text, javaContainer);
						if (pkg != null) {
							return new Object[] { pkg };
						}
					}
				}
			}
		}
        return null;
    }
    
	private IJavaElement getJavaContainer(IEditorPart editor) {
		IJavaElement container = editor.getAdapter(IJavaElement.class);
		if (container != null) {
			return container;
		}

		IEditorInput input = editor.getEditorInput();
		if ((container = TypesConversionManager.getAdapter(input, IJavaElement.class, Fidelity.Exact)) != null) {
			return container;
		}
		if (input instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) input).getFile();
			container = JavaCore.create(file);
			if (container != null) {
				return container;
			}
			container = JavaCore.create(file.getProject());
			if (container != null) {
				return container;
			}
		}

		IFile file = TypesConversionManager.getAdapter(input, IFile.class, Fidelity.Exact);
		if (file != null) {
			container = JavaCore.create(file);
			if (container != null) {
				return container;
			}
			container = JavaCore.create(file.getProject());
			if (container != null) {
				return container;
			}
		}

		IProject project = TypesConversionManager.getAdapter(input, IProject.class, Fidelity.Exact);
		if (project != null && (container = JavaCore.create(project)) != null) {
			return container;
		}
		return null;
	}

	protected static boolean isJavaIdentifier(Character ch) {
    	return Character.isLetterOrDigit(ch) || ch == '.';
    }
    
    @Override
	public boolean isCommonElement(Object o) {
    	// FIXME: Should make this configurable?  Like TPTP's filter list (which
    	// supports both inclusion and exclusion)?
    	if(o instanceof IType) {
    		return ((IType)o).getFullyQualifiedName().startsWith("java");
    	} else if(o instanceof IMember) {
    		return isCommonElement(((IMember)o).getDeclaringType());
    	}
    	return false;
	}

	public ImageDescriptor getImage(Object element) {
    	/* Weird: I just can't seem to figure out the right combination
    	 * of flags to get proper images; so return null, so that the IWorkbenchAdapter
    	 * is used instead. */
        // return javaLabelProvider.getImage(element);
    	return null;
    }
    
    public String getMinimalLabel(Object object) {
        if(!(object instanceof IJavaElement)) {
        	return null; 
        } else if(object instanceof ITypeHierarchy) {
        	return "type hierarchy for " + getMinimalLabel(((ITypeHierarchy)object).getType());
        }
        return  javaMinimalLabelProvider.getText(object);
    }

    public String getLabel(Object object) {
        if(object instanceof IMethod) {
            IMethod m = (IMethod)object;
			String methodLabel = javaMinimalLabelProvider.getText(m.getDeclaringType()) + "."
					+ javaMinimalLabelProvider.getText(m);
			try {
				IType type = m.getDeclaringType();
				if (type.isAnonymous()) {
					return type.getTypeQualifiedName() + " - " + methodLabel;
				}
			} catch (JavaModelException e) {
				// ignore
			}

			return methodLabel;
        } else if(object instanceof IField) {
            IField f = (IField)object;
            return javaMinimalLabelProvider.getText(f.getDeclaringType())+ "." 
                + javaMinimalLabelProvider.getText(f);
		} else if (object instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot pfr = (IPackageFragmentRoot) object;
			if(pfr.isExternal()) {
				return pfr.getElementName() + " - " + javaPostQualifiedLabelProvider.getText(pfr);
			} else {
				// qualified doesn't show relative path
				return pfr.getPath().makeRelative().toOSString();
			}
		} else if (object instanceof IType) {
			try {
				IType type = (IType) object;
				if (type.isAnonymous()) {
					return type.getTypeQualifiedName() + " - " + javaLabelProvider.getText(type);
				}
			} catch (JavaModelException e) {
				// ignore
			}
			return javaLabelProvider.getText(object);
        } else if(object instanceof IJavaElement) {
        	return javaLabelProvider.getText(object);
        } else if(object instanceof IJavaStackFrame) {
        	IJavaStackFrame frame = (IJavaStackFrame)object;
        	StringBuffer result = new StringBuffer();
	        	try {
	        		result.append(Signature.getSimpleName(frame.getReceivingTypeName()));
	        		if(!frame.getReceivingTypeName().equals(frame.getDeclaringTypeName())) {
	        			result.append('(');
	        			result.append(Signature.getSimpleName(frame.getDeclaringTypeName()));
	        			result.append(')');
	        		}
	        		result.append('.');
	        		result.append(frame.getMethodName());
	        		result.append(Signature.getParameterCount(frame.getSignature()) == 0
	        				? "()" : "(...)");
	        		return result.toString();
	        	} catch(DebugException e) {
	        		return frame.toString();
	        	}
        } else if(object instanceof ITypeHierarchy) {
        	return "type hierarchy for " + getLabel(((ITypeHierarchy)object).getType());
        }
        return null;
    }

    public boolean canOpen(Object obj) {
    	if(obj instanceof IMember || obj instanceof ITypeRoot) { return true; }
    	return TypesConversionManager.getAdapter(obj, IMember.class, Fidelity.Approximate) != null;
    }
    
    public boolean openObject(Object object) {
    	IJavaElement element = null;
        if(object instanceof IJavaElement) {
                element = (IJavaElement)object;
        } else {
        	element = (IJavaElement)TypesConversionManager.getAdapter(object, IMember.class, Fidelity.Approximate);
        }
        if(element != null) {
        	try {
                IEditorPart editor = JavaUI.openInEditor(element);
                JavaUI.revealInEditor(editor, element);
                return true;
            } catch(CoreException e) { /* ignore */ }
        }
        return false;
    }

    @Override
	public ISphereFactory[] getSphereFactories() {
		return new ISphereFactory[] { new JdtSphereFactory() };
	}

	@Override
	public String getHandleIdentifier(Object object) {
		if(object instanceof IJavaElement) {
			return "java:" + ((IJavaElement)object).getHandleIdentifier(); 
		}
		return super.getHandleIdentifier(object);
	}
	
	public Object getParent(Object object) {
		if(object instanceof IMember) {
			if(object instanceof IType) {
				return JavaModelHelper.getDefault().getSuperclass((IType)object, new NullProgressMonitor());
			}
			return ((IMember)object).getParent();
		}
		return null;
	}
}
