package ca.ubc.cs.ferret.model;

import java.util.List;

public interface ISphereCompositorFactory extends ISphereFactory {

	public void add(ISphereFactory factory);
	
	public boolean remove(ISphereFactory factory);
	
	public void moveSphereUp(ISphereFactory t);

	public void moveSphereDown(ISphereFactory t);

	public List<ISphereFactory> getComposedSphereFactories();
}
