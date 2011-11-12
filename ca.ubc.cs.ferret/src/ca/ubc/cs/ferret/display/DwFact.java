/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.display;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.Fact;

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
