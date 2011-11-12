package ca.ubc.cs.ferret.history;

import ca.ubc.cs.ferret.model.SphereHelper;

public class HistorySphereHelper extends SphereHelper {
	protected static HistorySphereHelper singleton;
	
	public static HistorySphereHelper getDefault() {
		if(singleton == null) {
			 singleton = new HistorySphereHelper();
			 singleton.start();
		}
		return singleton;
	}
	
	protected HistoryMonitor historyMonitor;
	
	protected HistorySphereHelper() {}

	@Override
	public void start() {
		if(historyMonitor == null) {
			historyMonitor = new HistoryMonitor();
		}
		historyMonitor.start();
	}

	@Override
	public void reset() {
		if(historyMonitor != null) {
			historyMonitor.reset();
		}
	}

	@Override
	public void stop() {
		if(historyMonitor != null) {
			historyMonitor.stop();
		}
	}

	public HistoryMonitor getHistoryMonitor() {
		return historyMonitor;
	}

}
