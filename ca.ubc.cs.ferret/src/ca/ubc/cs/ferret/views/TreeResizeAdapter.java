package ca.ubc.cs.ferret.views;

import java.util.ArrayList;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * The TableResizeAdapter is a ControlAdapter used to set the size of a table.
 * It is defined on a Composite which is the parent of a table. The Composites
 * only child is the table.
 * Taken from code in Bug 13467.
 */
public class TreeResizeAdapter extends ControlAdapter {

	private Tree tree;

	/**
	 * Create a new instance of the receiver with the table to create specified
	 */
	public TreeResizeAdapter(Tree treeChild) {
		tree = treeChild;
	}

	/**
	 * The list of column layout data (element type:
	 * <code>ColumnLayoutData</code>).
	 */
	private ArrayList<ColumnLayoutData> columns = new ArrayList<ColumnLayoutData>();

	/**
	 * Adds a new column of data.
	 * 
	 * @param data
	 *            the column layout data
	 */
	public void addColumnData(ColumnLayoutData data) {
		columns.add(data);
	}

	/**
	 * Sent when the size (width, height) of a control changes. The default
	 * behavior is to do nothing.
	 * 
	 * @param e
	 *            an event containing information about the resize
	 */
	public void controlResized(ControlEvent e) {

		Rectangle area = tree.getParent().getClientArea();
		int width = area.width - 2 * tree.getBorderWidth();

		// XXX: Layout is being called with an
		// invalid value the first time
		// it is being called on Linux. This
		// method resets the
		// Layout to null so we make sure we
		// run it only when
		// the value is OK.
		if (width <= 1)
			return;

		TreeColumn[] treeColumns = tree.getColumns();
		int size = Math.min(columns.size(), treeColumns.length);
		int[] widths = new int[size];
		int fixedWidth = 0;
		int numberOfWeightColumns = 0;
		int totalWeight = 0;

		// First calc space occupied by fixed
		// columns
		for (int i = 0; i < size; i++) {
			ColumnLayoutData col = (ColumnLayoutData) columns.get(i);
			if (col instanceof ColumnPixelData) {
				int pixels = ((ColumnPixelData) col).width;
				widths[i] = pixels;
				fixedWidth += pixels;
			} else if (col instanceof ColumnWeightData) {
				ColumnWeightData cw = (ColumnWeightData) col;
				numberOfWeightColumns++;
				// first time, use the
				// weight specified by
				// the column data, otherwise use the actual width as the weight
				// int weight =
				// firstTime ?
				// cw.weight :
				treeColumns[i].getWidth();
				int weight = cw.weight;
				totalWeight += weight;
			} else {
				Assert.isTrue(false, "Unknown column layout data"); //$NON-NLS-1$
			}
		}

		// Do we have columns that have a
		// weight
		if (numberOfWeightColumns > 0) {
			// Now distribute the rest to
			// the columns with weight.
			int rest = width - fixedWidth;
			int totalDistributed = 0;
			for (int i = 0; i < size; ++i) {
				ColumnLayoutData col = (ColumnLayoutData) columns.get(i);
				if (col instanceof ColumnWeightData) {
					ColumnWeightData cw = (ColumnWeightData) col;
					// calculate
					// weight as
					// above
					// int weight =
					// firstTime ?
					// cw.weight : tableColumns[i].getWidth();
					int weight = cw.weight;
					int pixels = totalWeight == 0 ? 0 : weight * rest
							/ totalWeight;
					if (pixels < cw.minimumWidth)
						pixels = cw.minimumWidth;
					totalDistributed += pixels;
					widths[i] = pixels;
				}
			}

			// Distribute any remaining
			// pixels to columns with weight.
			int diff = rest - totalDistributed;
			for (int i = 0; diff > 0; ++i) {
				if (i == size)
					i = 0;
				ColumnLayoutData col = (ColumnLayoutData) columns.get(i);
				if (col instanceof ColumnWeightData) {
					++widths[i];
					--diff;
				}
			}
		}

		for (int i = 0; i < size; i++) {
			treeColumns[i].setWidth(widths[i]);
		}
	}
}
