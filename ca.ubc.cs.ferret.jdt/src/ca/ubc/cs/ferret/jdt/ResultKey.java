/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import org.eclipse.jdt.core.IJavaElement;

import ca.ubc.cs.ferret.FerretPlugin;

public class ResultKey {
    protected String resultType;
    protected IJavaElement javaElement;
    
    public ResultKey(String _type, IJavaElement _element) {
        resultType = _type;
        javaElement = _element;
    }

    public boolean equals(Object other) {
        if(!(other instanceof ResultKey)) { return false; }
        ResultKey o = (ResultKey)other;
        return resultType.equals(o.resultType)
            && javaElement.equals(o.javaElement);
    }
    
    public int hashCode() {
        return resultType.hashCode() * 37 + javaElement.hashCode();
    }
    
    public String toString() {
        return resultType + ": " + FerretPlugin.prettyPrint(javaElement);
    }
}
