/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.attributes;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.clustering.attrs.IClassifier;

public class MethodArgumentsProvider extends AbstractJavaAttributeValue<IMethod,Object> {

    public MethodArgumentsProvider() {
        super();
    }

    public Object getCategory(IMethod object) {
        IMethod m = (IMethod)object;
        if(m.getNumberOfParameters() == 0) { return "none"; }
        try {
            Set<Object> parms = new HashSet<Object>();
            for(String ptn : m.getParameterTypes()) {
            	parms.add(resolve(ptn, m));
            }
            return parms;
        } catch(JavaModelException e) {
            log(e);
            return IClassifier.UNDETERMINED_ATTRIBUTE_VALUE;
        }
    }

    /**
     * Resolve the provided type name.  This is assumed to be in
     * Signature type format.
     * @param tn type 
     * @param m defining method context
     * @return the corresponding IType or a string description
     * @throws JavaModelException 
     * @see Signature
     */
    protected Object resolve(String tn, IMethod m) throws JavaModelException {
    	switch(Signature.getTypeSignatureKind(tn)) {
    	case Signature.ARRAY_TYPE_SIGNATURE:
    	case Signature.CLASS_TYPE_SIGNATURE:
    		if(Signature.getSignatureQualifier(tn).length() == 0) {
    			return JavaModelHelper.getDefault()
    			.resolveTypeName(Signature.getSignatureSimpleName(tn), m);
    		}
        	return Signature.getSignatureQualifier(tn) + "."
    	   		+ Signature.getSignatureSimpleName(tn);
    		
    	case Signature.BASE_TYPE_SIGNATURE:
    	case Signature.TYPE_VARIABLE_SIGNATURE:
    	case Signature.WILDCARD_TYPE_SIGNATURE:
    	case Signature.CAPTURE_TYPE_SIGNATURE:
    		default:
    		return Signature.toString(tn);
    	}
	}

	public String[] getCategories() {
		return null;
	}

	public String getCategoryText(Object category) {
		if(category instanceof String) { return (String)category; }
		return category.toString();
	}

}
