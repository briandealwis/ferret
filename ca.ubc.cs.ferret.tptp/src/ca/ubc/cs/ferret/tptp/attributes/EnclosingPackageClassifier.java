package ca.ubc.cs.ferret.tptp.attributes;

import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCLanguageElement;
import org.eclipse.hyades.models.trace.TRCMethod;

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class EnclosingPackageClassifier extends
		AbstractClassifier<TRCLanguageElement, String> {

	public String[] getCategories() {
		return null;
	}

	public String getCategory(TRCLanguageElement object) {
		if(object instanceof TRCMethod) {
			object = ((TRCMethod)object).getDefiningClass();
		}
		if (object instanceof TRCClass) {
			return ((TRCClass)object).getPackage().getName();
		}
		return null;
	}

}
