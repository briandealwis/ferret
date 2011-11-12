package ca.ubc.cs.ferret.pde;

import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;

public class PdePluginExtension extends PdeObject implements IPluginExtension {
	protected IPluginExtension ext;
	
	public PdePluginExtension(IPluginModelBase plugin, IPluginExtension ext) {
		super(plugin);
		this.ext = ext;
	}

	public String getPoint() {
		return ext.getPoint();
	}

	public String getId() {
		return ext.getId();
	}

	public String getName() {
		return ext.getName();
	}

	public Object getSchema() {
		return ext.getSchema();
	}

	public void setPoint(String point) throws CoreException {
		ext.setPoint(point);
	}

	// Ooo, these children methods are likely dangerous.  We should check and do appropriate
	// wrapping/unwrapping...
	
	public void add(IPluginObject child) throws CoreException {
		ext.add(unwrap(child));
	}

	public void add(int index, IPluginObject child) throws CoreException {
		ext.add(index, unwrap(child));
	}

	public int getChildCount() {
		return ext.getChildCount();
	}

	public IPluginObject[] getChildren() {
		return wrap(plugin, ext.getChildren());
	}

	public int getIndexOf(IPluginObject child) {
		return ext.getIndexOf(unwrap(child));
	}

	public void remove(IPluginObject child) throws CoreException {
		ext.remove(unwrap(child));
	}

	public void swap(IPluginObject child1, IPluginObject child2)
			throws CoreException {
		ext.swap(unwrap(child1), unwrap(child2));
	}

	public IPluginObject getParent() {
		return ext.getParent();		// FIXME should be wrapped?
	}

	public void setName(String name) throws CoreException {
		ext.setName(name);
	}

	public void write(String indent, PrintWriter writer) {
		ext.write(indent, writer);
	}

	public void setId(String id) throws CoreException {
		ext.setId(id);
	}

	@Override
	protected IPluginObject getWrappedObject() {
		return ext;
	}
}
