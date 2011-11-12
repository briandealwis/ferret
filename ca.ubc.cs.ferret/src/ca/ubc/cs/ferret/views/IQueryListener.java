package ca.ubc.cs.ferret.views;

import org.eclipse.jface.viewers.ITreeViewerListener;

import ca.ubc.cs.ferret.model.Consultation;
import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.IClusteringsContainer;

public interface IQueryListener extends ITreeViewerListener {
	/**
	 * Notify that query has been registered.  If q.isDone(), then this consultation
	 * has been previously submitted and evaluated (e.g., cached)
	 * @param q
	 */
	public void queryInitiated(Consultation q);
	/**
	 * Not guaranteed to happen; extremely unlikely when consultation
	 * is already done when initiated.
	 * @param q
	 */
	public void queryCompleted(Consultation q);
	public <T> void clusteredBy(IClusteringsContainer<? extends T> container, Clustering<? extends T> cluster);
	public void reconfigured();
}
