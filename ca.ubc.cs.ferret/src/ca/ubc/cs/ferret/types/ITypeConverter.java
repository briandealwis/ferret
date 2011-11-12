package ca.ubc.cs.ferret.types;

import ca.ubc.cs.ferret.model.ISphere;

public interface ITypeConverter {
	ConversionResult<?> convert(Object objectInstance,
			ConversionSpecification spec, ISphere sphere)
			throws ConversionException;
}
