/*
 * Copyright 2004 University of British Columbia
 * @author bsd
 * This class implements the menu actions to consult the QueryGuru.
 */
package ca.ubc.cs.ferret;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ca.ubc.cs.ferret.views.QueriesDossierView;


/**
 * @author bsd
 */
public abstract class AbstractAskFerretAction{
	
	public AbstractAskFerretAction() {
    }

    protected IWorkbenchPage getActivePage() {
        return FerretPlugin.getDefault().getWorkbench()
        .getActiveWorkbenchWindow().getActivePage();
    }
    
	protected IViewPart openView() {
		IWorkbenchPage page = getActivePage();
		if (page == null) { return null; }
		
		IViewPart view = null;
		try {
				view = page.showView(QueriesDossierView.viewID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return view;
	}
	
	/**
	 * @param objects the objects to query on
	 * @author bsd
	 * @since X
	 */
	protected void performQuery(Object objects[]) {
        if(objects.length == 0) { return; }
		IViewPart view = openView();
		if (view != null && view instanceof QueriesDossierView) {
			process((QueriesDossierView) view, objects);
		}
	}

	protected void process(QueriesDossierView view, Object[] objects) {
		view.performQuery(objects);
	}

}