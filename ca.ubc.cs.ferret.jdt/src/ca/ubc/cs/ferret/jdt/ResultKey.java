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
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.FerretPlugin;
import org.eclipse.jdt.core.IJavaElement;

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
