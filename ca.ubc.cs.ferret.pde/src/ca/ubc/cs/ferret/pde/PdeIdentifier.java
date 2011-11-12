package ca.ubc.cs.ferret.pde;

public class PdeIdentifier {
	protected String identifier;
	
	public PdeIdentifier(String id) {
		identifier = id;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PdeIdentifier && identifier.equals(((PdeIdentifier)obj).identifier);
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
	

}
