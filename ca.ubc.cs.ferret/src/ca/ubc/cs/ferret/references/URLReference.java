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
import java.net.URL;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * A reference within some resource described by a URL.
 * URL content may be fetched multiple times; this class may be
 * inappropriate if this URL is not meant for multiple fetches.
 */
public class URLReference extends AbstractReference {
    protected URL url;
    
    public URLReference(URL _url, int _offset, int _length) {
        super(_url.toString(), _offset, _length);
        url = _url;
    }

    public InputStream getInputStream() throws IOException {
        return (InputStream)url.getContent();
    }

    public IEditorInput getEditorInput() {
        return new IEditorInput() {
            public boolean exists() { return false; }
            public ImageDescriptor getImageDescriptor() { return imageDescriptor; }
            public String getName() { return url.getPath(); }
            public IPersistableElement getPersistable() { return null; }
            public String getToolTipText() { return name; }
            
			public <T> T getAdapter(Class<T> adapter) {
                System.out.println("URLReference asked to adapt to " + adapter.getName());
                return null; }
        };
    }
}
