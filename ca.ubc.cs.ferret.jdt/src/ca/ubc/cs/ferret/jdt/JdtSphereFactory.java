package ca.ubc.cs.ferret.jdt;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.jdt.ops.AllSubclassesRelation;
import ca.ubc.cs.ferret.jdt.ops.AllSubtypesRelation;
import ca.ubc.cs.ferret.jdt.ops.AllSuperclassesRelation;
import ca.ubc.cs.ferret.jdt.ops.AllSuperinterfacesRelation;
import ca.ubc.cs.ferret.jdt.ops.AllSupertypesRelation;
import ca.ubc.cs.ferret.jdt.ops.CastsToTypeRelation;
import ca.ubc.cs.ferret.jdt.ops.CatchesExceptionRelation;
import ca.ubc.cs.ferret.jdt.ops.ClassMethodOverridesRelation;
import ca.ubc.cs.ferret.jdt.ops.ClassMethodsOverriddenRelation;
import ca.ubc.cs.ferret.jdt.ops.ClassesImplementingInterfaceRelation;
import ca.ubc.cs.ferret.jdt.ops.DeclaredMethodsRelation;
import ca.ubc.cs.ferret.jdt.ops.DeclaredTypesRelation;
import ca.ubc.cs.ferret.jdt.ops.DeclaringTypeRelation;
import ca.ubc.cs.ferret.jdt.ops.FieldGettersRelation;
import ca.ubc.cs.ferret.jdt.ops.FieldSettersRelation;
import ca.ubc.cs.ferret.jdt.ops.FieldsDeclaredRelation;
import ca.ubc.cs.ferret.jdt.ops.FieldsUsedRelation;
import ca.ubc.cs.ferret.jdt.ops.ImmediateSubclassesRelation;
import ca.ubc.cs.ferret.jdt.ops.ImmediateSubinterfacesRelation;
import ca.ubc.cs.ferret.jdt.ops.ImmediateSubtypesRelation;
import ca.ubc.cs.ferret.jdt.ops.ImmediateSupertypesRelation;
import ca.ubc.cs.ferret.jdt.ops.InstanceofRelation;
import ca.ubc.cs.ferret.jdt.ops.InstantiatingClassRelation;
import ca.ubc.cs.ferret.jdt.ops.InterfaceMethodImplementorsRelation;
import ca.ubc.cs.ferret.jdt.ops.InterfaceMethodsSpecifyingClassMethodsRelation;
import ca.ubc.cs.ferret.jdt.ops.JdtIsClassRelation;
import ca.ubc.cs.ferret.jdt.ops.JdtIsFieldRelation;
import ca.ubc.cs.ferret.jdt.ops.JdtIsInterfaceRelation;
import ca.ubc.cs.ferret.jdt.ops.JdtIsMethodRelation;
import ca.ubc.cs.ferret.jdt.ops.JdtMethodSignatureRelation;
import ca.ubc.cs.ferret.jdt.ops.MethodArgumentsWithTypeRelation;
import ca.ubc.cs.ferret.jdt.ops.FieldsOfTypeRelation;
import ca.ubc.cs.ferret.jdt.ops.MethodReferencesRelation;
import ca.ubc.cs.ferret.jdt.ops.MethodsCalledRelation;
import ca.ubc.cs.ferret.jdt.ops.MethodsReturningTypeRelation;
import ca.ubc.cs.ferret.jdt.ops.ProvidedTypesRelation;
import ca.ubc.cs.ferret.jdt.ops.ReferencesTypeRelation;
import ca.ubc.cs.ferret.jdt.ops.ShadowedFieldsRelation;
import ca.ubc.cs.ferret.jdt.ops.SiblingsRelation;
import ca.ubc.cs.ferret.jdt.ops.SuperclassRelation;
import ca.ubc.cs.ferret.jdt.ops.ThrowsExceptionRelation;
import ca.ubc.cs.ferret.jdt.ops.TypesReferencedRelation;
import ca.ubc.cs.ferret.model.AbstractSphereFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.NamedJoinRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.Sphere;

public class JdtSphereFactory extends AbstractSphereFactory {
	public static final String ID = JdtSphereFactory.class.getName();
	private static final String HCI_JDT_TB = "ca.ubc.cs.ferret.jdt.tb";
	
	public String getId() {
		return ID;
	}

	public String getDescription() {
		return "Java static program queries (JDT)";
	}
	
	public String[] getDependencies() {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	public IStatus canCreate() {
		return Status.OK_STATUS;
	}

	public ISphere createSphere(IProgressMonitor monitor) throws FerretConfigurationException {
		monitor.beginTask("Configuring JDT Relation Sphere", 1);
		Sphere tb = new Sphere("Java static program queries");
		// Given an operation name, an Sphere will iterate over all
		// the possibilities and find (and ensure there is only) one operation
		// that fits the provided objects.
		// Note: the following are right-to-left: register(X,Y) is register Y under name X
		// alias(X,Y) is alias Y as name X.
		tb.register(ObjectOrientedRelations.OP_IS_FIELD, new JdtIsFieldRelation());
		tb.register(ObjectOrientedRelations.OP_IS_METHOD, new JdtIsMethodRelation());
		tb.register(ObjectOrientedRelations.OP_IS_CLASS, new JdtIsClassRelation());
		tb.register(ObjectOrientedRelations.OP_IS_INTERFACE, new JdtIsInterfaceRelation());
		tb.register(ObjectOrientedRelations.OP_SIGNATURE, new JdtMethodSignatureRelation());
		tb.register(ObjectOrientedRelations.OP_DECLARING_TYPE, new DeclaringTypeRelation());

		tb.register(ObjectOrientedRelations.OP_METHOD_REFERENCES, new MethodReferencesRelation());
		tb.register(ObjectOrientedRelations.OP_TYPE_REFERENCES, new ReferencesTypeRelation());
		tb.register(ObjectOrientedRelations.OP_REFERENCES,
				new ReferencesTypeRelation(), new MethodReferencesRelation());

		tb.register(ObjectOrientedRelations.OP_METHOD_OVERRIDERS, new ClassMethodOverridesRelation());
		tb.register(ObjectOrientedRelations.OP_METHODS_OVERRIDDEN, new ClassMethodsOverriddenRelation());
		tb.register(ObjectOrientedRelations.OP_INTERFACE_IMPLEMENTORS, new ClassesImplementingInterfaceRelation()); 
		tb.register(ObjectOrientedRelations.OP_INTERFACE_METHOD_IMPLEMENTORS, new InterfaceMethodImplementorsRelation());
		// methods: "implementors" <-- "interface-method-implementors"
		// types: "implementors" <-- "interface-implementors",
		tb.register(ObjectOrientedRelations.OP_IMPLEMENTORS,
				new ClassesImplementingInterfaceRelation(),
				new InterfaceMethodImplementorsRelation()); 
		
		tb.register(ObjectOrientedRelations.OP_METHOD_SPECIFICATIONS, new InterfaceMethodsSpecifyingClassMethodsRelation());
		tb.register(ObjectOrientedRelations.OP_SPECIFICATIONS, 
				new InterfaceMethodsSpecifyingClassMethodsRelation(),
				//replaced: new AllSuperinterfacesRelation()
				new NamedJoinRelation(
						ObjectOrientedRelations.OP_IS_INTERFACE,
						ObjectOrientedRelations.OP_SUPERTYPES, 
						ObjectOrientedRelations.OP_IS_INTERFACE));

		tb.register(ObjectOrientedRelations.OP_METHODS_CALLED, new MethodsCalledRelation());
		tb.register(ObjectOrientedRelations.OP_TYPES_REFERENCED, new TypesReferencedRelation());
		tb.register(ObjectOrientedRelations.OP_FIELDS_USED, new FieldsUsedRelation());
		tb.register(ObjectOrientedRelations.OP_METHODS_WITH_ARGUMENT_OF_TYPE, new MethodArgumentsWithTypeRelation());
		tb.register(ObjectOrientedRelations.OP_FIELDS_OF_TYPE, new FieldsOfTypeRelation());
		
		tb.register(ObjectOrientedRelations.OP_GETTERS, new FieldGettersRelation());
		tb.register(ObjectOrientedRelations.OP_SETTERS, new FieldSettersRelation());
		tb.register(ObjectOrientedRelations.OP_SHADOWS, new ShadowedFieldsRelation());
		tb.register(ObjectOrientedRelations.OP_DECLARED_TYPES, new DeclaredTypesRelation());
		
		tb.register(ObjectOrientedRelations.OP_IMMEDIATE_SUPERTYPES, new ImmediateSupertypesRelation());
		tb.register(ObjectOrientedRelations.OP_IMMEDIATE_SUBTYPES, new ImmediateSubtypesRelation());
		tb.register(ObjectOrientedRelations.OP_SUPERTYPES, new AllSupertypesRelation());
		tb.register(ObjectOrientedRelations.OP_SUBTYPES, new AllSubtypesRelation());

		tb.register(ObjectOrientedRelations.OP_SIBLINGS, new SiblingsRelation());
		tb.register(ObjectOrientedRelations.OP_IMMEDIATE_SUBCLASSES, new NamedJoinRelation(
				ObjectOrientedRelations.OP_IS_CLASS,
				ObjectOrientedRelations.OP_IMMEDIATE_SUBTYPES, 
				ObjectOrientedRelations.OP_IS_CLASS));
		tb.register(ObjectOrientedRelations.OP_SUPERCLASS,  new NamedJoinRelation(
				ObjectOrientedRelations.OP_IS_CLASS,
				ObjectOrientedRelations.OP_IMMEDIATE_SUPERTYPES, 
				ObjectOrientedRelations.OP_IS_CLASS));
		tb.register(ObjectOrientedRelations.OP_SUPERCLASSES, new NamedJoinRelation(
				ObjectOrientedRelations.OP_IS_CLASS,
				ObjectOrientedRelations.OP_SUPERTYPES, 
				ObjectOrientedRelations.OP_IS_CLASS));
		tb.register(ObjectOrientedRelations.OP_SUPERINTERFACES, new NamedJoinRelation(
				ObjectOrientedRelations.OP_IS_INTERFACE,
				ObjectOrientedRelations.OP_SUPERTYPES, 
				ObjectOrientedRelations.OP_IS_INTERFACE));
		tb.register(ObjectOrientedRelations.OP_SUBCLASSES, new NamedJoinRelation(
				ObjectOrientedRelations.OP_IS_CLASS,
				ObjectOrientedRelations.OP_SUBTYPES, 
				ObjectOrientedRelations.OP_IS_CLASS));
		tb.register(ObjectOrientedRelations.OP_SUBINTERFACES, new NamedJoinRelation(
				ObjectOrientedRelations.OP_IS_INTERFACE,
				ObjectOrientedRelations.OP_SUBTYPES, 
				ObjectOrientedRelations.OP_IS_INTERFACE));

		// "instantiators" <-- "class-instantiators", "interface-instantiators"
		tb.register(ObjectOrientedRelations.OP_CLASS_INSTANTIATORS, new InstantiatingClassRelation());
		tb.register(ObjectOrientedRelations.OP_INTERFACE_INSTANTIATORS, 
				new NamedJoinRelation(ObjectOrientedRelations.OP_INTERFACE_IMPLEMENTORS, ObjectOrientedRelations.OP_CLASS_INSTANTIATORS));
		tb.register(ObjectOrientedRelations.OP_INSTANTIATORS, 
				new InstantiatingClassRelation(),
				new NamedJoinRelation(ObjectOrientedRelations.OP_INTERFACE_IMPLEMENTORS, ObjectOrientedRelations.OP_CLASS_INSTANTIATORS));
		tb.register(ObjectOrientedRelations.OP_PROVIDED_TYPES, new ProvidedTypesRelation());
		tb.register(ObjectOrientedRelations.OP_DECLARED_METHODS, new DeclaredMethodsRelation());

		tb.register(ObjectOrientedRelations.OP_FIELDS_DECLARED, new FieldsDeclaredRelation());
		tb.register(ObjectOrientedRelations.OP_CASTS_TO_TYPE, new CastsToTypeRelation());
		
		tb.register(ObjectOrientedRelations.OP_METHOD_RETURNS_TYPE, new MethodsReturningTypeRelation());
		tb.register(ObjectOrientedRelations.OP_COVARIANT_RETURNS_TYPE,
				new NamedJoinRelation(ObjectOrientedRelations.OP_SUBTYPES, ObjectOrientedRelations.OP_METHOD_RETURNS_TYPE));
		tb.register(ObjectOrientedRelations.OP_CONTRAVARIANT_RETURNS_TYPE,
				new NamedJoinRelation(ObjectOrientedRelations.OP_SUPERTYPES, ObjectOrientedRelations.OP_METHOD_RETURNS_TYPE));

		tb.register(ObjectOrientedRelations.OP_THROWS_EXCEPTION, new ThrowsExceptionRelation());
		tb.register(ObjectOrientedRelations.OP_CATCHES_EXCEPTION, new CatchesExceptionRelation());
		tb.register(ObjectOrientedRelations.OP_INSTANCEOF, new InstanceofRelation());
		monitor.done();
		return tb;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public String toString() {
		return getDescription();
	}

	public ImageDescriptor getImageDescriptor() {
		return FerretJdtPlugin.getImageDescriptor("icons/jdt-tb.gif");
	}

	public String getHelpContextId() {
		return HCI_JDT_TB;
	}
}
