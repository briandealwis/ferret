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
