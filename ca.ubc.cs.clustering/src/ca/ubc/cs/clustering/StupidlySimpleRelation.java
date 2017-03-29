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
package ca.ubc.cs.clustering;

public class StupidlySimpleRelation implements IRelation {
    protected Object subject;

    protected String relation;

    protected Object object;

    public StupidlySimpleRelation(Object _subject, String _relation, Object _object) {
        subject = _subject;
        relation = _relation;
        object = _object;
    }

    public Object getSubject() {
        return subject;
    }

    public Object getObject() {
        return object;
    }
    
    public String getRelation() {
        return relation;
    }

    public String getDescription() {
        return relation;
    }

    public String toString() {
    	return relation + "{" + subject + " -> " + object + "}";
    }
}
