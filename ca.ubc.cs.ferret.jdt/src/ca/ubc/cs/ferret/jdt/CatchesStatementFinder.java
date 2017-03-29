/*******************************************************************************
 * Copyright (c) 2005 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
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
