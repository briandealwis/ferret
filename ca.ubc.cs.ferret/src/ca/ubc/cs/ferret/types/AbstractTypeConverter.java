package ca.ubc.cs.ferret.types;

import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public abstract class AbstractTypeConverter implements ITypeConverter {
	protected <T> ConversionResult<T> wrap(ConversionSpecification conversionContext,
			Fidelity conversionFidelity, Class<T> convClass, T converted) {
		ConversionResult<T> result = new ConversionResult<T>(convClass, conversionFidelity);
		if(converted != null) {
			result.addResult(converted);
		}
		return result;
	}


}
