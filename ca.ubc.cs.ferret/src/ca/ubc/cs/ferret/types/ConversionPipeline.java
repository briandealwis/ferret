package ca.ubc.cs.ferret.types;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class ConversionPipeline {
	protected List<ConversionSpecification> pipeline;
	protected Fidelity fidelity;
	
	public ConversionPipeline(List<ConversionSpecification> specs) {
		assert !specs.isEmpty();
		pipeline = specs;
		fidelity = specs.get(specs.size() - 1).getFidelity();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for(ConversionSpecification spec : pipeline) {
			buffer.append('{');
			buffer.append(spec.getProvidedType());
			buffer.append("->");
			buffer.append(spec.getDesiredType());
			buffer.append('}');
		}
		return buffer.toString();
	}

	public ConversionResult<?> convert(Object object, ISphere sphere) {
		try {
			ConversionResult<?> result = ConversionResult.forObject(object);
			for(ConversionSpecification spec : pipeline) {
				if(!result.hasSingleResult() || 
						(result = spec.convert(result.getSingleResult(), sphere)) == null) {
					return null;
				}
			}
			return result;
		} catch (CoreException e) {
			FerretPlugin.log(e);
		} catch (ConversionException e) {
			if(FerretPlugin.hasDebugOption("debug/showTypeConversions")) {
				FerretPlugin.log(e);
			}
		}
		return null;
	}

	public int size() {
		return pipeline.size();
	}
	
}
