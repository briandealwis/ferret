/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.model;

/**
 * Simple solutions have a single result object; other relations may add detail
 * to the solution.  There is presumably a single relation between the queryElements
 * and this result.
 */
public class SimpleSolution  extends AbstractSolution {
    
    public SimpleSolution(IConceptualQuery query, Object _schema) {
        super(query, _schema);
    }
    
    public boolean isSimpleSolution() {
        return super.isSimpleSolution();
    }
}
