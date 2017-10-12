/*******************************************************************************
 * Copyright (c) 2004 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret;

import ca.ubc.cs.ferret.views.QueriesDossierView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * This class implements the menu actions to consult Ferret.
 */
public abstract class AbstractAskFerretAction {
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
