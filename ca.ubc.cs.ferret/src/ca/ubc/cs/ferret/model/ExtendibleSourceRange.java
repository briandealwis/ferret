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

public class ExtendibleSourceRange implements IExtendibleSourceRange {
    protected int offset = 0;
    protected int length = 0;
    
    public ExtendibleSourceRange() {
    }

    public ExtendibleSourceRange(int _offset, int _length) {
        offset = _offset;
        length = _length;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public boolean contains(IExtendibleSourceRange sr) {
        return contains(sr.getOffset(), sr.getLength());
    }

    public boolean contains(int _offset, int _length) {
        return offset <= _offset && _offset + _length <= offset + length;
    }

    public void incorporate(int _offset, int _length) {
        if(_offset < offset) {
            length += offset - _offset;
            offset = _offset;
        }
        if(_offset + _length > offset + length) {
            length = _offset + _length - offset; 
        }
    }

    public void incorporate(IExtendibleSourceRange sr) {
        incorporate(sr.getOffset(), sr.getLength());
    }

}
