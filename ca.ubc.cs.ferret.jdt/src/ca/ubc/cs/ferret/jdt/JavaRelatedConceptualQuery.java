/*
 * Copyright 2004 University of British Columbia
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;

/**
 * @author bsd
 */
public abstract class JavaRelatedConceptualQuery<T> extends AbstractSingleParmConceptualQuery<T> {
	protected int javaModelCounter = -1;
	
    /**
     * The required public 0-argument constructor, as per the extension-point
     */
    public JavaRelatedConceptualQuery() {
    }

    protected void completed() {
    	javaModelCounter = JavaModelHelper.getDefault().getJavaModelCounter();
    	super.completed();
    }
    
    public boolean isValid() {
    	return javaModelCounter == JavaModelHelper.getDefault().getJavaModelCounter();
    }
}
