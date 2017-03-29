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
package ca.ubc.cs.ferret.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

// Copied from org.eclipse.jdt.ui.internal
public class ImageImageDescriptor extends ImageDescriptor {
    private Image fImage;
    
    /**
     * Constructor for ImagImageDescriptor.
     */
    public ImageImageDescriptor(Image image) {
        super();
        fImage = image;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
        return (obj != null) && getClass().equals(obj.getClass())
        && fImage.equals(((ImageImageDescriptor) obj).fImage);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see ImageDescriptor#getImageData()
     */
    public ImageData getImageData() {
        return fImage.getImageData();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see Object#hashCode()
     */
    public int hashCode() {
        return fImage.hashCode();
    }
}
