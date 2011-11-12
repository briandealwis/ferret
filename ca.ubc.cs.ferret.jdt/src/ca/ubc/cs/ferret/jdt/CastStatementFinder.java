package ca.ubc.cs.ferret.jdt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class CastStatementFinder extends StatementFinder {
	static Pattern castPattern = 
		Pattern.compile(".*\\(\\s*[0-9a-zA-Z_\\.$]+\\s*\\).*", Pattern.DOTALL);
	
	public CastStatementFinder() {
	}

	@Override
	public boolean preliminaryMatch(String source) {
        Matcher m = castPattern.matcher(source);
        return m.matches();
//		return source.contains("(");
	}

	@Override
	public boolean visit(CastExpression node) {
    	if(sourceRange == null || 
    			sourceRange.contains(node.getStartPosition(), node.getLength())) {
    		ITypeBinding binding = node.getType().resolveBinding();
    		found = found || 
    			(binding != null && typeNames.contains(binding.getQualifiedName()));
    	}
        return !found;	// if not found, keep going...
	}

}
