/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import ca.ubc.cs.ferret.model.SphereHelper;

public class AskFerretEditorContextMenuAction extends AbstractAskFerretAction
        implements IEditorActionDelegate {

    public AskFerretEditorContextMenuAction() {
        super();
    }

    protected IEditorPart editor;
    
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        editor = targetEditor;
    }

    public void run(IAction action) {
        if(editor == null) { return; }
        for(SphereHelper sphere : FerretPlugin.getSphereHelpers()) {
            Object selected[] = sphere.getSelectedObjects(editor);
            if(selected != null && selected.length > 0) {
                performQuery(selected);
                return;
            }
        }
        setStatusLineErrorMessage("No objects for current selection");
    }

    private void setStatusLineErrorMessage(String string) {
		editor.getEditorSite().getActionBars().getStatusLineManager()
			.setErrorMessage(string);
	}

	public void selectionChanged(IAction action, ISelection selection) {
        // ignore
    }

}
