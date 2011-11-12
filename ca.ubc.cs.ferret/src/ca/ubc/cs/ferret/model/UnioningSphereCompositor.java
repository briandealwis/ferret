package ca.ubc.cs.ferret.model;


public class UnioningSphereCompositor extends FunctionalSphereCompositor {

	public UnioningSphereCompositor() {
	}

	public UnioningSphereCompositor(ISphere... spheres) {
		super(spheres);
	}

	@Override
	protected RelationalFunction newComposingOperation() {
		return new UnioningOperation();
	}
}
