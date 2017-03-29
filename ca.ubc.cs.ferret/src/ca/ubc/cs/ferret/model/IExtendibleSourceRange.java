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

public interface IExtendibleSourceRange {
    public int getOffset();
    public int getLength();
    
    public boolean contains(IExtendibleSourceRange sr);
    public boolean contains(int offset, int length);

    public void incorporate(int offset, int length);
    public void incorporate(IExtendibleSourceRange sr);

}
