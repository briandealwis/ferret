/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.tests;

import junit.framework.TestCase;
import ca.ubc.cs.ferret.model.ExtendibleSourceRange;
import ca.ubc.cs.ferret.model.IExtendibleSourceRange;

public class SourceRangeTests extends TestCase {
    public void testBasics() {
        IExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        assertEquals(100, sr.getOffset());
        assertEquals(200, sr.getLength());
    }

    public void testContains() {
         IExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
         assertTrue(sr.contains(new ExtendibleSourceRange(110, 20)));
         assertFalse(sr.contains(new ExtendibleSourceRange(90, 5)));
         assertFalse(sr.contains(new ExtendibleSourceRange(90, 20)));
         assertFalse(sr.contains(new ExtendibleSourceRange(300, 1)));
         assertTrue(sr.contains(new ExtendibleSourceRange(299, 1)));
    }
    
    public void testIncorporate1() {
        ExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        sr.incorporate(new ExtendibleSourceRange(110, 120));
        assertEquals(100, sr.getOffset());
        assertEquals(200, sr.getLength());
    }
    
    public void testIncorporate2() {
        ExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        sr.incorporate(new ExtendibleSourceRange(110, 200));
        assertEquals(100, sr.getOffset());
        assertEquals(210, sr.getLength());
    }
    
    public void testIncorporate() {
        ExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        sr.incorporate(new ExtendibleSourceRange(80, 90));
        assertEquals(80, sr.getOffset());
        assertEquals(220, sr.getLength());
    }
}