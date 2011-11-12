package ca.ubc.cs.ferret.tptp.attributes;

import org.eclipse.hyades.models.trace.TRCMethod;

import ca.ubc.cs.ferret.util.AbstractClassifier;

public class MethodReturnTypeProvider extends
		AbstractClassifier<TRCMethod, String> {

	public String[] getCategories() {
		return null;
	}

	public String getCategory(TRCMethod object) {
		String signature = object.getSignature();
		int rParenIndex = signature.lastIndexOf(')') + 1;
		signature = signature.substring(rParenIndex).trim(); 
		return signature.length() == 0 ? "(constructor)" : signature;
	}
	
}
