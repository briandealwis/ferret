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
package ca.ubc.cs.ferret.tptp.jdt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCLanguageElement;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.trace.views.internal.FilteringUtil;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.tptp.TptpClass;
import ca.ubc.cs.ferret.tptp.TptpMethod;
import ca.ubc.cs.ferret.tptp.TptpPlugin;
import ca.ubc.cs.ferret.tptp.TptpSphereHelper;
import ca.ubc.cs.ferret.tptp.TptpSphereFactory;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class MethodDetailer {
	protected Collection<EObject> sources;
	protected EObject processes[];
	protected Fidelity fidelity = Fidelity.Exact;
	
	public MethodDetailer(Fidelity f, Collection<? extends EObject> sources) {
		this.sources = new ArrayList<EObject>(sources);
		fidelity = f;
		processes = TptpSphereFactory.getProcesses(sources);
	}

	@SuppressWarnings("unchecked")
	public void debug() {
		List<TRCMethod> methods = FilteringUtil.getFilteredMethods(null, processes);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/methods"));
			for(TRCMethod m : methods) {
				writer.append(m.getDefiningClass().getPackage().getName());
				writer.append(':');
				writer.append(m.getDefiningClass().getName());
				writer.append(':');
				writer.append(m.getName());
				writer.append(':');
				writer.append(m.getSignature());
				writer.append('\n');
			}
			writer.close();
		} catch(IOException e) {}
	}
	
	public TRCMethod findMethod(final IMethod method) {
		try {
			IType type = method.getDeclaringType();
			List<TRCMethod> results = TptpSphereHelper.findMethods(type.getPackageFragment().getElementName(), 
					type.getElementName(), method.getElementName(),
					createTPTPSignature(method), processes);
/*	debug();
			Collection<TRCMethod> same = 
				CollectionUtils.select(FilteringUtil.getFilteredMethods(null, processes),
						new Predicate<TRCMethod>() {
							public boolean evaluate(TRCMethod m) {
								return m.getName().equals(method.getElementName());
							}});
*/
			if(results.isEmpty()) {
//				debug();
				return null; 
			} else if(results.size() > 1) {
				FerretPlugin.log(new Status(IStatus.WARNING, "ca.ubc.cs.ferret.tptp-jdt", FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
						"IMethod " + FerretPlugin.compactPrettyPrint(method) + " resulted in " + results.size() + " TRCMethods: "
						+ FerretPlugin.compactPrettyPrint(results), null));
			}
			return results.get(0);
		} catch(JavaModelException e) {
			FerretPlugin.logHandledException(e);
			return null;
		}
	}

	/**
	 * Create a TPTP method signature corresponding to the provided method.
	 * Public only for testing purposes. 
	 * @param signature
	 * @return the TPTP signature
	 * @throws JavaModelException 
	 */
	public static String createTPTPSignature(IMethod method) throws JavaModelException {
		String sig = method.getSignature().replace('/', '.');
		String parameterTypes[] = Signature.getParameterTypes(sig);
		StringBuffer tptpSignature = new StringBuffer();
		tptpSignature.append('(');
		for(int i = 0; i < parameterTypes.length; i++) {
			tptpSignature.append(generateTPTPTypeSignature(parameterTypes[i], method));
			if(i < parameterTypes.length - 1) { tptpSignature.append(", "); }
		}
		tptpSignature.append(")");
		if(!method.isConstructor()) {
			tptpSignature.append(' ');
			tptpSignature.append(generateTPTPTypeSignature(method.getReturnType(), method));
		}
		return tptpSignature.toString();
	}

	public static String generateTPTPTypeSignature(String typeSignature, IMember source) throws JavaModelException {
		switch(Signature.getTypeSignatureKind(typeSignature)) {
		case Signature.ARRAY_TYPE_SIGNATURE:
			int arrayCount = Signature.getArrayCount(typeSignature);
			String result = generateTPTPTypeSignature(typeSignature.substring(arrayCount), source);
			for(int i = 0; i < arrayCount; i++) {
				result = result + "[]";
			}
			return result;
			
		case Signature.CLASS_TYPE_SIGNATURE:
			return resolveTypeSignature(typeSignature, source);

		case Signature.BASE_TYPE_SIGNATURE:
    		return Signature.toString(typeSignature);

		case Signature.TYPE_VARIABLE_SIGNATURE:
			String equivalent = typeSignature;
			for(Entry<String,String> mapping : generateGenericMapping(source.getDeclaringType()).entrySet()) {
				equivalent = equivalent.replace(mapping.getKey(), mapping.getValue());
			}
			if(!equivalent.equals(typeSignature)) {
				return generateTPTPTypeSignature(equivalent, source);
			}
			/*FALLTHROUGH*/
		case Signature.CAPTURE_TYPE_SIGNATURE:
		case Signature.WILDCARD_TYPE_SIGNATURE:
		default:
    		return Signature.toString(typeSignature);
		}
	}

	private static Map<String,String> generateGenericMapping(IType jt) throws JavaModelException {
		return generateGenericMapping(jt, new HashMap<String, String>());
	}
	
	private static Map<String,String> generateGenericMapping(IType jt, Map<String,String> genericMapping) throws JavaModelException {
		if(jt == null) { return genericMapping; }
		for(String mapping : jt.getTypeParameterSignatures()) {
			int colonIndex = mapping.indexOf(':');
			String typeVarNameSignature = "T" + mapping.substring(0, colonIndex) + ";";
			// now find the first specified type bound
			String typeBoundSignature = null;
			int typeNameIndex = colonIndex + 1;
			while(typeNameIndex < mapping.length()) {
				int nextColonIndex = mapping.indexOf(':', typeNameIndex);
				if(nextColonIndex < 0) { 
					nextColonIndex = mapping.length();
				}
				if(nextColonIndex - typeNameIndex > 0) {
					typeBoundSignature = "L" + 
						resolveTypeSignature(mapping.substring(typeNameIndex, nextColonIndex), jt) +";";
					break;
				}
				typeNameIndex = nextColonIndex + 1;
			}
			if(typeBoundSignature == null || typeBoundSignature.length() == 0) {
				typeBoundSignature = "Ljava.lang.Object;";	// the default
			}
			genericMapping.put(typeVarNameSignature, typeBoundSignature); 
		}
		return generateGenericMapping(jt.getDeclaringType(), genericMapping);
	}

	private static String resolveTypeSignature(String typeSignature, IMember source) throws JavaModelException {
		typeSignature = typeSignature.replace('/', '.');
		if (Signature.getSignatureQualifier(typeSignature).length() == 0) {
			return JavaModelHelper.getDefault().resolveTypeName(
						Signature.getSignatureSimpleName(typeSignature), source);
		}
		return Signature.getSignatureQualifier(typeSignature) + "."
				+ Signature.getTypeErasure(Signature.getSignatureSimpleName(typeSignature));
	}

	public TRCClass findType(IType type) {
		List<TRCClass> results = TptpSphereHelper.findClasses(type.getPackageFragment().getElementName(), 
				type.getElementName(), processes);
		if(results.isEmpty()) {
			return null; 
		} else if(results.size() > 1) {
			FerretPlugin.log(new Status(IStatus.WARNING, "ca.ubc.cs.ferret.tptp-jdt", FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
					"IType " + FerretPlugin.compactPrettyPrint(type) + " resulted in " + results.size() + " TRCClasses: "
					+ FerretPlugin.compactPrettyPrint(results), null));
		}
		return results.get(0);
	}


	public static IMethod findMethod(TRCMethod trcMethod) {
		if(TptpSphereHelper.isInitializer(trcMethod)) { return null; } // nothing we can do?
		IType type = findType(trcMethod.getDefiningClass());
		if(type == null) { return null; }
		return findMethod(type, trcMethod);
	}

	protected static IMethod findMethod(IType jType, TRCMethod trcMethod) {
		boolean isConstructor = TptpSphereHelper.isConstructor(trcMethod);
		// Inspect: jType.getMethods()   trcMethod.getDefiningClass().getName()
		String jdtParameterSignatures[] = getParameterTypes(trcMethod.getSignature()); 
		IMethod jMethod = jType.getMethod(
				isConstructor ? jType.getElementName() : trcMethod.getName(), 
						jdtParameterSignatures);
		IMethod matches[] = jType.findMethods(jMethod);
		if(matches != null && matches.length != 0) { return matches[0]; }
		
		if(jdtParameterSignatures.length > 0) {
			// Try applying generics mappings to the method args to see if
			// equivalent methods can be found
			try {
				Map<String,String> genericMapping = generateGenericMapping(jType);
				for(IMethod m : jType.getMethods()) {
					if(!m.getElementName().equals(trcMethod.getName())) { continue; }
					String mPT[] = m.getParameterTypes();
					if(mPT.length != jdtParameterSignatures.length) { continue; }
					boolean methodMatches = true; 
					for(int i = 0; i < mPT.length; i++) {
						if(mPT[i].equals(jdtParameterSignatures[i])) { continue; }	// parm is ok
						String equivalent = mPT[i];
						for(Entry<String, String> mapping : genericMapping.entrySet()) {
							equivalent = equivalent.replace(mapping.getKey(), mapping.getValue());
						}
						methodMatches = methodMatches &&
							equivalent.equals(jdtParameterSignatures[i]);
					}
					if(methodMatches) { return m; }
				}
			} catch(JavaModelException e) {
				JavaModelHelper.logJME(e);
			}
		}
		FerretPlugin.log(new Status(IStatus.WARNING, "ca.ubc.cs.ferret.tptp-jdt", FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE, 
				(isConstructor ? "(Implicit constructor?) " : "") 
				+ "Method not found: " + trcMethod.getName() + trcMethod.getSignature()
				+ " in type " + jType.getFullyQualifiedName() + ": " + trcMethod, null));
		return null;
	}

	public static IType findType(TRCClass type) {
		String dtn = getTypeName(type);
		IType jType = JavaModelHelper.getDefault().resolveType(dtn);
		if(jType == null) {
			FerretPlugin.log(new Status(IStatus.WARNING, TptpPlugin.pluginID, 0, 
					"Unable to resolve type " + dtn, null));
		}
		return jType;
	}

	public Fidelity getFidelity() {
		return fidelity;
	}
	protected static String getTypeName(TRCClass trcClass) {
		String packageName = trcClass.getPackage().getName();
		if(packageName.length() == 0) {
			return trcClass.getName();
		}
		return packageName + "." + trcClass.getName();
	}

	/**
	 * Sadly necessary due to TPTP signature-rewriting bogusness.
	 * Do your pretty-printing at UI time, not an XML-reading time!  Sheesh.
	 * @param signature
	 * @return array of type names
	 */
	protected static String[] getParameterTypes(String signature) {
		// Stolen from OpenJavaSource.getParametersFromMethodSignature(...)
		int start = signature.indexOf('(');
		int stop = signature.indexOf(')');		
		String parms = signature.substring(start+1, stop);
		if (parms.length() == 0) { return new String [0]; }
		List<String> parameters = new LinkedList<String>();

		start = 0;
		do {
			stop = parms.indexOf(',', start);
			if(stop < 0) { stop = parms.length(); }
			String extract = parms.substring(start, stop).trim();
			// FIXME: isn't it an error if length() == 0?
			if(extract.length() > 0) {
				parameters.add(Signature.createTypeSignature(extract, true));
			}
			start = stop + 1;
		} while(start < parms.length());
		return parameters.toArray(new String[parameters.size()]);
	}

	public IMethod findMethod(TptpMethod methodDescription) {
		TRCMethod sample = methodDescription.getExemplar();
		return findMethod(sample);
	}

	public IType findType(TptpClass classDescription) {
		TRCClass sample = classDescription.getExemplar();
		return findType(sample);
	}
	
	public boolean contains(TRCLanguageElement input) {
		return TptpSphereHelper.getDefault().isSourcedFrom(input, sources);
	}
}
