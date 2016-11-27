/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import ca.ubc.cs.ferret.Consultancy;
import ca.ubc.cs.ferret.display.DwConceptualQuery;
import ca.ubc.cs.ferret.display.DwConsultation;
import ca.ubc.cs.ferret.display.DwObject;
import ca.ubc.cs.ferret.display.DwSolution;
import ca.ubc.cs.ferret.display.IDisplayObject;
import ca.ubc.cs.ferret.model.Consultation;
import ca.ubc.cs.ferret.model.IConceptualQuery;
import ca.ubc.cs.ferret.model.Sphere;

public class DisplayObjectTests {
    @Test
    public void testModel() {
        GraduateStudent me = findGrad("Brian");
        assertNotNull(me);
        Consultation c = Consultancy.getDefault().getConsultation(new Object[] {me }, new Sphere("test"));
        Consultancy.getDefault().performConsultation(c);
        try { while(!c.isDone()) { Thread.sleep(500); } } catch(InterruptedException e) {}
        assertTrue(c.isDone());
        
        GradSiblings siblings = null;
        for(IConceptualQuery q : c.getConceptualQueries()) {
        	if(q instanceof GradSiblings) {
        		siblings = (GradSiblings)q; 
        	}
        }
        assertNotNull(siblings);

        // There are five other students, not including me.
        assertEquals(5, siblings.getSolutions().size());
        
        IDisplayObject o = new DwConsultation(c);
        for(IDisplayObject child : o.getChildren()) {
            assertTrue(child instanceof IDisplayObject);
            assertTrue(child instanceof DwConceptualQuery);
            ((DwConceptualQuery)child).setActiveClustering(null);    // no clustering!
            for(IDisplayObject sol : child.getChildren()) {
                assertTrue(sol instanceof IDisplayObject);
                assertTrue(sol instanceof DwSolution || sol instanceof DwObject);
                ensureProperChildren(sol, 0, 5);
            }
        }
        o.dispose();
    }
    
    private void ensureProperChildren(IDisplayObject sol, int currentLevel, int maxLevel) {
        if(currentLevel >= maxLevel) { return; }
        for(IDisplayObject child : sol.getChildren()) {
            assertTrue(child instanceof IDisplayObject);
            ensureProperChildren(child, currentLevel + 1, maxLevel);
        }
    }

    protected GraduateStudent findGrad(String name) {
        Set<GraduateStudent> students = CategorizationTest.getPeople();
        GraduateStudent s;
        for(GraduateStudent student : students) {
            if(student.name.equals(name)) { return student; }
        }
        return null;
    }
}
