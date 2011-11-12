package ca.ubc.cs.ferret.model;

public class NameAlreadyRegisteredException extends RuntimeException {

	public NameAlreadyRegisteredException(String message) {
		super(message);
	}

}
