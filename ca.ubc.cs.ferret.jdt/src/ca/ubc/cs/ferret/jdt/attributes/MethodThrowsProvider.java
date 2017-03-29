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
package ca.ubc.cs.ferret.jdt.attributes;

import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

public class MethodThrowsProvider extends AbstractJavaAttributeValue<IMethod,Object> {

    public MethodThrowsProvider() {
    }

    public Object getCategory(IMethod object) {
        try {
            Set<Object> exceptions = new HashSet<Object>();
            for(String etn : object.getExceptionTypes()) {
            	String qualifier = Signature.getSignatureQualifier(etn);
            	if(qualifier.length() > 0) {
            		etn = Signature.getSignatureQualifier(etn) + "." 
            			+ Signature.getSignatureSimpleName(etn);
            	} else {
            		etn = Signature.getSignatureSimpleName(etn);
            	}
            	exceptions.add(JavaModelHelper.getDefault().resolveType(etn, object));
            }
            if(exceptions.isEmpty()) { exceptions.add("(none)"); }
            return exceptions;
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

	public Object[] getCategories() {
		return null;
	}

	public String getCategoryText(Object category) {
		if(category instanceof IType) {
			return ((IType)category).getFullyQualifiedName();
		} else if(category instanceof String) {
			return (String)category;
		}
		return category.toString();
	}
}
