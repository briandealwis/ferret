/*******************************************************************************
 * Copyright (c) 2004 Brian de Alwis, UBC, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis - initial API and implementation
 *******************************************************************************/
package ca.ubc.cs.ferret.jdt;

import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.SimpleSolution;
import ca.ubc.cs.ferret.types.FerretObject;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;

/**
 * @author Brian de Alwis
 */
public class ReferencesToMethod extends JavaIntersectionConceptualQuery<IMethod,FerretObject> {
	protected static final String REFERRER_TAG = "referrer";
	
    public ReferencesToMethod() {
    }
    
    protected String getReferencesOperation() {
		return ObjectOrientedRelations.OP_METHOD_REFERENCES;
	}

    public String getDescription() {
    	if(elements.length == 1) {
    		return "references to method";
    	} else {
    		return "references to methods";
    	}
        //+ FerretPlugin.prettyPrint(method) + " (incl through interfaces and subtypes)";
    }

    public String getSubDescription() {
    	throw new FerretFatalError("method should not be called");
    }

    @Override
	protected Collection<FerretObject> performQuery(IMethod method, IProgressMonitor monitor) {
        IRelation op = getSphere().resolve(monitor,
        		getReferencesOperation(), method);
		return op.asCollection();
	}

	@Override
	protected void processSolution(FerretObject o) {
        SimpleSolution s = new SimpleSolution(this, o);
        s.add(REFERRER_TAG, o);
        addSolution(s);
//        if(o instanceof IMethod) {
//            IMethod m = (IMethod) o;
//            s.add(REFERRER_TAG, m);
////            for(IMethod method : elements) {
////            	s.add(new StupidlySimpleRelation(m, "references", method));
////            }
//            addSolution(s);
//        } else if(o instanceof IField) {
//            IField f = (IField) o;
//            s.add(REFERRER_TAG, f);
////            for(IMethod method : elements) {
////            	s.add(new StupidlySimpleRelation(f, "references", method));
////            }
//            addSolution(s);
//        } else if(o instanceof IInitializer) {
//            IInitializer i = (IInitializer) o;
//            s.add(REFERRER_TAG, i);
////            for(IMethod method : elements) {
////            	s.add(new StupidlySimpleRelation(i, "references", method));
////            }
//            addSolution(s);
//        } else {
//            Class<?> ifs[] = o.getClass().getInterfaces();
//            System.out.println("Weird: ReferencesToMethod: references to " + FerretPlugin.compactPrettyPrint(elements)
//                    + " has reference [" + o.getClass() + "]" + ((IMember)o).getHandleIdentifier());
//            for(int i = 0; i < ifs.length; i++) {
//                System.out.println("  ref implements: " + ifs[i]);
//            }
//        }

		
	}

}
