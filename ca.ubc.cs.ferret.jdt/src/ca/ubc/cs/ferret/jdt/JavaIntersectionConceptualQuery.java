package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.model.AbstractIntersectionConceptualQuery;

public abstract class JavaIntersectionConceptualQuery<IT,OT> extends AbstractIntersectionConceptualQuery<IT,OT> {
	protected int javaModelCounter = -1;
    
    public JavaIntersectionConceptualQuery() {}
    
    protected void completed() {
    	javaModelCounter = JavaModelHelper.getDefault().getJavaModelCounter();
    	super.completed();
    }
    
    public boolean isValid() {
    	return javaModelCounter == JavaModelHelper.getDefault().getJavaModelCounter();
    }

}
