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
package ca.ubc.cs.ferret.tptp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.trace.views.internal.FilteringUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.AbstractSphereFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.model.IdentityRelation;
import ca.ubc.cs.ferret.model.NullRelation;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.model.Sphere;
import ca.ubc.cs.ferret.tptp.ops.DynamicInstantiatesTypeRelation;
import ca.ubc.cs.ferret.tptp.ops.DynamicMethodReferencesRelation;
import ca.ubc.cs.ferret.tptp.ops.DynamicMethodsCalledByClassRelation;
import ca.ubc.cs.ferret.tptp.ops.DynamicMethodsCalledRelation;
import ca.ubc.cs.ferret.tptp.ops.DynamicMethodsUsedRelation;
import ca.ubc.cs.ferret.tptp.ops.DynamicWasInstantiatedRelation;
import ca.ubc.cs.ferret.tptp.ops.DynamicWasInvokedRelation;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class TptpSphereFactory extends AbstractSphereFactory {
	public static final String ID = TptpSphereFactory.class.getName();
	public static final String TRACE_ROOTS = "tptp-trace-roots";
	public static final String TRACE_FIDELITY = "tptp-trace-fidelity";
	public static final String CONVERSION_DETAILS = "tptp-conversion-details";
	private static final String HCI_TPTP_TB = "ca.ubc.cs.ferret.tptp.tb";

	protected Collection<EObject> sources;
	protected Fidelity fidelity = Fidelity.Approximate;
	
	public String getId() {
		return ID;
	}

	public String getDescription() {
		if(sources == null) { 
			return "Java dynamic run-time queries (TPTP)"; 
		}
		return "Java run-time: " + FerretPlugin.compactPrettyPrint(sources);
	}

	public ISphere createSphere(IProgressMonitor monitor) throws FerretConfigurationException {
		monitor.beginTask("Creating TPTP Sphere", 2);
//		TransformingSphereCompositor tb = new TransformingSphereCompositor("Java run-time-based queries (" + FerretPlugin.prettyPrint(sources) + ")");
		Sphere tb = new Sphere("Java run-time-based queries (" + FerretPlugin.prettyPrint(sources) + ")");
		tb.set(TRACE_ROOTS, sources);
		tb.set(TRACE_FIDELITY, fidelity);
		
		// IsClass is the same as WasInstantiated; no TptpClass can be an interface
//		tb.register(ObjectOrientedRelations.OP_IS_CLASS, new DynamicWasInstantiatedRelation());
//		tb.register(ObjectOrientedRelations.OP_IS_INTERFACE, new NullRelation());
		
		tb.register(TptpSphereHelper.OP_WAS_INVOKED, new DynamicWasInvokedRelation());
		tb.register(TptpSphereHelper.OP_WAS_INSTANTIATED, new DynamicWasInstantiatedRelation());
		tb.register(ObjectOrientedRelations.OP_METHOD_REFERENCES, new DynamicMethodReferencesRelation());
		tb.register(ObjectOrientedRelations.OP_METHODS_CALLED,
				new DynamicMethodsCalledRelation(),
				new DynamicMethodsCalledByClassRelation());
		tb.register(ObjectOrientedRelations.OP_CLASS_INSTANTIATORS, new DynamicInstantiatesTypeRelation());
		tb.register(ObjectOrientedRelations.OP_INSTANTIATORS, new DynamicInstantiatesTypeRelation());
		tb.register(ObjectOrientedRelations.OP_IMPLEMENTORS, new IdentityRelation(TRCClass.class));
		tb.register(TptpSphereHelper.OP_USED_METHODS, new DynamicMethodsUsedRelation());
		monitor.done();
		return tb;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<EObject> getSources(ISphere tb) {
		return (Collection<EObject>)tb.get(TRACE_ROOTS, Collection.class);
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == IWizardPage.class) {
			return new TptpSphereWizardPage(this);
		}
		return null;
	}

	public void setSources(EObject s[]) {
		sources = new HashSet<EObject>();
		Collections.addAll(sources, s);
	}

	public EObject[] getSources() {
		if(sources == null) { return new EObject[0]; }
		return sources.toArray(new EObject[sources.size()]);
	}

	public void setFidelity(Fidelity f) {
		fidelity = f;
	}
	
	public IStatus canCreate() {
		if(sources != null && !sources.isEmpty()) { return Status.OK_STATUS; }
		return new Status(IStatus.ERROR, TptpPlugin.pluginID, FerretErrorConstants.CONFIGURATION_ERROR,
				"No configured TPTP sources", null);
	}

	public static EObject[] getProcesses(ISphere tb) {
		return getProcesses(getSources(tb));
	}
	
	@SuppressWarnings("unchecked")
	public static EObject[] getProcesses(Iterable<? extends EObject> sources) {
		ArrayList<EObject> results = new ArrayList<EObject>();
		for(EObject source : sources) {
			results.addAll(FilteringUtil.getProcessList(source));
		}
		return results.toArray(new EObject[results.size()]);
	}

	@SuppressWarnings("unchecked")
	public static EObject[] getProcesses(EObject sources[]) {
		ArrayList<EObject> results = new ArrayList<EObject>();
		for(EObject source : sources) {
			results.addAll(FilteringUtil.getProcessList(source));
		}
		return results.toArray(new EObject[results.size()]);
	}

	public ImageDescriptor getImageDescriptor() {
		return TptpPlugin.getImageDescriptor("icons/tptp-tb.gif");
	}

	public String getHelpContextId() {
		return HCI_TPTP_TB;
	}

	@Override
	public ISphereFactory clone() {
		TptpSphereFactory cl = (TptpSphereFactory)super.clone();
		if(sources != null) {
			cl.sources = new HashSet<EObject>(sources);
		}
		return cl;
	}
}
