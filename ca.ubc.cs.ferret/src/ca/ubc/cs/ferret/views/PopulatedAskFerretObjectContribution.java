package ca.ubc.cs.ferret.views;

import ca.ubc.cs.ferret.AskFerretObjectAction;

public class PopulatedAskFerretObjectContribution extends AskFerretObjectAction {

	public PopulatedAskFerretObjectContribution() {}

	@Override
	protected void process(QueriesDossierView view, Object[] objects) {
		view.promptForDesiredQueries(objects);
	}

}
