package ca.ubc.cs.ferret.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public abstract class AbstractJob implements IJob {
	protected IProgressMonitor monitor;
	
	public AbstractJob() {}
	
	public boolean belongsTo(String jobFamily) {
		return false;
	}

	public void cancel() {
		if(monitor != null) {
			monitor.setCanceled(true);
		}
	}

	public void ensureReadyToRun() {
	}

	public abstract boolean run(IProgressMonitor monitor);
	
	public final boolean execute(IProgressMonitor monitor) {
		try {
			this.monitor = monitor;
			return run(monitor);
		} catch(OperationCanceledException e) {
			return false;
		} finally {
			this.monitor = null;
		}
	}
}
