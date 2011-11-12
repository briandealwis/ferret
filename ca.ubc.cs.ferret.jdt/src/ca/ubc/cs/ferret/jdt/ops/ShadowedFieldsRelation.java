package ca.ubc.cs.ferret.jdt.ops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.core.IJavaVariable;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class ShadowedFieldsRelation extends
		AbstractCollectionBasedRelation<IJavaElement> {

	public ShadowedFieldsRelation() {}

	@Override
	protected Class<IJavaElement> getInputType() {
		return IJavaElement.class;
	}

	@Override
	protected IJavaElement checkInput(IJavaElement input) {
		return (input instanceof IField || input instanceof ILocalVariable || input instanceof IJavaVariable) ? input : null;
	}

	@Override
	protected Collection<?> realizeCollection(IJavaElement input) {
		IType startPoint = null;
		String fieldName = null;
		monitor.beginTask("Finding shadowed variables of " + FerretPlugin.prettyPrint(input), IProgressMonitor.UNKNOWN);
		if(input instanceof IField) {
			startPoint = JavaModelHelper.getDefault().getSuperclass(((IField)input).getDeclaringType(), new SubProgressMonitor(monitor, 1));
			fieldName = ((IField)input).getElementName();
		} else if(input instanceof ILocalVariable) {
			startPoint = (IType)((ILocalVariable)input).getAncestor(IJavaElement.TYPE);
			fieldName = ((ILocalVariable)input).getElementName();
		}
		Collection<IField> shadowed = new ArrayList<IField>();
		while(startPoint != null) {
			IField f = startPoint.getField(fieldName);
			if(f != null && f.exists()) { shadowed.add(f); }
			startPoint = JavaModelHelper.getDefault().getSuperclass(startPoint, new SubProgressMonitor(monitor, 1));
		}
		monitor.done();
		return shadowed;
	}
}
