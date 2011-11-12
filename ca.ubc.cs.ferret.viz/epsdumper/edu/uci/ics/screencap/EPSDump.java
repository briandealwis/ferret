/*
 * Created on Mar 3, 2004
 */
package edu.uci.ics.screencap;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jibble.epsgraphics.EpsGraphics2D;
/**
 * An EPS capture has the wonderful advantage of being able
 * to be zoomed to arbitrary levels with vector graphics.
 * (Note that you have a choice between a vectorized font,
 * and using the display system's font. The vectorized font
 * isn't editable; the display font may not match your
 * Java view. To choose, set the public  field textVector.)
 *
 * This thin wrapper around org.jibble.epsgraphics (included,
 * for your happy dumping) calls component.print() in order to
 * dump. (It needs to do this as a workaround around the Java
 * double-buffering. As far as I can tell, there's no easy way
 * to call .paint(g) with double buffering disabled. If the
 * double-buffering is enabled, then the system attempts to 
 * copy the image memory over to the dump--which loses all the
 * nice advantages of EPS).
 * 
 * @author danyelf
 */
public class EPSDump extends Dump {
	
	public EPSDump() {		
	}
	
	/**
	 * Creates a new EPSDumper with the textVector flag set to
	 * the input value.  If TRUE, the EPSDumper will produce 
	 * text in the form of shapes (which will be precisely as
	 * seen on screen); if FALSE, the EPSDumper will produce 
	 * text as letters (which may look different on screen
	 * but will be editable within tools like Illustrator.)
	 * @param textVector
	 */
	public EPSDump( boolean textVector ) {
		this.textVector = textVector;
	}
	
	/**
	 * By default, this flag is TRUE (use vectorized text).
	 * Set it to FALSE in order to use native text.
	 */
	public boolean textVector = true;
		
	public void dumpComponent(File filename, Component component)
			throws IOException {
		FileOutputStream outputStream = new FileOutputStream(filename);
		EpsGraphics2D g = new EpsGraphics2D(filename.getName(), outputStream,
				0, 0, component.getWidth(), component.getHeight());
		g.setAccurateTextMode(textVector);
		component.print(g);
		g.flush();
		g.close();
	}
}
