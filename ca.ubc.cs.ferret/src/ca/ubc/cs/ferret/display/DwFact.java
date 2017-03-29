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
package ca.ubc.cs.ferret.display;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.Fact;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DwFact extends DwBaseObject {
    protected Fact fact;
    
    public DwFact(Fact _fact, IDisplayObject _parent) {
        super(_parent);
        fact = _fact;
    }

    @Override
    protected void buildChildren(IDisplayObject[] oldChildren) {
        // nothing to do: facts don't have children
        children = new IDisplayObject[0];
    }

    public String getText() {
        return fact.getFact();
    }

    public Object getObject() {
        return fact;
    }


    @Override
    public ImageDescriptor getImage() {
        return AbstractUIPlugin.imageDescriptorFromPlugin(FerretPlugin.pluginID, "icons/fact.gif");
    }

    @Override
    public int getImportance() {
        return 0;       // we're pretty damn important
    }
}
