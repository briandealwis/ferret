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

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Walk a resource delta to see if there was an actual change in content or
 * movement.  Ignore changes to markers (MARKER), description (DESCRIPTION),
 * and synchronization information (SYNC).
 * @author bsd
 */
public class ModificationVisitor implements IResourceDeltaVisitor {
    protected boolean wasModified = false;
    
    public ModificationVisitor() {
    }

    protected static int ignoredFlags = IResourceDelta.MARKERS
            & IResourceDelta.DESCRIPTION & IResourceDelta.SYNC;

    public boolean visit(IResourceDelta delta) throws CoreException {
        int eventFlags = delta.getFlags();
        if((eventFlags & ~ignoredFlags) != 0) {
            wasModified = true;
        }
//      if we've determined is was a modification, then don't bother continuing
        return !wasModified;    
    }

    public boolean wasModified() {
        return wasModified;
    }
}
