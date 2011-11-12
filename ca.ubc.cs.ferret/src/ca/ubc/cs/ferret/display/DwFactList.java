/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.display;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.Fact;

public class DwFactList extends DwBaseObject {
    protected Fact facts[];
    public DwFactList(Collection<Fact> _facts, IDisplayObject _parent) {
        super(_parent);
        facts = _facts.toArray(new Fact[_facts.size()]);
    }

    @Override
    protected void buildChildren(IDisplayObject[] oldChildren) {
        children = new DwFact[facts.length];
        for(int index = 0; index < facts.length; index++) {
        	IDisplayObject cdo = findDisplayObject(facts[index], oldChildren);
        	if(cdo == null) { cdo = new DwFact(facts[index], this); }
            children[index] = cdo;
        }
    }

    public String getText() {
        return "Facts [" + facts.length + "]";
    }

    public Object getObject() {
        return facts;
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
