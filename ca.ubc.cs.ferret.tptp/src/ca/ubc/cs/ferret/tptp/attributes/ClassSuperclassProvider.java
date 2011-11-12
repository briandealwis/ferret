package ca.ubc.cs.ferret.tptp.attributes;

import org.eclipse.hyades.models.trace.TRCClass;

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class ClassSuperclassProvider extends
		AbstractClassifier<TRCClass, TRCClass> {

	public TRCClass[] getCategories() {
		return null;
	}

	public TRCClass getCategory(TRCClass object) {
		for(Object o : object.getExtends()) {
			if(!((TRCClass)o).isInterface()) { return (TRCClass)o; }
		}
		return null;
	}

}
