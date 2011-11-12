/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.model;

public interface IExtendibleSourceRange {
    public int getOffset();
    public int getLength();
    
    public boolean contains(IExtendibleSourceRange sr);
    public boolean contains(int offset, int length);

    public void incorporate(int offset, int length);
    public void incorporate(IExtendibleSourceRange sr);

}
