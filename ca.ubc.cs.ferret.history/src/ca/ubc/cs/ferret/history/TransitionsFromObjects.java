package ca.ubc.cs.ferret.history;

import org.eclipse.core.runtime.IProgressMonitor;

import ca.ubc.cs.ferret.model.SimpleSolution;

public class TransitionsFromObjects extends AbstractHistoryQuery {
	
	public TransitionsFromObjects() {
	}

	@Override
	protected void internalRun(IProgressMonitor monitor) {
        monitor.beginTask(getDescription(), 20);
//        IRelation<Object> impls = getSphere().perform(monitor,
//        		HistorySphereHelper.TRANSITIONS_TO, elements);  
        for(Object to : HistorySphereHelper.getDefault().getHistoryMonitor().transitionsTo(parameter, 3)) {
        	SimpleSolution s = new SimpleSolution(this, parameter);
//        	s.add("to", to);
        	if(to instanceof Object[]) {
        		for(int i = 0; i < ((Object[])to).length; i++) {
        			s.add("from." + i, ((Object[])to)[i]);
        		}
        	} else {
        		s.add("from", to);
        	}
        	addSolution(s);
        }
        monitor.done();
	}

	public String getDescription() {
		return "navigated from";
	}
}
