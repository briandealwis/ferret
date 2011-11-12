package ca.ubc.cs.ferret.ui;

public interface IModelListener<T> {
	public void modelCleared(ListModel<T> model);
	public void modelElementsChanged(ListModel<T> model);
	public void modelElementAdded(ListModel<T> model, T element);
	public void modelElementRemoved(ListModel<T> model, T element);
}
