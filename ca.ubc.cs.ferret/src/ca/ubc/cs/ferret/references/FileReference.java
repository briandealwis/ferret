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
package ca.ubc.cs.ferret.references;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileInPlaceEditorInput;

public class FileReference extends AbstractReference {
	protected IPath filePath;

	/**
     * Create new instance of the receiver.
     * @param _filePath the path to the file
     * @param _offset 
     * @param _length
     */
	public FileReference(IPath _filePath) {
        super(_filePath.toString());
		filePath = _filePath;
	}

	public FileReference(IPath _filePath, int offset, int length) {
        super(_filePath.toString(), offset, length);
		filePath = _filePath;
	}

	public IPath getPath() {
		return filePath;
	}

    public File getFile() {
        return filePath.toFile();
    }
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(getFile());
    }

    public IEditorInput getEditorInput() {
        IFile f = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(filePath);
        if(f != null) { return new FileInPlaceEditorInput(f); }
        return new LocalFileStorageEditorInput(new LocalFileStorage(filePath.toFile()));
    }
}
