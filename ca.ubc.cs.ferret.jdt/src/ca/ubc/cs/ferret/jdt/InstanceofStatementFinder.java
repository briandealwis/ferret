package ca.ubc.cs.ferret.jdt;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InstanceofExpression;

public class InstanceofStatementFinder extends StatementFinder {

	@Override
	public boolean visit(InstanceofExpression node) {
    	if(sourceRange == null || 
    			sourceRange.contains(node.getStartPosition(), node.getLength())) {
    		ITypeBinding binding = node.getRightOperand().resolveBinding();
    		found = binding != null && typeNames.contains(binding.getQualifiedName());
    	}
        return false;
	}

	@Override
	public boolean preliminaryMatch(String source) {
		return source.contains("instanceof");
	}

}
