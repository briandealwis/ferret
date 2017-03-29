/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.references;

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

/**
 * Implementation of storage for a local file (<code>java.io.File</code>).
 * Stolen from org.eclipse.debug.core.sourcelookup.containers.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * @see IStorage
 */

public class LocalFileStorage extends PlatformObject implements IStorage {
	
	/**
	 * The file this storage refers to.
	 */ 
	private File fFile;
		
	/**
	 * Constructs and returns storage for the given file.
	 * 
	 * @param file a local file
	 */
	public LocalFileStorage(File file){
		setFile(file);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		try {
			return new FileInputStream(getFile());
		} catch (IOException e){
			throw new CoreException(new Status(IStatus.ERROR, 
					FerretPlugin.pluginID, FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
					e.getMessage(), e)); 
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		try {
			return new Path(getFile().getCanonicalPath());
		} catch (IOException e) {
			FerretPlugin.log(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		return getFile().getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#isReadOnly()
	 */
	public boolean isReadOnly() {
		return true;
	}
	
	/**
	 * Sets the file associated with this storage
	 * 
	 * @param file a local file
	 */
	private void setFile(File file) {
		fFile = file;	
	}
	
	/**
	 * Returns the file associated with this storage
	 * 
	 * @return file
	 */
	public File getFile() {
		return fFile;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {		
		return object instanceof LocalFileStorage &&
			 getFile().equals(((LocalFileStorage)object).getFile());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getFile().hashCode();
	}	
}
