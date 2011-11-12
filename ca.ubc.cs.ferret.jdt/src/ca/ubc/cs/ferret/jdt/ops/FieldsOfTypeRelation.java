package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IType;

import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.NamedJoinRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.types.FerretObject;

public class FieldsOfTypeRelation extends AbstractCollectionBasedRelation<IType> {

	public FieldsOfTypeRelation() {}

	@Override
	protected Class<IType> getInputType() {
		return IType.class;
	}

	@Override
	protected Collection<?> realizeCollection(IType input) {
		monitor.beginTask("FieldsOfTypeRelation", 10);
		IRelation potentialFields = 
			new NamedJoinRelation(ObjectOrientedRelations.OP_TYPE_REFERENCES,
					ObjectOrientedRelations.OP_IS_FIELD)
						.resolve(new SubProgressMonitor(monitor,5), resolver.getRootSphere(), input);

		Set<FerretObject> results = new HashSet<FerretObject>();
		IProgressMonitor subm = new SubProgressMonitor(monitor, 5);
		for(FerretObject field : potentialFields) {
			if(checkField(field, input, new SubProgressMonitor(subm, IProgressMonitor.UNKNOWN))) {
				results.add(field);
			}
		}
		monitor.done();
		return results;
	}

	private boolean checkField(FerretObject field, IType declaredType,
			IProgressMonitor mp) {
		IRelation declTypes = resolver.getRootSphere().resolve(new SubProgressMonitor(mp, IProgressMonitor.UNKNOWN),
				ObjectOrientedRelations.OP_DECLARED_TYPES, field);
		if(FerretObject.contains(declTypes.asCollection(), declaredType)) { return true; }
//		System.out.println("uh oh, type ref for field but not parm: ");
//		System.out.println("  type: " + FerretPlugin.prettyPrint(declaredType));
//		System.out.println("  field: " + FerretPlugin.prettyPrint(field));
//		try {
//			System.out.println("  source: " + ((IField)field.getPrimaryObject()).getSource());
//		} catch (JavaModelException e) {
//			System.out.println("  (couldn't retrieve source): " + e);
//		}
		return false;
	}

}
