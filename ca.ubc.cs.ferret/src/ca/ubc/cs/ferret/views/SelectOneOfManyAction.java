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

import ca.ubc.cs.ferret.ICallback;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class SelectOneOfManyAction<T> extends Action implements IMenuCreator {
    protected ListenerList<ICallback<T>> selectionChangedList = new ListenerList<>();
    protected List<String> descriptions;
    protected List<T> objects;
    protected List<ImageDescriptor> images;
    protected Set<Integer> separators;
    protected Menu dropDownMenu = null;
    protected T selected = null;
    protected int actionStyle = IAction.AS_RADIO_BUTTON;
    
    public SelectOneOfManyAction(String text) {
        super(text, AS_DROP_DOWN_MENU);
        clear();
        setMenuCreator(this);
    }

    public void dispose() {
        if(dropDownMenu == null) { return; }
        dropDownMenu.dispose();
        dropDownMenu = null;
    }

    public void run() {
        System.out.println("SelectOneOfManyAction run()!");
    }
    
    public void clear() {
        descriptions = new ArrayList<String>();
        objects = new ArrayList<T>();
        images = new ArrayList<ImageDescriptor>();
        separators = new HashSet<Integer>();
        selected = null;
    }

    public Menu getMenu(Control parent) {           
        if (dropDownMenu != null) {
            dropDownMenu.dispose();
        }
        dropDownMenu = new Menu(parent);
        addActionsToMenu();
        return dropDownMenu;
    }

    public Menu getMenu(Menu parent) {
        if (dropDownMenu != null) {
            dropDownMenu.dispose();
        }
        dropDownMenu = new Menu(parent);
        addActionsToMenu();
        return dropDownMenu;
    }    
    
    protected void addActionsToMenu() {
    	
        for(int i = 0; i < descriptions.size(); i++) {
            String description = descriptions.get(i);
            T object = objects.get(i);
            ImageDescriptor image = images.get(i);
            
            Action action = new Action(description, getActionStyle()) {                
                @Override
                public void run() {
                	if((getStyle() != IAction.AS_RADIO_BUTTON &&
                			getStyle() != IAction.AS_RADIO_BUTTON) || isChecked()) {
                		selectionChanged(objects.get(Integer.parseInt(getId())));
                	}
                }
            };
            action.setId(Integer.toString(i));
            action.setEnabled(true);
            action.setChecked(selected == object);
//            ImageDescriptor id = FerretPlugin.getImage(object);
//            if(id != null) { action.setImageDescriptor(id); }
            if(image != null) { action.setImageDescriptor(image); }
            ActionContributionItem item = new ActionContributionItem(action);
            item.fill(dropDownMenu, -1);
            if(separators.contains(i)) {
            	new Separator().fill(dropDownMenu, -1);
            }
        }
    }

    public int getActionStyle() {
    	return actionStyle;
    }
    
    public void setActionStyle(int style) {
    	actionStyle = style;
    }
    
    public T getSelected() {
        return selected;
    }

    public void setSelected(T selected) {
        this.selected = selected;
    }

    protected void selectionChanged(T object) {
		for (ICallback<T> listener : selectionChangedList) {
			listener.run(object);
        }
    }

    public void add(String string, T object) {
    	add(string, null, object);
    }
    	
    public void add(String string, ImageDescriptor image, T object) {
        descriptions.add(string);
        objects.add(object);
        images.add(image);
    }

	public void addSeparator() {
		separators.add(descriptions.size() - 1);
	}

    public void addSelectionCallback(ICallback<T> callback) {
        selectionChangedList.add(callback);
    }

    public void removeSelectionCallback(ICallback<T> callback) {
        selectionChangedList.remove(callback);
    }
}
