/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class JdtSphereHelperFactory implements IExecutableExtensionFactory {

    public JdtSphereHelperFactory() {
        super();
    }

    public Object create() throws CoreException {
        return JdtSphereHelper.getDefault();
    }

}
