
package ca.ubc.cs.ferret.jdt.attributes;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Classify members based on the type of their defining type.
 */
public class MemberClassVsInterfaceProvider extends AbstractJavaAttributeValue<IMember, String> {
	private static final String CLASS = "class";
	private static final String INTERFACE = "interface";
	private static final String ENUM = "enum";
	private static final String ANNOTATION = "annotation";
	private static final String LAMBDA = "lambda";
	private static final String[] CATEGORIES = { CLASS, INTERFACE, ENUM, ANNOTATION, LAMBDA };

	public String[] getCategories() {
		return CATEGORIES;
	}

	public String getCategory(IMember element) {
		try {
			if (!(element instanceof IType)) {
				element = ((IMember) element).getDeclaringType();
			}
			IType type = (IType) element;
			// Enums and annotations are seen as classes/interfaces
			if (type.isEnum()) {
				return ENUM;
			} else if (type.isAnnotation()) {
				return ANNOTATION;
			} else if (type.isLambda()) {
				return LAMBDA;
			} else if (type.isInterface()) {
				return INTERFACE;
			} else if (type.isClass()) {
				return CLASS;
			}
		} catch (JavaModelException e) {
			log(e);
		}
		return UNDETERMINED_ATTRIBUTE_VALUE;
	}

	public ImageDescriptor getCategoryImage(String category) {
		switch (category) {
		case CLASS:
			return JavaUI.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_CLASS);
		case INTERFACE:
			return JavaUI.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INTERFACE);
		case ENUM:
			return JavaUI.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_ENUM);
		case ANNOTATION:
			return JavaUI.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_ANNOTATION);
		case LAMBDA: // no direct image
			return JavaUI.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INNER_CLASS_PRIVATE);
		default:
			return null;
		}
	}

	public String getCategoryText(String category) {
		return category;
	}
}
