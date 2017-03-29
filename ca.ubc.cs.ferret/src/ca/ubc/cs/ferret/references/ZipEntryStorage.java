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

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

/**
 * Storage implementation for zip entries.
 * Stolen from org.eclipse.debug.core.sourcelookup.containers.ZipEntryStorage
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * @see IStorage
 */
public class ZipEntryStorage extends PlatformObject implements IStorage {
	
	/**
	 * Zip file associated with zip entry
	 */
	private ZipFile fArchive;
	
	/**
	 * Zip entry
	 */
	private ZipEntry fZipEntry;
	
	public ZipEntryStorage(String archive, String entry) throws IOException {
		this(new ZipFile(archive), new ZipEntry(entry));
	}

	/**
	 * Constructs a new storage implementation for the
	 * given zip entry in the specified zip file
	 * 
	 * @param archive zip file
	 * @param entry zip entry
	 */
	public ZipEntryStorage(ZipFile archive, ZipEntry entry) {
		setArchive(archive);
		setZipEntry(entry);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		try {
			return getArchive().getInputStream(getZipEntry());
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, 
					FerretPlugin.pluginID, FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
					"Unable to read zip entry contents", e)); 
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		return new Path(getArchive().getName()).append(getZipEntry().getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		int index = getZipEntry().getName().lastIndexOf('\\');
		if (index == -1) {
			index = getZipEntry().getName().lastIndexOf('/');
		}
		if (index == -1) {
			return getZipEntry().getName();
		} 
		return getZipEntry().getName().substring(index + 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#isReadOnly()
	 */
	public boolean isReadOnly() {
		return true;
	}
	
	/**
	 * Sets the archive containing the zip entry.
	 * 
	 * @param archive a zip file
	 */
	private void setArchive(ZipFile archive) {
		fArchive = archive;
	}
	
	/**
	 * Returns the archive containing the zip entry.
	 * 
	 * @return zip file
	 */
	public ZipFile getArchive() {
		return fArchive;
	}	
	
	/**
	 * Sets the entry that contains the source.
	 * 
	 * @param entry the entry that contains the source
	 */
	private void setZipEntry(ZipEntry entry) {
		fZipEntry = entry;
	}
	
	/**
	 * Returns the entry that contains the source
	 * 
	 * @return zip entry
	 */
	public ZipEntry getZipEntry() {
		return fZipEntry;
	}		

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {		
		return object instanceof ZipEntryStorage &&
			 getArchive().equals(((ZipEntryStorage)object).getArchive()) &&
			 getZipEntry().getName().equals(((ZipEntryStorage)object).getZipEntry().getName());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getZipEntry().getName().hashCode();
	}
}
