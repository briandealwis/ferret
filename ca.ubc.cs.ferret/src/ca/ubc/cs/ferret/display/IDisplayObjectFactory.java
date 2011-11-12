package ca.ubc.cs.ferret.display;

public interface IDisplayObjectFactory {
	public IDisplayObject createDisplayObject(Object object, IDisplayObject parent);
}
