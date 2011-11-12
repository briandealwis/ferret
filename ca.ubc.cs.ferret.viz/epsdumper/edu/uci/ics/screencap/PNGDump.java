/*
 * Created on Mar 3, 2004
 */
package edu.uci.ics.screencap;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This DUMP creates a PNG or JPG bitmap dump.
 * 
 * created Mar 3, 2004
 * 
 * @author danyelf
 */
public class PNGDump extends Dump {
	
	private String filetype;

	/**
	 * Creates a bitmap dumper that produces a PNG dump.
	 */
	public PNGDump() {
		this.filetype = "png";
	}
	
	/**
	 * Creates a bitmap dump of type FILETYPE. With input
	 * "JPG" or "jpg", creates a JPEG dump; with input
	 * "PNG" or "png", creates a PNG dump. Behavior for
	 * other types is not defined (but probably not what
	 * you want.)
	 * 
	 * @param filetype
	 */
	public PNGDump( String filetype ) {
		this.filetype = filetype;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.screencap.Dump#dumpComponent(java.io.File,
	 *      java.awt.Component)
	 */
	public void dumpComponent(File filename, Component component)
			throws IOException {

		BufferedImage bi = new BufferedImage(component.getWidth(), component
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		component.paint(g);
		g.dispose();
		ImageIO.write(bi, filetype, filename);
	}
}
