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
package ca.ubc.cs.ferret.kenyon;

import java.util.Collection;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

import ca.ubc.cs.ferret.FerretFatalError;

// I'm sure there's a better way to do this using Hibernate Criteria
public class KSQLHandlesSource implements IKHandlesSource {

	protected MultiMap<Integer,String> sqlIdentifiers = new MultiHashMap<Integer, String>();
	protected int numberStages = 0;
	
	public int getNumberStages() {
		return numberStages;
	}

	public void addHandle(int stage, String sqlString) {
		if(stage > numberStages) {
			throw new FerretFatalError("cannot skip stages");
		} else if(stage == numberStages) {
			numberStages++;
		}
		sqlIdentifiers.put(stage, sqlString);
	}
	
	public String asSQL(int stage, String elementHandle) {
		if(stage >= numberStages) { 
			throw new FerretFatalError("invalid stage");
		}
		boolean requiresLikeKeyword = false;
		Collection<String> identifiers = sqlIdentifiers.get(stage);
		for(String sqlString : identifiers) {
			if(sqlString.indexOf('%') >= 0) { requiresLikeKeyword = true; }
		}
		return requiresLikeKeyword ? asSQLLike(elementHandle, identifiers)
				: asSQLIn(elementHandle, identifiers);
	}

	private String asSQLIn(String elementHandle, Collection<String> identifiers) {
		StringBuffer result = new StringBuffer();
		result.append(elementHandle);
		result.append(identifiers.size() == 1 ? " = " : " in (");
		result.append('\'');
		int i = 0;
		for(String identifier : identifiers) {
			result.append(identifier);
			if(++i < identifiers.size()) {
				result.append("', '");
			} else {
				result.append('\'');
			}
		}
		if(identifiers.size() > 1) { result.append(')'); }
		return result.toString();
	}

	private String asSQLLike(String elementHandle, Collection<String> identifiers) {
		StringBuffer result = new StringBuffer();
		if(identifiers.size() > 1) { result.append('('); }
		int i = 0;
		for(String identifier : identifiers) {
			result.append(elementHandle);
			result.append(" like '");
			result.append(identifier);
			result.append('\'');
			if(++i < identifiers.size()) {
				result.append(" or ");
			}
		}
		if(identifiers.size() > 1) { result.append(')'); }
		return result.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(getClass() != obj.getClass()) { return false; }
		KSQLHandlesSource other = (KSQLHandlesSource)obj;
		return getNumberStages() == other.getNumberStages() && 
			asSQL(0, "foo").equals(other.asSQL(0, "foo"));
	}

	@Override
	public int hashCode() {
		return getNumberStages() * 31 + asSQL(0, "foo").hashCode();
	}

}
