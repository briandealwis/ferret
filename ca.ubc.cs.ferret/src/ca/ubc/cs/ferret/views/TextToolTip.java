/**
 * This class was shamlessly hacked up from the class
 * org.eclipse.hyades.uml2sd.ui.view.DiagramToolTip
 * from the Eclipse TPTP project
 */
package ca.ubc.cs.ferret.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is used to reproduce the same tooltip behavior on Windows and Linux
 * when the mouse move hover the sequence diagram
 * @author sveyrier
 */
public class TextToolTip implements PaintListener  {

	
	/**
	 * The parent control where the tooltip must be drawn
	 */
	protected Control parent = null;
	/**
	 * The tooltip shell
	 */
	protected Shell toolTipShell = null;
	/**
	 * The tooltip text
	 */
	protected String text = null;
	
	/**
	 * Create a new tooltip for the given parent control
	 * @param _parent the parent control
	 */
	public TextToolTip(Control _parent)
	{
		parent = _parent;
		toolTipShell = new Shell(parent.getShell(),SWT.ON_TOP|SWT.NO_FOCUS);
		toolTipShell.setLayout(new RowLayout());
		toolTipShell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		toolTipShell.addPaintListener(this);
		toolTipShell.setSize(10,10);
	}
	
	/**
	 * Display the tooltip using the given text
	 * The tooltip will stay on screen until it is told otherwise
	 * @param value the text to display
	 */
	public void showToolTip(String value)
	{
		if(value == null) {
			toolTipShell.setVisible(false);
			return;
		}
		value = value.trim();
		if (value.length() == 0) {
			toolTipShell.setVisible(false);
			return;
		}
		boolean refreshRequired = text == null || !value.equals(text);
		text=value;
		int w = toolTipShell.getBounds().width;
		Point hr = Display.getDefault().getCursorLocation();
		int cursorH=32;
		for (int i=0;i<Display.getDefault().getCursorSizes().length;i++)
		{
			if (Display.getDefault().getCursorSizes()[i].y<cursorH)
				cursorH=Display.getDefault().getCursorSizes()[i].y;
		}	
		if (hr.x+w>Display.getDefault().getBounds().width)
		{
			int tempX=(hr.x+w)-Display.getDefault().getBounds().width;
			if (tempX>Display.getDefault().getBounds().width)
				hr.x = 0;
			hr.x=hr.x-tempX;
		}
		toolTipShell.setLocation(hr.x,hr.y+cursorH);
		toolTipShell.setVisible(true);
		if(refreshRequired) { toolTipShell.redraw(); }
	}
	
	
	/**
	 * Hide the tooltip
	 */
	public void hideToolTip()
	{
		toolTipShell.setVisible(false);
	}
	
	/**
	 * Draw the tooltip text on the control widget when a paint event is received
	 */
	public void paintControl(PaintEvent event)
	{
		Point size = event.gc.textExtent(text);		
		event.gc.drawText(text,2,0,true);	
		toolTipShell.setSize(size.x+6,size.y+2);
	}
}
