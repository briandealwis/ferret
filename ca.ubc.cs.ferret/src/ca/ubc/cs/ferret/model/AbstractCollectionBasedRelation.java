package ca.ubc.cs.ferret.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.OperationCanceledException;

import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.FerretObject;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

/**
 * Simple framework for a concrete tool query to handle the FerretObject conversion requirements.  
 * Assumes the query takes a single input.
 * @param <IT> the expected type of the inputs
 */
public abstract class AbstractCollectionBasedRelation<IT> extends AbstractToolRelation {
	protected Collection<FerretObject> results;
	protected Iterator<FerretObject> iterator;
	protected ConversionResult<IT> convertedInput;
	protected Collection<IT> inputValues;
	protected Fidelity resultingFidelity = Fidelity.Exact;
	
	public AbstractCollectionBasedRelation() {}

	/**
	 * Do any further checking of the converted results. 
	 * @param inputs the converted results of <code>input</code>
	 * @return true if the input is appropriate, false otherwise
	 */
	protected IT checkInput(IT input) {
		return input;
	}

	/**
	 * The actual class corresponding to the expected input types 
	 * @return the input type
	 */
	protected abstract Class<IT> getInputType();
	
	/**
	 * Realize the query for <TT>input</TT>.  This must <B>never</B> return null:
	 * log an error and return an empty collection (e.g., {@link Collections#EMPTY_LIST}).
	 */
	protected abstract Collection<?> realizeCollection(IT input);

	/**
	 * Populate the collection of query-results by issuing realizeCollection() for each
	 * of the acceptable converted inputs.
	 */
	protected void populate() {
		// This is the implementation of the union of multivalent conversions.
		// You might have expected it to be a bit grander or something.
		// Perhaps this should be pluggable?
		Collection<Object> outputs = new HashSet<Object>();
		for(IT value : inputValues) {
			outputs.addAll(realizeCollection(value));
		}
		results = FerretObject.wrap(outputs, getResultsFidelity(), resolver.getRootSphere());
		iterator = results.iterator();
	}

	/**
	 * Return the expected fidelity of the results of this relation.  This is composed with the
	 * fidelity of the inputs to the relation.
	 * @return the expected fidelity of the results of this relation
	 */
	protected Fidelity getResultsFidelity() {
		return resultingFidelity;
	}

	/**
	 * Return the minimum fidelity supported by this relation.
	 * @return the minimum fidelity supported
	 */
	protected Fidelity getRequiredFidelity() {
		return Fidelity.Approximate;
	}

	public boolean hasNext() {
		if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		if(iterator == null) { populate(); }
		return iterator.hasNext();
	}

	public FerretObject next() {
		if(iterator == null) { populate(); }
		return iterator.next();
	}

	@Override
	public int size() {
		if(iterator == null) { populate(); }
		return results.size();
	}

	@Override
	public Collection<FerretObject> asCollection() {
		if(iterator == null) { populate(); }
		return results;
	}

	@Override
	final protected boolean configure(FerretObject... arguments) {
		if(arguments.length != 1) { return false; }
		assert getInputType() != null;
		return convertInput(arguments[0]);
	}

	protected boolean convertInput(FerretObject arg) {
		convertedInput = arg.convert(getInputType(), 0, getRequiredFidelity());
		if(convertedInput == null) { return false; }
		inputValues = new ArrayList<IT>();
		resultingFidelity = resultingFidelity.least(convertedInput.getFidelity());
		for(IT inputValue : convertedInput.getResults()) {
			IT checked = checkInput(inputValue); 
			if(checked != null) { inputValues.add(checked); }
		}		
		return true;
	}

}
