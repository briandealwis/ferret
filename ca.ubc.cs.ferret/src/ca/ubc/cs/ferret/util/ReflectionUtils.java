/*******************************************************************************
 * Copyright (c) 2017 Brian de Alwis, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/


package ca.ubc.cs.ferret.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility methods for reflective access and invokation.
 */
public class ReflectionUtils {
	/** Invoke a method without regard to its return type. */
	public static void invokeVoid(Object object, String methodName, Class<?>[] parameterTypes, Object... parameters)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		method.invoke(object, parameters);
	}

	// not intended to be created
	private ReflectionUtils() {
	}
}
