/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.model;

public class Fact extends AbstractSolution {
    public String fact;
    
    public Fact(IConceptualQuery q, String _fact) {
        super(q);
        fact = _fact;
    }

    public String getFact() {
        return fact;
    }
    
    public Object getEntity(String slotName) {
        throw new IllegalStateException("Facts have no entities");
    }
    
    public String toString() {
        return fact;
    }
}
