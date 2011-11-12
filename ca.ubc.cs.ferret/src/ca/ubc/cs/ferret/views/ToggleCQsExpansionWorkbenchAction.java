package ca.ubc.cs.ferret.views;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


public class ToggleCQsExpansionWorkbenchAction implements IWorkbenchWindowActionDelegate {
	protected IWorkbenchWindow window;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void dispose() {}

	public final void run(IAction action) {
		window.getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				QueriesDossierView view = findFerretView();
				if(view != null) {
					runAction(view);
				}
			}
		});
	}
	
	/**
	 * Perform this action on the provided view.  Will be run in UI thread. 
	 * @param view the view for the appropriate workbench window
	 */
	protected void runAction(QueriesDossierView view) {
		view.expandCQs(true);				
	}

	public void selectionChanged(IAction action, ISelection selection) {}

	protected IWorkbenchPage getActivePage() {
		return window.getActivePage();
	}

	protected QueriesDossierView findFerretView() {
		IWorkbenchPage page = getActivePage();
		if (page == null) { return null; }
		
		IViewPart view = page.findView(QueriesDossierView.viewID);
		if(view != null && view instanceof QueriesDossierView) {
			return (QueriesDossierView)view;
		}
		return null;
	}

}
