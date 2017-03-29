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
package ca.ubc.cs.ferret;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class AskFerretObjectAction extends AbstractAskFerretAction
    implements IObjectActionDelegate {

    ISelection selection;
    
    public AskFerretObjectAction() {
        super();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        // This is where we hook into the current element
        if (selection.isEmpty()) {
            return;
        }
        if (selection instanceof IStructuredSelection) {
            performQuery(((IStructuredSelection)selection).toArray());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *          org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
        if(FerretPlugin.hasDebugOption("debug/selectionChanged")) {
        	System.out.println("AskFerretObjectAction: selectionChanged: selection={" + FerretPlugin.compactPrettyPrint(selection) + "} ["
        			+ selection.getClass().getName() + "], action=" + action);
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // ignored
    }
}
