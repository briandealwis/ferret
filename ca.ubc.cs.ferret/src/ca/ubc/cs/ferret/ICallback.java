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

/**
 * A complement to the Runnable interface that takes a single argument.
 *  Clients are expected to implement this interface.
 * @author bsd
 * @see java.lang.Runnable
 */
@FunctionalInterface
public interface ICallback<T> {
    /**
     * Run this runnable with the given argument.  The content of the argument
     * is unchecked; implementations should ensure that it conforms to the
     * expectation of this runnable.
     * @param argument the argument to pass to the application
     * @see java.lang.Runnable#run()
     */
    public void run(T argument);
}
