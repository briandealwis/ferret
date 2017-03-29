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
package ca.ubc.cs.ferret.util;

import org.eclipse.core.runtime.IProgressMonitor;
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
