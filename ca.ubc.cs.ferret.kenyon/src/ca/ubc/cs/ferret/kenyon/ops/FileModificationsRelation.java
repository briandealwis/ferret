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
package ca.ubc.cs.ferret.kenyon.ops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.eclipse.core.resources.IFile;

import ca.ubc.cs.clustering.utils.TimedCachePolicy;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.kenyon.KTransaction;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import edu.se.evolution.kenyon.KenyonException;
import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;
import edu.se.evolution.kenyon.scm.SCMTransaction;

public class FileModificationsRelation extends
		AbstractCollectionBasedRelation<IFile> {

	public FileModificationsRelation() {}

	@Override
	protected Class<IFile> getInputType() {
		return IFile.class;
	}

	@Override
	protected Collection<?> realizeCollection(IFile input) {
		List<KTransaction> transactions = getCachedTransactions().get(input);
		if(transactions != null) { return transactions; }
		try {
			Session s = KenyonSphereHelper.getDefault().getSession();
			transactions = findModifyingTransactions(s, transformFilename(input));
			getCachedTransactions().insert(input, transactions);
			return transactions;
		} catch (HibernateException e) {
			FerretPlugin.log(e);
			return Collections.EMPTY_SET;
		} catch (KenyonException e) {
			FerretPlugin.log(e);
			return Collections.EMPTY_SET;
		}
	}

	protected TimedCachePolicy<Object, List<KTransaction>> getCachedTransactions() {
		return KenyonSphereHelper.getDefault().getCachedTransactions();
	}

	protected List<KTransaction> findModifyingTransactions(Session s,
			String transformedFilename) throws KenyonException, HibernateException {
		Query q = s.createQuery(
				"select spec "
				+ "from SCMReposConfigSpec spec "
				+ "    inner join spec.completingTransactions as transaction "
				+ "    inner join transaction.revisions as revision "
				+ "order by spec.time "
				+ "where "
				+ "  revision.filename = '" + transformedFilename +"' ");
		q.setCacheable(true);
		List<Object[]> changes = q.list();
		List<KTransaction> solutions = new ArrayList<KTransaction>();
		if(changes.isEmpty()) { return null; }
		for(Object solution : changes) {
			SCMReposConfigSpec spec = (SCMReposConfigSpec)solution;
			for(SCMTransaction tx : (Collection<SCMTransaction>)spec.getCompletingTransactions()) {
				solutions.add(new KTransaction(spec, tx));
			}
		}
		return solutions;
	}

	protected String transformFilename(IFile file) {
		// IPath.toPortableString is something like "/Project/path/path/file"
		// Kenyon will record it as "CVSModule/path/path/file".
		// FIXME: Assumes the Project name and CVSModule coincide.
		String relativeFilename = file.getFullPath().toPortableString();
		if(relativeFilename.length() > 0 && relativeFilename.charAt(0) == '/') {
			relativeFilename = relativeFilename.substring(1);
		}
		return relativeFilename;
	}

	@Override
	protected Fidelity getResultsFidelity() {
		return Fidelity.Approximate;
	}

}
