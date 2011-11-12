package ca.ubc.cs.ferret.model;


public class DisjunctingSphereCompositor extends FunctionalSphereCompositor {

	public DisjunctingSphereCompositor() {	}

	public DisjunctingSphereCompositor(ISphere... spheres) {
		super(spheres);
	}

	@Override
	protected RelationalFunction newComposingOperation() {
		return new DisjunctingOperation();
	}
}
