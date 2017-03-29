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
package ca.ubc.cs.ferret.views;

import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.IClusteringsContainer;
import ca.ubc.cs.ferret.model.Consultation;
import org.eclipse.jface.viewers.ITreeViewerListener;

public interface IQueryListener extends ITreeViewerListener {
	/**
	 * Notify that query has been registered.  If q.isDone(), then this consultation
	 * has been previously submitted and evaluated (e.g., cached)
	 * @param q
	 */
	public void queryInitiated(Consultation q);
	/**
	 * Not guaranteed to happen; extremely unlikely when consultation
	 * is already done when initiated.
	 * @param q
	 */
	public void queryCompleted(Consultation q);
	public <T> void clusteredBy(IClusteringsContainer<? extends T> container, Clustering<? extends T> cluster);
	public void reconfigured();
}
