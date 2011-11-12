package ca.ubc.cs.ferret.views;

import ca.ubc.cs.ferret.AskFerretWorkbenchAction;

public class PopulatedAskFerretWorkbenchAction extends AskFerretWorkbenchAction {

	public PopulatedAskFerretWorkbenchAction() {}

	@Override
	protected void process(QueriesDossierView view, Object[] objects) {
		view.promptForDesiredQueries(objects);
	}

}
