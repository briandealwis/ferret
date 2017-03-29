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
package ca.ubc.cs.ferret.types.tests;

import ca.ubc.cs.ferret.types.ConversionPipeline;
import ca.ubc.cs.ferret.types.ConversionSpecification;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.ferret.types.TypesConversionManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import junit.framework.TestCase;

public class TypesConversionManagerTest extends TestCase {
	
	@Override
	protected void tearDown() throws Exception {
		TypesConversionManager.stop();
		super.tearDown();
	}

	public void testFidelityComparison() {
		assertTrue(Fidelity.Approximate.compareTo(Fidelity.Exact) < 0);
		assertTrue(Fidelity.Approximate.compareTo(Fidelity.Equivalent) < 0);
		assertTrue(Fidelity.Approximate.compareTo(Fidelity.Approximate) == 0);
		assertTrue(Fidelity.Equivalent.compareTo(Fidelity.Approximate) > 0);
		assertTrue(Fidelity.Equivalent.compareTo(Fidelity.Equivalent) == 0);
		assertTrue(Fidelity.Equivalent.compareTo(Fidelity.Exact) < 0);
		assertTrue(Fidelity.Exact.compareTo(Fidelity.Approximate) > 0);
		assertTrue(Fidelity.Exact.compareTo(Fidelity.Equivalent) > 0);
		assertTrue(Fidelity.Exact.compareTo(Fidelity.Exact) == 0);
		
		assertEquals(Fidelity.Approximate.least(Fidelity.Exact), Fidelity.Approximate);
		assertEquals(Fidelity.Approximate.least(Fidelity.Equivalent), Fidelity.Approximate);
		assertEquals(Fidelity.Approximate.least(Fidelity.Approximate), Fidelity.Approximate);
		assertEquals(Fidelity.Equivalent.least(Fidelity.Exact), Fidelity.Equivalent);
		assertEquals(Fidelity.Equivalent.least(Fidelity.Equivalent), Fidelity.Equivalent);
		assertEquals(Fidelity.Equivalent.least(Fidelity.Approximate), Fidelity.Approximate);
		assertEquals(Fidelity.Exact.least(Fidelity.Exact), Fidelity.Exact);
		assertEquals(Fidelity.Exact.least(Fidelity.Equivalent), Fidelity.Equivalent);
		assertEquals(Fidelity.Exact.least(Fidelity.Approximate), Fidelity.Approximate);
	}
	
	public void testLookup() {
		TestingTypesConversionManager tcm =  new TestingTypesConversionManager();
		tcm.testAddConversionSpec(
				new ConversionSpecification("java.util.Set", "java.util.List",
						Fidelity.Exact, null));
		assertNotNull(tcm.findConversion(HashSet.class, "java.util.List", Fidelity.Exact));
		assertNull(tcm.findConversion(Object.class, "java.util.List", Fidelity.Exact));
		assertNull(tcm.findConversion(HashMap.class, "java.util.List", Fidelity.Exact));
	}
	
	public void testEquivalencePromotion() {
		TestingTypesConversionManager tcm =  new TestingTypesConversionManager();
		tcm.testAddConversionSpec(
				new ConversionSpecification("java.util.Set", "java.util.List",
						Fidelity.Equivalent, null));
		assertNotNull(tcm.findConversion(HashSet.class, "java.util.List", Fidelity.Approximate));
		assertNotNull(tcm.findConversion(HashSet.class, "java.util.List", Fidelity.Equivalent));
		assertNull(tcm.findConversion(HashSet.class, "java.util.List", Fidelity.Exact));
	}

	public void testMultiPaths() {
		TestingTypesConversionManager tcm =  new TestingTypesConversionManager();
		tcm.testAddConversionSpec(
				new ConversionSpecification("java.util.Set", "java.util.List",
						Fidelity.Equivalent, null));
		tcm.testAddConversionSpec(
				new ConversionSpecification("java.lang.Double", "java.util.Set",
						Fidelity.Equivalent, null));
		List<ConversionPipeline> pipelines = 
			tcm.findConversion(Double.class, "java.util.List", Fidelity.Approximate);
		assertNotNull(pipelines);
		assertEquals(1, pipelines.size());
		assertEquals(2, pipelines.get(0).size());
	}
}
