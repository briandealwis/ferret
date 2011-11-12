package ca.ubc.cs.ferret.views;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.ui.dialogs.ListDialog;

import ca.ubc.cs.ferret.AskFerretEditorContextMenuAction;
import ca.ubc.cs.ferret.model.Consultation;
import ca.ubc.cs.ferret.model.IConceptualQuery;

public class PopulatedAskFerretEditorContextMenuAction 
		extends AskFerretEditorContextMenuAction {

	public PopulatedAskFerretEditorContextMenuAction() {}

	@Override
	protected void process(QueriesDossierView view, Object[] objects) {
		view.promptForDesiredQueries(objects);
	}

}
