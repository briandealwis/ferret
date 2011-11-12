package ca.ubc.cs.ferret.tests;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.clustering.attrs.IClassifier;

public class GradStudentDegreeClassifier implements
		IClassifier<GraduateStudent, String> {
	
	public String[] getCategories() {
		return new String[] { "MSc", "PhD" };
	}

	public String getCategory(GraduateStudent object) {
		return object.degree;
	}

	public ImageDescriptor getCategoryImage(String category) {
		return null;
	}

	public String getCategoryText(String category) {
		return category;
	}

}
