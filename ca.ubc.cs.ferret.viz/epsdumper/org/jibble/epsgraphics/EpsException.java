/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of EpsGraphics2D.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: bsd $
$Id: EpsException.java,v 1.1 2007/09/20 19:11:12 bsd Exp $

*/

package org.jibble.epsgraphics;

public class EpsException extends RuntimeException {
    
    public EpsException(String message) {
        super(message);
    }
    
}
