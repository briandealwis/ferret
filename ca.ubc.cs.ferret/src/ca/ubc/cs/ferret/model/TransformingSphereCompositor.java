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
package ca.ubc.cs.ferret.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A restricting sphere wraps another sphere, subjecting any such wrapped relations to a
 * one of a set of transforms; relations may be registered on this instance, but are not
 * subject to these transforms.  These transforms are expressed as a set of relations
 * ({@link IRelationFactory}, actually) in the order added using {@link #addTransform(IRelationFactory)}.
 * The first relation that handles the results of the wrapped relation is used.  
 */
public class TransformingSphereCompositor extends AbstractSphere {
	protected ISphere source;
	protected List<IRelationFactory> transforms;
	protected String description;
	
	public TransformingSphereCompositor(String d) {
		description = d;
		transforms = new ArrayList<IRelationFactory>();
	}

	public TransformingSphereCompositor(String d, ISphere tb) {
		this(d);
		source = tb;
		source.setParent(this);
	}

	@Override
	public AbstractRelationResolvingState createResolverState(AbstractRelationResolvingState parent) {
		return new TransformingResolvingState(parent);
	}
	
	public <T> T get(String key, Class<T> clazz) {
		return source.get(key, clazz);
	}

	/**
	 * Add the provided transform.  Transforms are verified in order added, and the first
	 * found is the transform used.
	 * @param xform
	 */
	public void addTransform(IRelationFactory xform) {
		transforms.add(xform);
	}

	public class TransformingResolvingState extends AbstractRelationResolvingState {
		protected AbstractRelationResolvingState substate;
		
		public TransformingResolvingState(AbstractRelationResolvingState parent) {
			super(parent);
		}

		@Override
		public AbstractRelationResolvingState next(IProgressMonitor monitor) {
			if(finished) { return parent; }
			if(substate == null) {
				substate = source.createResolverState(null);
			}
			if(!substate.finished()) {
				substate = substate.next(monitor, relationName, arguments);
				return this;
			}
			finished = true;
			if((result = substate.getResult()) != null) {
				// recursive calls are possible using aliases: no point imposing the same transforms
				if(!(result instanceof TransformingOperation) || !((TransformingOperation)result).getTransformations().equals(transforms)) {
					// could support an option to configure the relation to be permissive
					result =  new TransformingOperation(monitor, this, result, transforms, true);
				}
			}
			return this;
		}

	}

	@Override
	public <T> void internalGetAll(String key, Class<T> cl,
			Map<ISphere, T> results) {
		((AbstractSphere)source).internalGetAll(key, cl, results);
	}
}
