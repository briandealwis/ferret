/*
 * Copyright 2005 by X.
 * @author bsd
 */
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
