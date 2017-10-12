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
package ca.ubc.cs.ferret.model;

import ca.ubc.cs.clustering.IClusteringsProvider;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The base contract for a CQ.  A CQs may provide a custom clustering.
 */
public interface IConceptualQuery extends IClusteringsProvider<Object> {

    /**
     * Describe the results of the query.  This is expected to be a single sentence
     * summarizing the interesting result.  Answer null if there was no (useful) answer.
     * @return the summary.
     */
    public String getDescription();
    
    /**
     * Return this query's categorization.  This is taken from the ICQ's
     * declaration in the plugin.xml.  ICQs may change their category,
     * though it may not have immediate effect.
     * @return the categorization
     */
    public String getCategory();
    
    /**
     * Set the query's category.
     */
    public void setCategory(String newCategory);
    
    public Consultation getConsultation();
    public void setConsultation(Consultation consult);
    
    public void run(IProgressMonitor monitor);
    
    /**
     * Is the query represented by this instance complete? 
     * @return true if compete
     */
    public boolean isDone();
    
    /**
     * Are this instance's results still valid?  Assumes it is done. 
     * @return true if results still valid
     */
    public boolean isValid();
    
    /**
     * Reset the instance to appear to not have been issued.
     */
    public void reset();
    
    /**
     * Return true if all solutions are simple.
     * @return true if solutions are simple
     */
    public boolean isSimple();
    
    public Set<ISolution> getSolutions();
    public Set<Fact> getFacts();

    /**
     * Indicate that the CQ failed to run due to an error.
     */
	public void markErrorOccurred();

	/**
	 * Return true if an error occurred during the processing of this CQ.
	 * @return
	 */
	public boolean errorOccurred();

	public static final String DEFAULT_PARAMETER = "default";

	public boolean setParameters(Map<String, Object[]> singletonMap);
}
