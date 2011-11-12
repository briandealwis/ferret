package ca.ubc.cs.objhdl;

public class NullObjectMapper implements IObjectMapper {
	public static final String HANDLE_TYPE_UNKNOWN = "unknown";

	public String[] describe(Object object) {
		return new String[] { HANDLE_TYPE_UNKNOWN,
				object.toString() };
	}

	public String[] getHandleTypes() {
		return new String[] { HANDLE_TYPE_UNKNOWN };
	}

	public Object resolve(String handleType, String description) {
		return description;
	}

}
