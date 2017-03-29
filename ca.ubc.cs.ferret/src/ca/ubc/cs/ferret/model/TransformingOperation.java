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
package ca.ubc.cs.ferret.model;

import ca.ubc.cs.ferret.types.FerretObject;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Chains the first relation with the first matching relation from {@link #transforms}.
 * The results of this relation are that from the matching relation.
 * If none of the relations in {@link #transforms} match, and {@link #permissive} 
 * is true, then the value is passed unmolested, or is otherwise discarded (the default).
 * @author Brian de Alwis
 */
public class TransformingOperation extends RelationalFunction {
	protected IProgressMonitor monitor;
	protected IRelationResolver resolver;
	protected IRelation first;
	protected List<IRelationFactory> transforms;
	protected boolean permissive;
	
	protected boolean monitorStarted = false;
	protected FerretObject nextObject = null;
	
	protected IRelation tail;

	public TransformingOperation(IProgressMonitor monitor,
			IRelationResolver resolver, IRelation first,
			List<IRelationFactory> transforms, boolean permissive) {
		this.monitor = monitor;
		this.resolver = resolver;
		this.first = first;
		this.transforms = transforms;
		this.permissive = permissive;
	}

	public TransformingOperation(IProgressMonitor monitor,
			IRelationResolver resolver, IRelation first,
			List<IRelationFactory> transforms) {
		this(monitor, resolver, first, transforms, false);
	}

	public boolean hasNext() {
		if(nextObject != null) { return true; }
		return (nextObject = internalNext()) != null;
	}

	public FerretObject next() {
		FerretObject n = nextObject;
		nextObject = null;
		return n;
	}

	protected FerretObject internalNext() {
		if(!monitorStarted) {
			monitor.beginTask(getClass().getName(), IProgressMonitor.UNKNOWN);
			monitorStarted = true;
		}
		if(tail != null && tail.hasNext()) { return tail.next(); }
		if(!first.hasNext()) {
			monitor.done();
			return null;
		}
		do {
			FerretObject next = first.next();
			IProgressMonitor m = new SubProgressMonitor(monitor, 1);
			boolean transformed = false;
			for(IRelationFactory rf : transforms) {
				if((tail = rf.configure(m, resolver, next)) != null) {
					transformed = true;
					if(tail.hasNext()) { return tail.next(); }
					break;
				}	
			}
			if(!transformed && isPermissive()) { return next; }
		} while(first.hasNext());
		return null;
	}

	public boolean isPermissive() {
		return permissive;
	}

	public void setPermissive(boolean permissive) {
		this.permissive = permissive;
	}

	public List<IRelationFactory> getTransformations() {
		return transforms;
	}
	

}
