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
package ca.ubc.cs.ferret.preferences;

import ca.ubc.cs.ferret.Consultancy;
import ca.ubc.cs.ferret.FerretPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FerretPreferencePage extends FieldEditorPreferencePage 
		implements IWorkbenchPreferencePage {
	public static final String pageId = "ca.ubc.cs.ferret.preferences.primary";

	public FerretPreferencePage() {
		super(GRID);	// or FLAT?
//		setPreferenceStore(FerretPlugin.getDefault().getPreferenceStore());
//		setDescription("Ferret Preferences FOO!");
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return FerretPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void createFieldEditors() {
		// Sadly the use of Groups is not possible: calcNumOfColumns()
		// assumes that getFieldEditorParent()'s children are all FieldEditors
		// and uses that to set the GridLayout's number of columns.
		addLabel("Ferret display options:", 2);
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_AUTO_EXPAND,
				"Automatically expand results", getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_SHOW_ONLY_COMPLETED_CQS,
				"Show only completed queries", getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_SHOW_EMPTY_CQS,
				"Show queries with no results", getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_SHOW_FACTS,
				"Show additional facts about found elements", getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_SHOW_HEADER,
				"Show header on Ferret view", getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_HISTORY_RESTORE_NAVIGATION_TO_QUERY_POINT,
				"Restore navigation history when selecting previously queried-for elements",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_SOLUTION_RESTORE_NAVIGATION_TO_QUERY_POINT,
				"Restore navigation history when selecting solutions from Ferret",
				getFieldEditorParent()));
		IntegerFieldEditor mneEditor = new IntegerFieldEditor(
				IFerretPreferenceConstants.PREF_MIN_ELEMENTS_FOR_CLUSTERING,
				"Minimum number of elements for clustering",
				getFieldEditorParent());
		mneEditor.setValidRange(0, 600);	// 600 is arbitrarily large
		addField(mneEditor);
		addField(new FontFieldEditor(
				IFerretPreferenceConstants.PREF_DOSSIER_FONT,
				"Font for Ferret view text: ", getFieldEditorParent()));

		addLabel("Querying options:", 2);
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_RESPOND_TO_SELECTIONS,
				"Automatically issue queries on user selections",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_REISSUE_CURRENT_QUERY_ON_CHANGE,
				"Reissue current query on workspace change",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_HONOUR_OPEN_PREFERENCE,
				"Honour Workbench's Open preference? (If disabled, then a double-click "
				+ "is required to open query results)",
				getFieldEditorParent()));
		IntegerFieldEditor bgCountEditor = new IntegerFieldEditor(
				IFerretPreferenceConstants.PREF_MAX_BACKGROUND_COUNT,
				"Maximum pending background queries allowed (0 to disable)",
				getFieldEditorParent());
		bgCountEditor.setValidRange(0, 600);	// 600 is arbitrary, true
		addField(bgCountEditor);		
		
		IntegerFieldEditor rcCountEditor = new IntegerFieldEditor(
				IFerretPreferenceConstants.PREF_RECENT_CONSULTATIONS_LIMIT,			
				"Recent consultations to cache",
				getFieldEditorParent());
		rcCountEditor.setValidRange(0, 600);	// 600 is arbitrary, true
		addField(rcCountEditor);

		IntegerFieldEditor cacheTimeoutsEditor = new IntegerFieldEditor(
				IFerretPreferenceConstants.PREF_CACHE_TIMEOUTS,
				"Default timeouts for caches (in seconds)",
				getFieldEditorParent());
		cacheTimeoutsEditor.setValidRange(5, 600);	// 600 is arbitrary, true
		addField(cacheTimeoutsEditor);
		
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_BACKGROUND_JOBS_AS_USER,
				"Register background queries as user jobs",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				IFerretPreferenceConstants.PREF_VERBOSE_JOB_TITLES,
				"Use more verbose job titles",
				getFieldEditorParent()));
	}

	protected void addLabel(String text, int span) {
		Label label = new Label(getFieldEditorParent(), SWT.NULL);
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		label.setLayoutData(gd);
		label.setText(text);
		label.setFont(getFieldEditorParent().getFont());
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		if(!result) { return false; }
		Consultancy.getDefault().reconfigure();
//		getPreferenceStore().getString(IFerretPreferenceConstants.PREF_DOSSIER_FONT)
		return true;
	}
}
