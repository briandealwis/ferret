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

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;


public class ZipEntryReference extends AbstractReference {
    protected ZipEntryStorage jarEntry;
    
    public ZipEntryReference(String _jarFile, String _entryName) throws IOException {
    	super(_entryName);
    	jarEntry = new ZipEntryStorage(_jarFile, _entryName);
    }
    
    public ZipEntryReference(String _jarFile, String _entryName, 
            int _offset, int _length) throws IOException {
        super(_entryName, _offset, _length);
        jarEntry = new ZipEntryStorage(_entryName, _jarFile);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return jarEntry.getContents();
        } catch (CoreException e) {
//            FerretPlugin.log(e);
            throw new IOException("Unexpected CoreException: " + e.toString());
        }
    }

    @Override
    public IEditorInput getEditorInput() {
        return new ZipEntryStorageEditorInput(jarEntry);
    }

}
