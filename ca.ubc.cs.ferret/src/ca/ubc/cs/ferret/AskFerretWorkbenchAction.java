/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ca.ubc.cs.ferret.model.SphereHelper;

public class AskFerretWorkbenchAction extends AbstractAskFerretAction  
    implements IWorkbenchWindowActionDelegate {

    public AskFerretWorkbenchAction() {
    }

    protected IWorkbenchWindow window;

    public void init(IWorkbenchWindow _window) {
        window = _window;
    }
    
    public void dispose() {
        window = null;
    }

   protected IWorkbenchPage getActivePage() {
       if(window == null) { return super.getActivePage(); }
        return window.getActivePage();
    }
    
     public void run(IAction action) {
    	 window.getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
		         IEditorPart editor = getActivePage().getActiveEditor();
		         if(editor == null) { return; }
		         for(SphereHelper sphere : FerretPlugin.getSphereHelpers()) {
		             Object selected[] = sphere.getSelectedObjects(editor);
		             if(selected != null && selected.length > 0) {
		                 performQuery(selected);
		             }
		         }
			}});
     }

    public void selectionChanged(IAction action, ISelection selection) {
        // ignore, don't care: we fetch our value from the currently-selected element
        // in the editor
    }

}
