package ca.ubc.cs.ferret.tests;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.clustering.attrs.IClassifier;

public class GradStudentSupervisorClassifier implements IClassifier<GraduateStudent,String> {

	public String[] getCategories() {
		return null;
	}

	public String getCategory(GraduateStudent object) {
		return object.supervisor;
	}

	public ImageDescriptor getCategoryImage(String category) {
		return null;
	}

	public String getCategoryText(String category) {
		return category;
	}
}
