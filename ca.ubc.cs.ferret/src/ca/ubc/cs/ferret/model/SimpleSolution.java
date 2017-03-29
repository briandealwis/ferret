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
