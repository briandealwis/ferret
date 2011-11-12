package ca.ubc.cs.ferret.jdt;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class CatchesStatementFinder extends StatementFinder {

	@Override
	public boolean preliminaryMatch(String source) {
		return source.contains("catch");
	}

	@Override
	public boolean visit(CatchClause node) {
    	if(sourceRange == null || 
    			sourceRange.contains(node.getStartPosition(), node.getLength())) {
    		ITypeBinding binding = node.getException().getType().resolveBinding();
    		found = binding != null && typeNames.contains(binding.getQualifiedName());
    	}
        return false;
	}

}
