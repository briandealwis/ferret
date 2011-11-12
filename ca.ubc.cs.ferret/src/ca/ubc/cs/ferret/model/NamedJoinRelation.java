package ca.ubc.cs.ferret.model;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import ca.ubc.cs.ferret.types.FerretObject;

public class NamedJoinRelation extends AbstractToolRelation
		implements IRelationFactory {
	protected String operationNames[];	
	protected List<IRelation> stack;
	
	public NamedJoinRelation(String... strings) {
		operationNames = strings;
	}

	protected boolean configure(FerretObject... arguments) {
		IRelation top;
		try {
			top = resolver.topPerform(monitor, operationNames[0], arguments);
		} catch(UnsupportedOperationException e) { return false; }
		if(top == null) { return false; }
		stack = new LinkedList<IRelation>();
		stack.add(top);
		return true;
	}
	
	public boolean hasNext() {
		IRelation tail = getTail();
		if(tail == null) { return false; }
		// Note: tail.hasNext() never return false: getTail() should never return an
		// IRelation in such a state 
		return tail.hasNext();
	}

	public FerretObject next() {
		IRelation tail = getTail();
		if(tail == null) { throw new NoSuchElementException(); }
		return tail.next();
	}
	
	protected IRelation getTail() {
		if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		// We will either return an IRelation with hasNext() == true or null
		if(stack.isEmpty()) { return null; }	// stack initially has the top-most op 
		IRelation last = stack.get(stack.size() - 1);
		if(!last.hasNext()) {
			stack.remove(stack.size() - 1);
			return getTail();	// recurse + restart
		}
		if(stack.size() == operationNames.length) {
			// Then we have a full pipeline, and the last hasNext() -- so return it
			return last;
		}
		IRelation next;
		do {
			if(monitor.isCanceled()) { throw new OperationCanceledException(); }
			try {
				next = resolver.topPerform(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN),
						operationNames[stack.size()], last.next());
			} catch(UnsupportedOperationException e) {
				/* clearly the element didn't agree with the remainder of the
				 * chain, so cycle */
				next = null;		
			}
		} while(next == null && last.hasNext());
		if(next != null) { stack.add(next); }
		// and recurse: if !last.hasNext() then it will be dequeued in the recurse
		// and the next argument will be obtained from its predecessor and we'll
		// continue.  If next != null, and it's the end of the pipeline, then we return
		// it on the recurse; else the recurse will create its join in the pipeline
		return getTail();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("NamedJoinRelation(");
		for(int i = 0; i < operationNames.length; i++) {
			buf.append(operationNames[i]);
			if(i + 1 < operationNames.length) { buf.append(","); }
		}
		buf.append(")");
		return buf.toString();
	}

	public IRelation resolve(IProgressMonitor monitor, ISphere sphere,
			FerretObject... parameters) {
		return configure(monitor, sphere.createResolverState(null), parameters);
	}

	public IRelation resolve(IProgressMonitor monitor, ISphere sphere,
			Object... parameters) {
		return configure(monitor, sphere.createResolverState(null), parameters);
	}
}
