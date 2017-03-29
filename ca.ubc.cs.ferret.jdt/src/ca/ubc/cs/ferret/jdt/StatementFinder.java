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

import ca.ubc.cs.ferret.model.ExtendibleSourceRange;
import ca.ubc.cs.ferret.model.IExtendibleSourceRange;
import java.util.Set;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public abstract class StatementFinder extends ASTVisitor {
    protected Set<IType> types;
    protected Set<String> typeNames;
    protected IExtendibleSourceRange sourceRange;
    protected boolean found = false;
    
    public void configure(Set<IType> _types, Set<String> _typeNames) {
        types = _types;
        typeNames = _typeNames;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        // Ensure the method declaration fits within the method's range
        return sourceRange == null
        	|| sourceRange.contains(node.getStartPosition(), node.getLength()); 
    }

	public abstract boolean preliminaryMatch(String source);

	public boolean check(ASTNode node, IMember method) {
	    found = false;
		try {
		    ISourceRange sr = method.getSourceRange();
		    sourceRange = new ExtendibleSourceRange(sr.getOffset(), sr.getLength());
		} catch (JavaModelException e) {
			return false;
		}
		node.accept(this);
		return found;
	}
}
