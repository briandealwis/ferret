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
package ca.ubc.cs.ferret.history;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.views.QueriesDossierView;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class HistoryMonitor implements IWindowListener, ISelectionListener {

	protected Multiset<Object> counts;
	
	/**
	 * Records the <value> that transitions to <key>
	 */
	protected Map<Object,Multiset<Object>> transitionsTo;
	
	/**
	 * Records the <key> that transitions to <value>
	 */
	protected Map<Object,Multiset<Object>> transitionsFrom;
	protected LinkedList<Object> history;
	protected int selectionCounter = 0;
	
	public HistoryMonitor() {}
	
	public void start() {
		reset();
		getWorkbench().addWindowListener(this);
		for(IWorkbenchWindow w : getWorkbench().getWorkbenchWindows()) {
			windowOpened(w);
		}
	}

	public void reset() {
		counts = HashMultiset.create();
		transitionsTo = new MapMaker().weakKeys().makeMap();
		transitionsFrom = new MapMaker().weakKeys().makeMap();
		history = new LinkedList<Object>();
	}

	public void stop() {
		getWorkbench().removeWindowListener(this);
		for(IWorkbenchWindow w : getWorkbench().getWorkbenchWindows()) {
			windowClosed(w);
		}
	}

	protected int getMaximumHistorySize() {
		// TODO: This should be a configuration preference.
		return 200;
	}
	
	// IWindowListener requirements
	public void windowClosed(IWorkbenchWindow window) {
		window.getSelectionService().removePostSelectionListener(this);
	}

	public void windowOpened(IWorkbenchWindow window) {
		window.getSelectionService().addPostSelectionListener(this);
	}

	public void windowActivated(IWorkbenchWindow window) {}
	public void windowDeactivated(IWorkbenchWindow window) {}

	// Helper methods
	protected IWorkbench getWorkbench() {
		return FerretHistoryPlugin.getDefault().getWorkbench();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if(part.getSite().getId().equals(QueriesDossierView.viewID)) { return; }
        if(FerretPlugin.hasDebugOption("debug/selectionChanged")) {
            System.out.println("SelectionChanged: selection={" + FerretPlugin.compactPrettyPrint(selection) + "} ["
                    + selection.getClass().getName() + "], part=" + part);
        }
		if (selection.isEmpty()) {
			return;
		}
		if (selection instanceof IStructuredSelection) {
			selectionCounter++;
			Object[] sel = ((IStructuredSelection)selection).toArray();
			if(sel.length == 1) {
				recordSelection(sel[0]);
			} else {
				recordSelection(sel);
			}
		}
	}

	protected synchronized void recordSelection(Object object) {
		if(history.isEmpty()) {
			history.addLast(object);
			return;
		}
		Object previous = history.getLast();
		history.addLast(object);
		
		// Do bookkeeping for objects to be tossed
		if(history.size() > getMaximumHistorySize()) {
			Object last = history.removeFirst();
			counts.remove(last);
			int n = counts.count(last);
			if(n == 0) {
				for(Object to : transitionsTo.remove(last)) {
					transitionsFrom.get(to).remove(last, Integer.MAX_VALUE);
				}
				for(Object from : transitionsFrom.remove(last)) {
					transitionsTo.get(from).remove(last, Integer.MAX_VALUE);
				}
			}
		}

		Multiset<Object> from = transitionsFrom.get(previous);
		if(from == null) { transitionsFrom.put(previous, from = HashMultiset.create()); }
		from.add(object);
		
		Multiset<Object> to = transitionsTo.get(object);
		if(to == null) { transitionsTo.put(object, to = HashMultiset.create()); }
		to.add(previous);
	}

	public Collection<Object> transitionsTo(Object item, int maximum) {
		return determineTransitions(transitionsTo.get(item), item, maximum);
	}

	public Collection<Object> transitionsFrom(Object item, int maximum) {
		return determineTransitions(transitionsFrom.get(item), item, maximum);
	}

	protected synchronized Collection<Object> determineTransitions(Multiset<Object> transitions,
			Object item, int maximum) {
		if(transitions == null) { return Collections.emptyList(); }
		// Process the transitions list, choosing the top <maximum> items.
		List<Object[]> assessment = new ArrayList<Object[]>(transitions.elementSet().size());
		for(Object o : transitions.elementSet()) {
			if(o.equals(item)) { continue; }	// no point adding ourselves
			double relevance = calculateRelevance(o, transitions.count(o));
			assessment.add(new Object[] { o, relevance });
		}
		Collections.sort(assessment, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return (int)((Double)o2[1] - (Double)o1[1]);  // note order
			}});
		Set<Object> results = new HashSet<Object>(maximum);
		for(int index = 0; index < Math.min(assessment.size(), maximum); index++) {
			results.add(assessment.get(index)[0]);
		}
		return results;
	}

	protected double calculateRelevance(Object o, int count) {
		// could do something fancier like decay the count based on 
		// the occurrences in the past
		return count;
	}


	public int getSelectionCounter() {
		return selectionCounter;
	}
}
