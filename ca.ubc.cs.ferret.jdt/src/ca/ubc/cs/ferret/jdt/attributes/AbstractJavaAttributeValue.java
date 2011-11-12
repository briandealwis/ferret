package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.FerretJdtPlugin;
import ca.ubc.cs.ferret.util.AbstractClassifier;

public abstract class AbstractJavaAttributeValue<T,C> extends AbstractClassifier<T,C> {

	protected void log(Exception e) {
		if(!(e instanceof JavaModelException) || 
				FerretJdtPlugin.logJavaModelExceptions()) {
			FerretPlugin.log(e);
		}
	}
}
