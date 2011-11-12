package ca.ubc.cs.ferret.history;

import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;

public abstract class AbstractHistoryQuery extends AbstractSingleParmConceptualQuery<Object> {
	protected int selectionCounter;
	
	public AbstractHistoryQuery() {}

	public void completed() {
		selectionCounter = getHistoryMonitor().getSelectionCounter();
	}

	public boolean isValid() {
		return selectionCounter == getHistoryMonitor().getSelectionCounter();
	}

	protected HistoryMonitor getHistoryMonitor() {
		return HistorySphereHelper.getDefault().getHistoryMonitor();
	}
}
