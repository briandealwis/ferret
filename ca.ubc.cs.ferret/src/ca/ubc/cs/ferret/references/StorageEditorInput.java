/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.references;
 
import ca.ubc.cs.ferret.FerretPlugin;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IStorageEditorInput;

/* Stolen from org.eclipse.jdt.internal.debug.ui.StorageEditorInput */
public abstract class StorageEditorInput extends PlatformObject implements IStorageEditorInput {

	/**
	 * Storage associated with this editor input
	 */
	private IStorage fStorage;
	
	/**
	 * Constructs an editor input on the given storage
	 */
	public StorageEditorInput(IStorage storage) {
		fStorage = storage;
	}
	
	/**
	 * @see IStorageEditorInput#getStorage()
	 */
	public IStorage getStorage() {
		return fStorage;
	}

	/**
	 * @see IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return FerretPlugin.getDefault().getWorkbench().getSharedImages()
			.getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
	}

	/**
	 * @see IEditorInput#getName()
	 */
	public String getName() {
		return getStorage().getName();
	}

	/**
	 * @see IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * @see IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return getStorage().getFullPath().toOSString();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return object instanceof StorageEditorInput &&
		 getStorage().equals(((StorageEditorInput)object).getStorage());
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getStorage().hashCode();
	}

}
