package ca.ubc.cs.ferret.history;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.SimpleSolution;

public class TransitionsToObjects extends AbstractHistoryQuery {

	public TransitionsToObjects() {
		super();
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
        monitor.beginTask(getDescription(), 20);
//        IRelation<Object> impls = getSphere().perform(monitor,
//        		HistorySphereHelper.TRANSITIONS_TO, elements);  
        for(Object from : HistorySphereHelper.getDefault().getHistoryMonitor().transitionsTo(parameter, 3)) {
        	SimpleSolution s = new SimpleSolution(this, parameter);
//        	s.add("from", from);
        	if(from instanceof Object[]) {
        		for(int i = 0; i < ((Object[])from).length; i++) {
        			s.add("to." + i, ((Object[])from)[i]);
        		}
        	} else {
        		s.add("to", from);
        	}
        	addSolution(s);
        }
        monitor.done();
	}

	public String getDescription() {
		return "navigated to";
	}

}
