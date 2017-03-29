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
package ca.ubc.cs.ferret.kenyon.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;

import ca.ubc.cs.ferret.kenyon.Activator;

public class FKPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(IFKPreferenceConstants.PREF_HIBERNATE_DIALECT,
			"net.sf.hibernate.dialect.DerbyDialect");
		node.put(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_DRIVER_CLASS,
			"org.apache.derby.jdbc.ClientDriver");
		node.put(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_URL,
			"jdbc:derby://<server>[:<port>]/<databaseName>");
	}

}
