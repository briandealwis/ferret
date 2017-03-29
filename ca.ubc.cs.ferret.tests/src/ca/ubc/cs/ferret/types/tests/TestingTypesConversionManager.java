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

import ca.ubc.cs.ferret.types.ConversionSpecification;
import ca.ubc.cs.ferret.types.TypesConversionManager;
import edu.uci.ics.jung.graph.DirectedGraph;

public class TestingTypesConversionManager extends TypesConversionManager {

	public TestingTypesConversionManager() {
		getConversionGraph();
	}
	
	public DirectedGraph<String,ConversionSpecification> testGetConversionGraph() {
		return getConversionGraph();
	}
	
	public void testAddConversionSpec(ConversionSpecification cs) {
		addConversionSpec(cs);
	}
	
	
}
