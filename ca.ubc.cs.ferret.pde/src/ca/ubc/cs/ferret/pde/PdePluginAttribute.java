package ca.ubc.cs.ferret.pde;

import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;

public class PdePluginAttribute extends PdeObject implements IPluginAttribute {
	protected IPluginAttribute attr;
	
	public PdePluginAttribute(IPluginModelBase plugin, IPluginAttribute attr) {
		super(plugin);
		this.attr = attr;
	}

	public String getValue() {
		return attr.getValue();
	}

	public void setValue(String value) throws CoreException {
		attr.setValue(value);
	}

	public String getName() {
		return attr.getName();
	}

	public IPluginObject getParent() {
		return attr.getParent();
	}

	public void setName(String name) throws CoreException {
		attr.setName(name);
	}

	public void write(String indent, PrintWriter writer) {
		attr.write(indent, writer);
	}

	@Override
	protected IPluginObject getWrappedObject() {
		return attr;
	}
}
