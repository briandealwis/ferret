package ca.ubc.cs.ferret.types;

@SuppressWarnings("serial")
public class ConversionException extends Exception {
	protected Object relatedObject;
	
	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(String message, Object obj) {
		super(message);
		relatedObject = obj;
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

	public Object getRelatedObject() {
		return relatedObject;
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
}
