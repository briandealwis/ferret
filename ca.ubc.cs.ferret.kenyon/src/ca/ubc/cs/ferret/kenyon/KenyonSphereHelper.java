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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.clustering.utils.TimedCachePolicy;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.kenyon.preferences.FerretKenyonPreferencePage;
import ca.ubc.cs.ferret.kenyon.preferences.IFKPreferenceConstants;
import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.model.SphereHelper;
import edu.se.evolution.kenyon.KenyonActivator;
import edu.se.evolution.kenyon.graph.Node;
import edu.se.evolution.kenyon.scm.Revision;
import edu.se.evolution.kenyon.scm.SCMReposConfigSpec;
import edu.se.evolution.kenyon.scm.SCMTransaction;
import edu.se.evolution.kenyon.util.HibernateUtil;

public class KenyonSphereHelper extends SphereHelper {	
	public static final String OP_MODIFICATIONS = "EvoModifications";
	public static final String OP_HANDLE_MODIFICATIONS = "EvoHandleModifications";
	public static final String OP_FILE_MODIFICATIONS = "EvoFileModifications";
	public static final String KEY_PROJECTS_MAPPINGS = "EvoProjectMappings";

	protected static KenyonSphereHelper singleton;
	
	protected TimedCachePolicy<Object, List<KTransaction>> cachedTransactions;

	public static KenyonSphereHelper getDefault() {
		if(singleton == null) { 
			singleton = new KenyonSphereHelper();
			singleton.start();
		}
		return singleton;
	}

	protected KenyonSphereHelper() {}

	@Override
	public ISphereFactory[] getSphereFactories() {
		return new ISphereFactory[] { new KenyonSphereFactory() };
	}

	@Override
	public void reset() {
		try {
			HibernateUtil.closeSession();
		} catch (HibernateException e) {
			FerretPlugin.log(e);
		}
		HibernateUtil.closeSessionFactory();
		cachedTransactions =
			new TimedCachePolicy<Object, List<KTransaction>>(FerretPlugin.getCacheTimeouts(),
					true, Math.min(5, Math.max(FerretPlugin.getCacheTimeouts() / 10, 30)));
		cachedTransactions.create();
		cachedTransactions.start();
	}

	@Override
	public void start() {
		reset();
	}

	@Override
	public void stop() {
		if(singleton == this) { singleton = null; }
		HibernateUtil.closeSessionFactory();
		cachedTransactions.stop();
		cachedTransactions.destroy();
		cachedTransactions = null;
	}

	public Session getSession() throws HibernateException {
		Session s = HibernateUtil.currentSession();
		if(s != null) {
			if(s.isOpen()) { return s; }
			HibernateUtil.closeSession();
		}
		Status status = testJDBCConnection(getHibernateDialectProperty(), getHibernateConnectionDriverClassProperty(),
				getHibernateConnectionUrlProperty(), getHibernateConnectionUsernameProperty(), getHibernateConnectionPasswordProperty());
		if(!status.isOK()) {
			throw new FerretFatalError("Could not initiate configured JDBC connection: " + status.getMessage(), status);
		}
		HibernateUtil.initSessionFactory(KenyonActivator.findHBMs(),
				getHibernateConfigurationProperties());
		return HibernateUtil.currentSession();
	}
	
	public void closeSession() {
		try {
			HibernateUtil.closeSession();
		} catch(HibernateException e) {
			/* ignore */
		}
	}

	private Properties getHibernateConfigurationProperties() {
		URL url = Activator.getDefault().getBundle().getEntry("/hibernate.properties");
		Properties props = new Properties();
		try {
			if(url != null) { props.load(url.openStream()); }
		} catch(IOException e) {
			Activator.log(e);
		}
		addPropertyIfNotEmpty(props, "hibernate.dialect", getHibernateDialectProperty()); 
		addPropertyIfNotEmpty(props, "hibernate.connection.url", getHibernateConnectionUrlProperty()); 
		addPropertyIfNotEmpty(props, "hibernate.connection.driver_class", getHibernateConnectionDriverClassProperty()); 
		addPropertyIfNotEmpty(props, "hibernate.connection.username", getHibernateConnectionUsernameProperty()); 
		addPropertyIfNotEmpty(props, "hibernate.connection.password", getHibernateConnectionPasswordProperty()); 
		String showSQL = Platform.getDebugOption(Activator.PLUGIN_ID + "/" + "showSQL");
		if(showSQL == null) { showSQL = "false"; }
		props.put("hibernate.show_sql", showSQL);
		return props;
	}

	public static void addPropertyIfNotEmpty(Properties props, String name, String value) {
		if(value != null && value.trim().length() > 0) {
			props.put(name, value.trim());
		}
	}

	private static String getHibernateConnectionPasswordProperty() {
		return Activator.getDefault().getPluginPreferences().getString(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_PASSWORD);
	}

	private static String getHibernateConnectionUsernameProperty() {
		return Activator.getDefault().getPluginPreferences().getString(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_USERNAME);
	}

	protected static String getHibernateConnectionDriverClassProperty() {
		return Activator.getDefault().getPluginPreferences().getString(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_DRIVER_CLASS);
	}

	protected static String getHibernateConnectionUrlProperty() {
		return Activator.getDefault().getPluginPreferences().getString(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_URL);
	}

	protected static String getHibernateDialectProperty() {
		return Activator.getDefault().getPluginPreferences().getString(IFKPreferenceConstants.PREF_HIBERNATE_DIALECT);
	}

	public MultiStatus testJDBCConnection(String dialectClassName, String driverClassName, 
			String connectionUrl, String connectionUsername, String connectionPassword) {
		MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 1, "Unable to connect using JDBC", null);
		// The Class.forName() should maybe be moved into Kenyon's HibernateUtil
		// as it'll be the resolving from there that matters.
		try {
			Class.forName(dialectClassName);
		} catch(ClassNotFoundException cnfe) {
			status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					FerretErrorConstants.CONFIGURATION_ERROR,
					"Unable to load Hibernate dialect class " + dialectClassName, cnfe));
		}
		try {
			Class.forName(driverClassName);
		} catch(ClassNotFoundException cnfe) {
			status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					FerretErrorConstants.CONFIGURATION_ERROR,
					"Unable to load JDBC driver class " + driverClassName, cnfe));
		}
		if((connectionUsername.length() == 0)
				!= (connectionPassword.length() == 0)) {
			status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					FerretErrorConstants.CONFIGURATION_ERROR,
					"Either both username and password must be specified, or neither specified", null));
		}
		if(status.isOK()) {
			try {
				Connection con;
				if(connectionUsername.length() == 0) {
					con = DriverManager.getConnection(connectionUrl);
				} else {
					con = DriverManager.getConnection(connectionUrl,
							connectionUsername, connectionPassword);
				}
				con.close();
			} catch(SQLException sqle) {
				status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						FerretErrorConstants.CONFIGURATION_ERROR,
						"Unable to connect to database", sqle));
			}
		}
		return status;
	}

	public TimedCachePolicy<Object, List<KTransaction>> getCachedTransactions() {
		return cachedTransactions;
	}

	@Override
	public String getLabel(Object object) {
		if(object instanceof SCMReposConfigSpec) {
			SCMReposConfigSpec spec = (SCMReposConfigSpec)object; 
			StringBuffer buffer = new StringBuffer();
			buffer.append("branch ");
			buffer.append(spec.getBranchIdentifier());
			buffer.append(" ");
			buffer.append(spec.getDateString());
			return buffer.toString();
		} else if(object instanceof SCMTransaction) {
			StringBuffer buffer = new StringBuffer();
			SCMTransaction tx = (SCMTransaction)object;
			buffer.append("transaction by '");
			buffer.append(tx.getAuthor());
			buffer.append("' at ");
			buffer.append(tx.getStartDate().toGMTString());
			return buffer.toString();
		} else if(object instanceof Revision) {
			Revision revision = (Revision)object;
			return revision.getFilename() + ": " + revision.getRevnum();
		} else if(object instanceof Node) {
			// return FerretPlugin.prettyPrint(ObjectMapping.resolve(((Node)object).getName()));
		}
		return null;
	}


	@Override
	public ImageDescriptor getImage(Object object) {
		if(object instanceof Node) {
			// return FerretPlugin.getImage(ObjectMapping.resolve(((Node)object).getName()));
		}
		return null;
	}

}
