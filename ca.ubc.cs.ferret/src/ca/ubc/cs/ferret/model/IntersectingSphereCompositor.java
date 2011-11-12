package ca.ubc.cs.ferret.model;


public class IntersectingSphereCompositor extends FunctionalSphereCompositor {

	public IntersectingSphereCompositor() {}
	
	public IntersectingSphereCompositor(ISphere... spherees) {
		super(spherees);
	}

	@Override
	protected RelationalFunction newComposingOperation() {
		return new IntersectingOperation();
	}

}
