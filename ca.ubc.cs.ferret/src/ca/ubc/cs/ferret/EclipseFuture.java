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

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;

public class EclipseFuture<T> implements Future<T> {
	protected Object description;
	protected boolean cancelled = false;
	protected boolean hasValue = false;
	protected T value;
	protected ILock lock;
	protected Thread acquirer = null;
	
	public EclipseFuture(Object d) {
		description = d;
		lock = Job.getJobManager().newLock();
		lock.acquire();
		acquirer = Thread.currentThread();
	}
	
	public void set(T v) {
		if(hasValue) {
			log("set *overriding* value from " 
					+ FerretPlugin.prettyPrint(value) + " to " + FerretPlugin.prettyPrint(v));
		} else {
			log("set value to '" + FerretPlugin.prettyPrint(v));
		}
		value = v;
		cancelled = false;
		if(hasValue) {return; }	// allow updates
		hasValue = true;
		lock.release();
		acquirer = null;
	}
	
	public boolean isDone() {
		return hasValue;
	}
	
	public T get() throws CancellationException {
		while(!hasValue) { 
			lock.acquire(); 
			if(!hasValue) {
				log("get(): lock acquired but with no value");
				return null;
			} 
			lock.release();
			if(isCancelled()) {
				log("get(): future was apparently cancelled");
				throw new CancellationException(toString());
			}
		}
		return value;
	}

	private void log(String message) {
		if(FerretPlugin.hasDebugOption("debug/showFutures")) {
			System.out.println(Thread.currentThread() + " " + toString() + ":" + message);
		}
	}

	public T get(long delay, TimeUnit unit) throws TimeoutException, CancellationException {
		if(!hasValue) {
			try {
				log("get(" + unit.toMillis(delay) + "): about to acquire lock " + lock);
				if(!lock.acquire(unit.toMillis(delay))) {
					if(isCancelled()) { throw new CancellationException(toString()); }
					log("get(" + unit.toMillis(delay) + "): timed out");
					throw new TimeoutException("timeout for " + description);
				}
				if(!hasValue) {
					FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID,
							FerretErrorConstants.CONTRACT_VIOLATION,
							"Should not happen: a future's lock has been successfully acquired, but the future has no value", null));
					log("get(" + unit.toMillis(delay) + "): lock acquired but with no value");
					return null;
				} 

				// if we got this far, we have acquired the lock and thus must release it
				lock.release();
				if(isCancelled()) {
					log("get(" + unit.toMillis(delay) + "): future was apparently cancelled");
					throw new CancellationException(toString());
				}
				assert hasValue;
			} catch(InterruptedException e) {
				log("get(" + unit.toMillis(delay) + "): interrupted exception");
				FerretPlugin.log(e);
			}
		}
		if(isCancelled()) { throw new CancellationException(toString()); }
		return value;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		if(isDone()) { return false; }
		log("cancelled by " + (Thread.currentThread() == acquirer ? "acquirer" : "another"));
		cancelled = true;
		if(Thread.currentThread() == acquirer) { 
			lock.release(); 
			acquirer = null;
		}
		return true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getSimpleName());
		buffer.append('(');
		if(isDone()) { buffer.append("{done} "); }
		if(isCancelled()) { buffer.append("{cancelled} "); }
		buffer.append(lock.toString());
		buffer.append(' ');
		buffer.append(FerretPlugin.prettyPrint(description));
		buffer.append(')');
		return buffer.toString();
	}
}
 
