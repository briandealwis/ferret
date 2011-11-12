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
