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
package ca.ubc.cs.ferret.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.ubc.cs.ferret.model.ExtendibleSourceRange;
import ca.ubc.cs.ferret.model.IExtendibleSourceRange;
import org.junit.Test;

public class SourceRangeTests {
	@Test
    public void testBasics() {
        IExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        assertEquals(100, sr.getOffset());
        assertEquals(200, sr.getLength());
    }

	@Test
    public void testContains() {
         IExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
         assertTrue(sr.contains(new ExtendibleSourceRange(110, 20)));
         assertFalse(sr.contains(new ExtendibleSourceRange(90, 5)));
         assertFalse(sr.contains(new ExtendibleSourceRange(90, 20)));
         assertFalse(sr.contains(new ExtendibleSourceRange(300, 1)));
         assertTrue(sr.contains(new ExtendibleSourceRange(299, 1)));
    }
    
	@Test
    public void testIncorporate1() {
        ExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        sr.incorporate(new ExtendibleSourceRange(110, 120));
        assertEquals(100, sr.getOffset());
        assertEquals(200, sr.getLength());
    }
    
	@Test
    public void testIncorporate2() {
        ExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        sr.incorporate(new ExtendibleSourceRange(110, 200));
        assertEquals(100, sr.getOffset());
        assertEquals(210, sr.getLength());
    }
    
	@Test
	public void testIncorporate() {
        ExtendibleSourceRange sr = new ExtendibleSourceRange(100,200);
        sr.incorporate(new ExtendibleSourceRange(80, 90));
        assertEquals(80, sr.getOffset());
        assertEquals(220, sr.getLength());
    }
}
