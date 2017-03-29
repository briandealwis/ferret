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
package ca.ubc.cs.ferret.jdt.attributes;

import ca.ubc.cs.ferret.jdt.FerretJdtPlugin;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;

public class JavaElementTypeProvider 
		extends AbstractJavaAttributeValue<IJavaElement,String> {
	private static final String PACKAGE = "package";
	private static final String FILE = "file";
	private static final String PROJECT = "project";
	private static final String INITIALIZER = "initializer";
	private static final String FIELD = "field";
	private static final String CLASS = "class";
	private static final String INTERFACE = "interface";
	private static final String METHOD = "method";
	private static Map<String,String> jdtDescriptorMapping;
	
	static {
		jdtDescriptorMapping = new HashMap<String, String>();
		jdtDescriptorMapping.put(PACKAGE, ISharedImages.IMG_OBJS_PACKAGE);
		jdtDescriptorMapping.put(FILE, ISharedImages.IMG_OBJS_CUNIT);
		jdtDescriptorMapping.put(INITIALIZER, ISharedImages.IMG_OBJS_PRIVATE);
		jdtDescriptorMapping.put(FIELD, ISharedImages.IMG_FIELD_PUBLIC);
		jdtDescriptorMapping.put(CLASS, ISharedImages.IMG_OBJS_CLASS);
		jdtDescriptorMapping.put(INTERFACE, ISharedImages.IMG_OBJS_INTERFACE);
		jdtDescriptorMapping.put(METHOD, ISharedImages.IMG_OBJS_PUBLIC);
		// PROJECT is in Workbench's shared images
	}

	public String[] getCategories() {
		return null;
	}

	public String getCategory(IJavaElement object) {
		if(object instanceof IMethod) {
			return METHOD;
		} else if(object instanceof IType) {
			try {
				if(((IType)object).isInterface()) {
					return INTERFACE;
				} else {
					return CLASS;
				}
			} catch(JavaModelException e) {
				return CLASS;
			}
		} else if(object instanceof IField) {
			return FIELD;
		} else if(object instanceof IInitializer) {
			return INITIALIZER;
		} else if(object instanceof IJavaProject) {
			return PROJECT;
		} else if(object instanceof ITypeRoot) {
			return FILE;
		} else if(object instanceof IPackageFragment || object instanceof IPackageFragmentRoot
				|| object instanceof IPackageDeclaration) {
			return PACKAGE;
		}
		return null;
	}

	public ImageDescriptor getCategoryImage(String category) {
		String key = jdtDescriptorMapping.get(category);
		if(key != null) {
			return JavaUI.getSharedImages().getImageDescriptor(key);
		}
		if(category.equals(PROJECT)) {
			return FerretJdtPlugin.getDefault().getWorkbench().getSharedImages()
				.getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_OBJ_PROJECT);
		}
		return null;
	}

	public String getCategoryText(String category) {
		return category;
	}

}
