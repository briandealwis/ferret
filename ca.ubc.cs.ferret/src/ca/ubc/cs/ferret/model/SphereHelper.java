/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.model;

import java.util.function.Predicate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import ca.ubc.cs.clustering.attrs.IAttributeSource;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;

public abstract class SphereHelper {

    protected SphereHelper() {
        // Protected as Spheres are expected to be singletons
    }

    abstract public void start();

    abstract public void reset();

    abstract public void stop();
    
    
    /**
     * Identify the model objects corresponding to the current selection or
     * caret location.
     * 
     * @param editor
     * @return model objects
     */
    public Object[] getSelectedObjects(IEditorPart editor) {
        return null;
    }

    /**
     * Return a minimally identifying label for the particular instance. The
     * assumption is that further details will be available in the surrounding
     * context. For example, for a Java method, this would be simply the method
     * name, with perhaps method arguments.
     * 
     * @param element
     * @return
     */
    public String getMinimalLabel(Object element) {
        return null;
    }

    public String getLabel(Object element) {
        return getMinimalLabel(element);
    }

    /**
     * Helper function for users of inheritors of LabelProvider.  The default implementation
     * of LabelProvider.getText() helpfully uses toString().  We can do that on our own! 
     * @param element
     * @param label
     * @return
     */
    protected String getMeaningfulLabel(Object element, String label) {
    	if(element == null) { return null; }
    	return label.equals(element.toString()) ? null : label;

    }
    
    /** 
     * Provide an image for the specified element, if possible.  Return null if not supported by
     * this sphere.
     */
    public ImageDescriptor getImage(Object element) {
        return null;
    }

    /**
     * Return whether this sphere is able to open an editor for objects
     * like that provded.  Return true if it has this capability.
     * @param obj
     * @return true if <code>obj</code> is able to be opened
     */
	public boolean canOpen(Object obj) {
		return false;
	}

    /**
     * Attempt to open an editor on the provided element, if applicable. Return
     * true if this sphere can handle the provided object, and was able to open.
     */
    public boolean openObject(Object element) {
        return false;
    }

    /**
     * Return an attribute source for <code>element</code>.
     * Return null if this sphere does not support providing attributes for <code>element</code>.
     * @param element the object providing attributes
     * @return an attribute source for the element
     */
    public IAttributeSource getAttributeSource(Object element) {
        return null;
    }
    
	public ISphereFactory[] getSphereFactories() {
		return new ISphereFactory[0];
	}

	public String getHandleIdentifier(Object object) {
		return null;
	}

	public Object getParent(Object object) {
		return null;
	}

	public boolean isCommonElement(Object o) {
		return false;
	}

	// FIXME: should this take a Regex instead?
	protected ITextSelection expandTextSelection(IEditorPart editor,
			ITextSelection ts, Predicate<Character> bounds) {
		ITextEditor txtEditor = getTextEditor(editor);
		if (txtEditor == null) { return null; }

		IDocument document= txtEditor.getDocumentProvider().getDocument(txtEditor.getEditorInput());
		int startIndex = ts.getOffset(), stopIndex = startIndex;
		int docLength = document.getLength();
		try { 
			while(startIndex > 0) {
				char ch = document.getChar(startIndex - 1); 
				if(!bounds.test(ch)) { break; }
				startIndex--;
			}
			while(stopIndex + 1 < docLength) {
				char ch = document.getChar(stopIndex + 1); 
				if(!bounds.test(ch)) { break; }
				stopIndex++;
			}
			return new TextSelection(document, startIndex, stopIndex - startIndex + 1);
		} catch (BadLocationException e) {
			FerretPlugin.log(new Status(IStatus.WARNING, FerretPlugin.pluginID,
					FerretErrorConstants.EXCEPTION_HANDLED,
					"BadLocationException while expanding identifier in text selection: " + e.getMessage(), null));;
			return null;
		}
	}

	protected ITextEditor getTextEditor(IEditorPart editor) {
		if (editor instanceof MultiPageEditorPart) {
			MultiPageEditorPart mpe = (MultiPageEditorPart) editor;
			editor = getMPEActiveEditor(mpe);
		}
		return editor instanceof ITextEditor ? (ITextEditor) editor : null;
	}

	protected IEditorPart getMPEActiveEditor(MultiPageEditorPart mpe) {
		return (IEditorPart)FerretPlugin.invokeMethod(mpe, "getActiveEditor",
				new Class[0], new Object[0]);
	}

}
