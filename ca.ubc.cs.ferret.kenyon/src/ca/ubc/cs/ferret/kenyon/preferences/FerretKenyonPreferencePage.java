package ca.ubc.cs.ferret.kenyon.preferences;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ca.ubc.cs.ferret.kenyon.Activator;
import ca.ubc.cs.ferret.kenyon.KenyonSphereHelper;

public class FerretKenyonPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	protected StringFieldEditor hibernateDialectFE;
	protected StringFieldEditor hibernateDriverClassFE;
	protected StringFieldEditor hibernateConnectionUrlFE;
	protected StringFieldEditor hibernateConnectionUsernameFE;
	protected StringFieldEditor hibernateConnectionPasswordFE;
	protected Button testConnectionButton;
	
	public FerretKenyonPreferencePage() {
		super(GRID);	// or FLAT?
	}

	public void init(IWorkbench workbench) {}

	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	@Override
	protected void createFieldEditors() {
		addField(hibernateDialectFE = 
			new StringFieldEditor(IFKPreferenceConstants.PREF_HIBERNATE_DIALECT,
				"Hibernate dialect", getFieldEditorParent()));  
		addField(hibernateDriverClassFE =
			new StringFieldEditor(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_DRIVER_CLASS,
					"Hibernate connection driver class", getFieldEditorParent()));
		addField(hibernateConnectionUrlFE =
			new StringFieldEditor(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_URL,
					"Hibernate database URL", getFieldEditorParent()));
		addField(hibernateConnectionUsernameFE =
			new StringFieldEditor(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_USERNAME,
					"Database username", getFieldEditorParent()));
		addField(hibernateConnectionPasswordFE =
			new StringFieldEditor(IFKPreferenceConstants.PREF_HIBERNATE_CONNECTION_PASSWORD,
					"Database password", getFieldEditorParent()));
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				resetTestButtonText();
			}};
		hibernateDialectFE.getTextControl(getFieldEditorParent()).addModifyListener(modifyListener);
		hibernateDriverClassFE.getTextControl(getFieldEditorParent()).addModifyListener(modifyListener);
		hibernateConnectionUrlFE.getTextControl(getFieldEditorParent()).addModifyListener(modifyListener);
		hibernateConnectionUsernameFE.getTextControl(getFieldEditorParent()).addModifyListener(modifyListener);
		hibernateConnectionPasswordFE.getTextControl(getFieldEditorParent()).addModifyListener(modifyListener);
		
		testConnectionButton = new Button(getFieldEditorParent(), SWT.PUSH);
//		GridData gd = new GridData();
//		gd.horizontalSpan = span;
//		testConnectionButton.setLayoutData(gd);
		resetTestButtonText();
		testConnectionButton.setFont(getFieldEditorParent().getFont());
		testConnectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testJDBCConnection();
			}
		});

	}

	protected void resetTestButtonText() {
		testConnectionButton.setText("Test JDBC Connection");
	}
	
	protected void testJDBCConnection() {
		testConnectionButton.setEnabled(false);
		testConnectionButton.setText("Testing...");

		MultiStatus status = 
			KenyonSphereHelper.getDefault().testJDBCConnection(hibernateDialectFE.getStringValue(),
				hibernateDriverClassFE.getStringValue(),
				hibernateConnectionUrlFE.getStringValue(),
				hibernateConnectionUsernameFE.getStringValue().trim(),
				hibernateConnectionPasswordFE.getStringValue().trim());

		testConnectionButton.setEnabled(true);
		if(status.isOK()) {
			testConnectionButton.setText("Succeeded");
		} else {
			ErrorDialog.openError(getShell(), "Test connection parameters", 
					"Unable to connect to database", status);
			resetTestButtonText();
		}
	}

	@Override
	public boolean performOk() {
		if(!super.performOk()) { return false; }
		KenyonSphereHelper.getDefault().reset();
		return true;
	}

}
