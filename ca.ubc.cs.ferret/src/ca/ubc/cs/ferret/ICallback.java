/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret;

/**
 * A complement to the Runnable interface that takes a single argument.
 *  Clients are expected to implement this interface.
 * @author bsd
 * @see java.lang.Runnable
 */
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
