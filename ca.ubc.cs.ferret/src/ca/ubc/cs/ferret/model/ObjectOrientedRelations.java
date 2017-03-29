/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.model;

public interface ObjectOrientedRelations {

	public static final String OP_METHOD_REFERENCES = "OoMethodReferences";
	public static final String OP_TYPE_REFERENCES = "OoReferencesType";
	public static final String OP_REFERENCES = "OoReferences";
	
	public static final String OP_IS_FIELD = "OoIsField";
	public static final String OP_IS_METHOD = "OoIsMethod";
	public static final String OP_IS_CLASS = "OoIsClass";
	public static final String OP_IS_INTERFACE = "OoIsInterface";
	public static final String OP_SIGNATURE = "OoSignature";
	
	public static final String OP_METHODS_CALLED = "OoMethodsCalled";
	public static final String OP_TYPES_REFERENCED = "OoTypesReferenced";
	public static final String OP_FIELDS_USED = "OoFieldsUsed";
	/**
	 * Return list of things that implement the interfaces represented by the inputs.
	 */
	public static final String OP_IMPLEMENTORS = "OoImplementors";
	public static final String OP_INTERFACE_IMPLEMENTORS = "OoInterfaceImplementors";
	public static final String OP_INTERFACE_METHOD_IMPLEMENTORS = "OoInterfaceMethodImplementors";
	public static final String OP_METHOD_OVERRIDERS = "OoMethodOverriders";
	public static final String OP_METHODS_OVERRIDDEN = "OoMethodsOverridden";
	/**
	 * Return the list of interfaces that are implemented by the inputs. 
	 */
	public static final String OP_SPECIFICATIONS = "OoSpecifications";
	public static final String OP_TYPE_SPECIFICATIONS = "OoTypeSpecifications";
	public static final String OP_METHOD_SPECIFICATIONS = "OoMethodSpecifications";
	public static final String OP_SUBTYPES = "OoSubtypes";
	public static final String OP_SUBCLASSES = "OoSubclasses";
	public static final String OP_IMMEDIATE_SUBCLASSES = "OoImmediateSubclasses";
	public static final String OP_SUBINTERFACES = "OoSubinterfaces";
	public static final String OP_SUPERTYPES = "OoSupertypes";
	public static final String OP_SUPERCLASSES = "OoSuperclasses";
	public static final String OP_SUPERINTERFACES = "OoSuperinterfaces";
	public static final String OP_SUPERCLASS = "OoSuperclass";
	public static final String OP_SIBLINGS = "OoSiblings";
	public static final String OP_CLASS_INSTANTIATORS = "OoClassInstantiators";
	public static final String OP_INTERFACE_INSTANTIATORS = "OoInterfaceInstantiators";
	public static final String OP_INSTANTIATORS = "OoInstantiators";
	/**
	 * Declaration specifies return type.
	 */
	public static final String OP_METHOD_RETURNS_TYPE = "OoReturnsType";
	public static final String OP_CONTRAVARIANT_RETURNS_TYPE = "OoContravariantReturnsType";
	public static final String OP_COVARIANT_RETURNS_TYPE = "OoCovariantReturnsType";
	public static final String OP_METHODS_WITH_ARGUMENT_OF_TYPE = "OoMethodsWithArgOfType";
	public static final String OP_FIELDS_OF_TYPE = "OoFieldsOfType";
	public static final String OP_THROWS_EXCEPTION = "OoThrowsException";
	public static final String OP_CATCHES_EXCEPTION = "OoCatchesException";
	public static final String OP_INSTANCEOF = "OoInstanceof";
	public static final String OP_CASTS_TO_TYPE = "OoCastsToType";
	public static final String OP_PROVIDED_TYPES = "OoProvidedTypes";
	public static final String OP_DECLARED_TYPES = "OoDeclaredType";
	public static final String OP_FIELDS_DECLARED = "OoDeclaredFields";
	public static final String OP_GETTERS = "OoFieldGetters";
	public static final String OP_SETTERS = "OoFieldSetters";
	public static final String OP_SHADOWS = "OoFieldShadows";
	public static final String OP_DECLARED_METHODS = "OoDeclaredMethods";
	public static final String OP_IMMEDIATE_SUPERTYPES = "OoImmediateSupertypes";
	public static final String OP_IMMEDIATE_SUBTYPES = "OoImmediateSubtypes";
	public static final String OP_DECLARING_TYPE = "OoDeclaringType";

}
