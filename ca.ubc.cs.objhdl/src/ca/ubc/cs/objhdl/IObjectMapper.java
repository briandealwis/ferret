package ca.ubc.cs.objhdl;

public interface IObjectMapper {
	/**
	 * Return the object types supported by this mapper.
	 * @return the type description ids
	 */
	public String[] getHandleTypes();
	
	/**
	 * Return a description of this object, suitable for providing to
	 * {@link resolve(String,String)}. Result is a two-element string 
	 * array, whose first element is the handle type, and whose second
	 * element is the handle identifier.  If this mapper is unable to
	 * describe the object, it may return null.
	 * @param o the object to describe
	 * @return a two-element string array, or null
	 */
	public String[] describe(Object o);

	/**
	 * Attempt to resolve the object described by the arguments.
	 * Return null if the described object could not be resolved.
	 * @param handleType the handle type
	 * @param handle the handle-specific handle description
	 * @return the mapped object, if found
	 */
	public Object resolve(String handleType, String description); 
}
