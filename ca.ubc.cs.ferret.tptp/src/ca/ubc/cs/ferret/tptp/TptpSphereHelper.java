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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.hyades.models.hierarchy.TRCAgent;
import org.eclipse.hyades.models.hierarchy.TRCAgentProxy;
import org.eclipse.hyades.models.hierarchy.TRCMonitor;
import org.eclipse.hyades.models.hierarchy.TRCNode;
import org.eclipse.hyades.models.hierarchy.TRCProcessProxy;
import org.eclipse.hyades.models.hierarchy.extensions.BinaryExpression;
import org.eclipse.hyades.models.hierarchy.extensions.ExtensionsFactory;
import org.eclipse.hyades.models.hierarchy.extensions.LogicalExpression;
import org.eclipse.hyades.models.hierarchy.extensions.LogicalOperators;
import org.eclipse.hyades.models.hierarchy.extensions.QueryResult;
import org.eclipse.hyades.models.hierarchy.extensions.RelationalOperators;
import org.eclipse.hyades.models.hierarchy.extensions.ResultEntry;
import org.eclipse.hyades.models.hierarchy.extensions.SimpleOperand;
import org.eclipse.hyades.models.hierarchy.extensions.SimpleSearchQuery;
import org.eclipse.hyades.models.hierarchy.util.internal.SimpleSearchQueryEngine;
import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCLanguageElement;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.models.trace.TracePackage;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JdtSphereFactory;
import ca.ubc.cs.ferret.model.AbstractSphereFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ISphereFactory;
import ca.ubc.cs.ferret.model.NamedRelation;
import ca.ubc.cs.ferret.model.SphereHelper;
import ca.ubc.cs.ferret.model.TransformingSphereCompositor;

public class TptpSphereHelper extends SphereHelper {

	public static final String OP_WAS_INVOKED = "DynWasInvoked";
	public static final String OP_WAS_INSTANTIATED = "DynWasInstantiated";
	public static final String OP_USED_METHODS = "DynMethodsUsed";

	protected static final String HCI_FILTERED_JDT = "ca.ubc.cs.ferret.tptp.tb_filtered_jdt";

	protected static TptpSphereHelper singleton;

//    protected MethodDetailer methodDetailer;

	@Override
	public ISphereFactory[] getSphereFactories() {
		return new ISphereFactory[] { new TptpSphereFactory(),
				newWrappedJdtSphereFactory() };
	}
	
	private ISphereFactory newWrappedJdtSphereFactory() {
		return new AbstractSphereFactory() {
			public IStatus canCreate() {
				return Status.OK_STATUS;
			}

			public ISphere createSphere(IProgressMonitor monitor)
					throws FerretConfigurationException {
				monitor.beginTask("Creating " + getDescription(), 10);
				TransformingSphereCompositor tb = new TransformingSphereCompositor(getDescription(),
						new JdtSphereFactory().createSphere(new SubProgressMonitor(monitor, 5)));
				tb.addTransform(new NamedRelation(TptpSphereHelper.OP_WAS_INVOKED));
				monitor.done();
				return tb;
			}

			public String getDescription() {
				return "Static Java restricted to actually-executed methods (JDT/TPTP)";
			}

			public String getId() {
				return getClass().getName();
			}

			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter) {
				return null;
			}

			public ImageDescriptor getImageDescriptor() {
				return TptpPlugin.getImageDescriptor("icons/filtered-jdt-tb.gif");
			}

			public String getHelpContextId() {
				return HCI_FILTERED_JDT;
			}};
	}

	public static boolean isInitializer(TRCMethod trcMethod) {
		return trcMethod.getName().equals("-clinit-")
			|| trcMethod.getName().equals("-init-");
	}


	public static boolean isConstructor(TRCMethod trcMethod) {
		return trcMethod.getName().equals(trcMethod.getDefiningClass().getName());
	}

	protected TptpSphereHelper() {}
    
    public static TptpSphereHelper getDefault() {
		if(singleton == null) {
			 singleton = new TptpSphereHelper();
			 singleton.start();
		}
		return singleton;
    }

    public void start() {}
    public void reset() {
//    	methodDetailer = null;
    }
    public void stop() {
    	if(singleton == this) { singleton = null; }
    	reset();
    }

    @SuppressWarnings("unchecked")
	public static List<TRCMethod> findMethods(String packageName, String className,
    		String methodName, String methodSignature, EObject processes[]) {
		SimpleSearchQuery query = ExtensionsFactory.eINSTANCE.createSimpleSearchQuery();
		
		// AND
		LogicalExpression andExpression = ExtensionsFactory.eINSTANCE.createLogicalExpression();
		andExpression.setOperator(LogicalOperators.AND_LITERAL);
		query.setWhereExpression(andExpression);
	
		andExpression.getArguments().add(createBinaryExpression_Like(
				TracePackage.eINSTANCE.getTRCPackage_Name(), packageName));
		andExpression.getArguments().add(createBinaryExpression_Like(
				TracePackage.eINSTANCE.getTRCClass_Name(), className));
		andExpression.getArguments().add(createBinaryExpression_Like(
				TracePackage.eINSTANCE.getTRCMethod_Name(), methodName));
		andExpression.getArguments().add(createBinaryExpression_Like(
				TracePackage.eINSTANCE.getTRCMethod_Signature(), methodSignature));
		List results = perform(query, processes, TracePackage.eINSTANCE.getTRCMethod());		
		return (List<TRCMethod>)results;
    }
    
	/* snarfed from TPTP's org.eclipse.hyades.use.cases.junit.models.test.ProfilingTraceSearchTest */
	@SuppressWarnings("unchecked")
	protected static BinaryExpression createBinaryExpression_Like(EAttribute feature, String value) {
		BinaryExpression textSearch = ExtensionsFactory.eINSTANCE.createBinaryExpression();
		SimpleOperand operand = ExtensionsFactory.eINSTANCE.createSimpleOperand();
		operand.setFeature(feature);
		textSearch.setLeftOperand(operand);
		
		operand = ExtensionsFactory.eINSTANCE.createSimpleOperand();
//		operand.setValueType(EcorePackage.eINSTANCE.getEString());
		operand.setValue(feature.getEAttributeType());
		operand.setValue(value);
		textSearch.getRightOperands().add(operand);
		textSearch.setCaseInsensitive(false);
		textSearch.setOperator(RelationalOperators.LIKE_LITERAL);
		return textSearch;
	}
	
	@SuppressWarnings("unchecked")
	public static List<TRCClass> findClasses(String packageName, String className, 
			EObject processes[]) {
		SimpleSearchQuery query = ExtensionsFactory.eINSTANCE.createSimpleSearchQuery();

		// AND
		LogicalExpression andExpression = ExtensionsFactory.eINSTANCE.createLogicalExpression();
		andExpression.setOperator(LogicalOperators.AND_LITERAL);
		query.setWhereExpression(andExpression);
	
		andExpression.getArguments().add(createBinaryExpression_Like(
				TracePackage.eINSTANCE.getTRCPackage_Name(), packageName));
		andExpression.getArguments().add(createBinaryExpression_Like(
				TracePackage.eINSTANCE.getTRCClass_Name(), className));
		List<TRCClass> results = (List<TRCClass>)perform(query, processes, 
				TracePackage.eINSTANCE.getTRCClass());
		return results;
	}
	
	@SuppressWarnings("unchecked")
	protected static List<?> perform(SimpleSearchQuery query, EObject processes[], EClass returnClass) {
		Resource resource = null;
		
		for(EObject process : processes) {
			query.getSources().add(EcoreUtil.getURI(process).toString());
			if (resource == null) {
				resource = process.eResource(); 
			}
		}
		
		if (resource == null) {
			return new ArrayList();
		}
		SimpleOperand outputElement = ExtensionsFactory.eINSTANCE.createSimpleOperand();
		outputElement.setType(returnClass);
		query.getOutputElements().clear();
		query.getOutputElements().add(outputElement);
		
		SimpleSearchQueryEngine engine = new SimpleSearchQueryEngine(query, resource.getResourceSet());
		engine.getRequiredPaths().add(TracePackage.eINSTANCE.getTRCProcess_Packages());
		engine.getRequiredPaths().add(TracePackage.eINSTANCE.getTRCPackage_Classes());
		engine.getRequiredPaths().add(TracePackage.eINSTANCE.getTRCClass_Methods());

		QueryResult result = engine.execute();

		if(result == null) {
			return Collections.EMPTY_LIST;
		}
		return (List<?>)((ResultEntry)result.getResultEntries().get(0)).getValue();
	}

	@Override
	public String getMinimalLabel(Object element) {
		if(element instanceof TRCMethod) {
			StringBuffer buffer = new StringBuffer();
			TRCMethod method = (TRCMethod)element;
			buffer.append(method.getDefiningClass().getName());
			buffer.append('.');
			buffer.append(method.getName());
			int start = method.getSignature().indexOf('(');
			int stop = method.getSignature().indexOf(')');		
			boolean hasArgs = stop - start > 1;
			buffer.append(hasArgs ? "(...)" : "()");
			return buffer.toString();
		} else if(element instanceof TRCClass) {
			return ((TRCClass)element).getName();
		}
		return null;
	}

	@Override
	public ImageDescriptor getImage(Object element) {
		if(element instanceof TRCMethod) {
			return TptpPlugin.getImageDescriptor("icons/methods_co.gif");
		} else if(element instanceof TRCClass) {
			return TptpPlugin.getImageDescriptor("icons/class_obj.gif");
		}
		return null;
	}

	public boolean isSourcedFrom(TRCLanguageElement input, Collection<? extends EObject> sources) {
		// Check that the provided TRC object is actually contained within the source roots
		// configured for the sphere parameters for this instance.
		// This is really lame and there must be a better way.
		TRCAgent agent = (TRCAgent)EcoreUtil.getRootContainer(input);
		for(EObject source : sources) {
			if(containsAgent(source, agent)) { return true; }
		}
//		EcoreUtil.getRootContainer(sources.iterator().next())
//		((TRCProcessProxy)sources.iterator().next()).getAgentProxies().
//		((TRCProcessProxy)sources.iterator().next()).getAgentProxies()
//		((TRCProcessImpl)element).getAgent()
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends TRCLanguageElement> T reresolve(T input, ISphere tb) {
		Collection<EObject> sources = TptpSphereFactory.getSources(tb);
		if(isSourcedFrom(input, sources)) { return input; }
		if(input instanceof TRCMethod) {
			TRCMethod m = (TRCMethod)input;
			return (T)getSingleElement(findMethods(m.getDefiningClass().getPackage().getName(),
					m.getDefiningClass().getName(), m.getName(), m.getSignature(),
					TptpSphereFactory.getProcesses(sources))); 
		} else if(input instanceof TRCClass) {
			TRCClass cl = (TRCClass)input;
			return (T)getSingleElement(findClasses(cl.getPackage().getName(),
					cl.getName(), TptpSphereFactory.getProcesses(sources))); 
		}
		FerretPlugin.log(new Status(IStatus.ERROR, "ca.ubc.cs.ferret.tptp", FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
				"Asked to re-resolve unhandled TRC type: " + FerretPlugin.debugPrint(input), null));
		return null;
	}
	
	protected <E> E getSingleElement(List<E> c) {
		if(c.isEmpty()) {
			return null; 
		} else if(c.size() > 1) {
			FerretPlugin.log(new Status(IStatus.WARNING, "ca.ubc.cs.ferret.tptp", FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
					"Expected single element: " + FerretPlugin.compactPrettyPrint(c), null));
		}
		return c.get(0);
	}
	
	protected boolean containsAgent(EObject source, TRCAgent agent) {
		if(source instanceof TRCAgentProxy) {
			return ((TRCAgentProxy)source).getAgent().equals(agent);
		} else if(source instanceof TRCProcessProxy) {
			for(Object proxy : ((TRCProcessProxy)source).getAgentProxies()) {
				if(containsAgent((TRCAgentProxy)proxy, agent)) { return true; }
			}
		} else if(source instanceof TRCNode) {
			TRCNode node = (TRCNode)source;
			for(Object obj : node.getProcessProxies()) {
				if(containsAgent((TRCProcessProxy)obj, agent)) { return true; }
			}
		} else if(source instanceof TRCMonitor) {
			TRCMonitor mon = (TRCMonitor)source;
			for(Object obj : mon.getNodes()) {
				if(containsAgent((TRCNode)obj, agent)) { return true; }
			}
		} else {
			throw new FerretFatalError("unknown TPTP containment type!");
		}
		return false;
	}

	@Override
	public Object getParent(Object object) {
		if(object instanceof TRCMethod) {
			return ((TRCMethod)object).getDefiningClass();
		} else if(object instanceof TRCClass) {
			return ((TRCClass)object).getEnclosedBy();
		}
		return super.getParent(object);
	}
}
