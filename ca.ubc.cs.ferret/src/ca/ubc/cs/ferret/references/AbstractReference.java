/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.references;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.EditorsUI;

import ca.ubc.cs.ferret.FerretPlugin;

public abstract class AbstractReference {
    // Uncertain about including this here...
    protected ImageDescriptor imageDescriptor;
    protected String name;
    protected String text;

    protected int offset = -1;
    protected int length = -1;
    
    public AbstractReference(String _name) {
        text = name = _name;
    }
    
    public AbstractReference(String _name, int _offset, int _length) {
        this(_name);
        offset = _offset;
        length = _length;
        imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }


    public int getOffset() {
        return offset;
    }

    public String getEditorId() {
        IWorkbench workbench= FerretPlugin.getDefault().getWorkbench();
        IEditorRegistry editorRegistry= workbench.getEditorRegistry();
        IEditorDescriptor descriptor= editorRegistry.getDefaultEditor(getName(), getContentType());
        if (descriptor != null)
            return descriptor.getId();
        return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
    }

    public IContentType getContentType() {
        InputStream stream = null;
        try {
            stream = getInputStream();
            return Platform.getContentTypeManager().findContentTypeFor(stream, getName());
        } catch (IOException x) {
            FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 0, 
                    "Exception determining content-type of " + getName(), x));
            return null;
        } finally {
            try {
                if (stream != null) { stream.close(); }
            } catch (IOException x) {
                /* who cares */
            }
        }
    }

    public abstract InputStream getInputStream() throws IOException;

    public abstract IEditorInput getEditorInput();
    
    public String toString() {
        return getText();
    }

    public boolean open() {
    	return open(FerretPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage());
    }
    
    public boolean open(IWorkbenchPage page) {
        if (page == null) { return false; }
    	IEditorInput ei = getEditorInput();
    	IEditorPart editorPart = page.findEditor(ei);
    	if(editorPart == null) {
    		try {
    			editorPart = page.openEditor(ei, getEditorId(), false);
    		} catch (PartInitException e) {
    			FerretPlugin.log(e);
    			return false;
    		} 
    	}
    	page.bringToTop(editorPart);       // but don't activate it

    	// if(editor instanceof ITextEditor && e instanceof ISourceReference) {
    	//    ISourceRange range = ((ISourceReference)je).getSourceRange();
    	//    ((ITextEditor)editor).selectAndReveal(range.getOffset(), range.getLength());
    	// }
    	if(getOffset() >= 0 && getLength() >= 0) {
    		ISelectionProvider selectionProvider =
    			editorPart.getEditorSite().getSelectionProvider();
    		if(selectionProvider != null) {
    			selectionProvider.setSelection(new TextSelection(getOffset(), getLength()));
    		}
    	}
    	return true;
    }

	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	public void setImageDescriptor(ImageDescriptor imageDescriptor) {
		this.imageDescriptor = imageDescriptor;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setText(String _text) {
		text = _text;
	}
}