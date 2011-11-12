package ca.ubc.cs.clustering.attrs;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.IConfigurationElement;

public class ClassifierDescription {
	protected IClassifier classifier;
	protected String id;
	protected String description;
	protected Expression guard;
	protected IConfigurationElement declaration;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Expression getGuard() {
		return guard;
	}
	public void setGuard(Expression guard) {
		this.guard = guard;
	}
	
	public IConfigurationElement getDeclaration() {
		return declaration;
	}
	public void setDeclaration(IConfigurationElement declaration) {
		this.declaration = declaration;
	}
	
}
