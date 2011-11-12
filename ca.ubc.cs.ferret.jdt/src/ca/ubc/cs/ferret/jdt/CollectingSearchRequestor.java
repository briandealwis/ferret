/*
 * Copyright 2004  University of British Columbia
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

/**
 * @author bsd
 */
public class CollectingSearchRequestor extends SearchRequestor {
	protected Map<Object,Set<SearchMatch>> results;
	
	public CollectingSearchRequestor() {
		resetResults();
	}
	
	public void resetResults() {
		 results = new HashMap<Object,Set<SearchMatch>>();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 * @author bsd
	 * @since X
	 */
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		Object category;
		Set<SearchMatch> set;
		if((category = filterSearchMatch(match)) == null) { return; }
		if((set = results.get(category)) == null) {
			results.put(category, set = new HashSet<SearchMatch>());
		}
		set.add(match);
		Thread.yield();	// try to make UI more responsive
	}

	/**
	 * Provide definable filter for categorizing search matches.  The filter should
	 * answer null if the match is not to be kept, or some category object.
	 * @param match
	 * @return the category object
	 * @author bsd
	 */
	protected Object filterSearchMatch(SearchMatch match) throws CoreException {
        if(match.getAccuracy() != SearchMatch.A_ACCURATE) { return null; }
        if(match.getElement() instanceof IType) { return IType.class; }
        if(match.getElement() instanceof IMethod) { return IMethod.class; }
        if(match.getElement() instanceof IField) { return IField.class; }
        if(match.getElement() instanceof IInitializer) { return IInitializer.class; }
		return match.getElement().getClass();
	}

	public <E> Map<Object,Set<E>> getResults() {
        Map<Object,Set<E>> fakeResults = new HashMap<Object, Set<E>>(results.size());
        for(Object key : results.keySet()) {
            Set<SearchMatch> matches = results.get(key);
            Set<E> fakeMatches = new HashSet<E>(matches.size());
            for(SearchMatch match : matches) { fakeMatches.add((E)match.getElement()); }
            fakeResults.put(key, fakeMatches);
        }
		return fakeResults;
	}
	
	public <E> Set<E> getValues() {
	    Set<E> set = new HashSet<E>();
	    for(Collection<Object> r : getResults().values()) {
	        set.addAll((Collection<E>)r);
	    }
	    return set;
    }

    public Set<SearchMatch> getMatches() {
        Set<SearchMatch> set = new HashSet<SearchMatch>();
        for(Collection<SearchMatch> r : results.values()) {
            set.addAll(r);
        }
        return set;
    }
}
