package ca.ubc.cs.ferret.tptp.attributes;

import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCLanguageElement;
import org.eclipse.hyades.models.trace.TRCMethod;

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class DeclaringTypeClassifier extends
		AbstractClassifier<TRCLanguageElement, TRCClass> {

	public TRCClass[] getCategories() {
		return null;
	}

	public TRCClass getCategory(TRCLanguageElement object) {
		if(object instanceof TRCMethod) {
			return ((TRCMethod)object).getDefiningClass();
		}
		if (object instanceof TRCClass) {
			return ((TRCClass)object).getEnclosedBy();
		}
		return null;
	}

}
