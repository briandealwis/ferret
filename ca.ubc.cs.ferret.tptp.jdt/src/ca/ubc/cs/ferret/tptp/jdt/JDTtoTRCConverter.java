package ca.ubc.cs.ferret.tptp.jdt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.display.IPrettyPrinter;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.Sphere;
import ca.ubc.cs.ferret.tptp.TptpClass;
import ca.ubc.cs.ferret.tptp.TptpMethod;
import ca.ubc.cs.ferret.tptp.TptpSphereFactory;
import ca.ubc.cs.ferret.types.AbstractTypeConverter;
import ca.ubc.cs.ferret.types.ConversionException;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification;
import ca.ubc.cs.ferret.types.IEquivalenceTesting;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;

public class JDTtoTRCConverter extends AbstractTypeConverter {

	public ConversionResult<?> convert(Object object,
			ConversionSpecification spec, ISphere tb) throws ConversionException {
		if(object instanceof IMethod) {
			return convertJDTMethod((IMethod)object, spec, tb);
		} else if(object instanceof TRCMethod) {
			return convertTRCMethod((TRCMethod)object, spec, tb);
		} else if(object instanceof IType) {
			return convertJDTType((IType)object, spec, tb);
		} else if(object instanceof TRCClass) {
			return convertTRCClass((TRCClass)object, spec, tb);
		}
		throw new ConversionException("Unable to handle type " + object.getClass().getName(),
				object);
	}
	
	protected ConversionResult<?> convertTRCMethod(TRCMethod trcMethod,
			ConversionSpecification spec, ISphere tb) throws ConversionException {
		if(spec.getDesiredType().equals(IEquivalenceTesting.class.getName())
				|| spec.getDesiredType().equals(IPrettyPrinter.class.getName())) {
			return wrap(spec, Fidelity.Exact, IEquivalenceTesting.class, new TptpMethod(trcMethod));
		}
		IMethod result = MethodDetailer.findMethod(trcMethod);
		Fidelity fidelity = Fidelity.Exact;
		if(tb != null) {
			for(MethodDetailer md : getMethodDetailers(tb).values()) {
				if(md.contains(trcMethod)) {
					fidelity = fidelity.least(md.getFidelity());
				}
			}
		} else { 
			fidelity = Fidelity.Approximate;
		}
		return wrap(spec, spec.getFidelity(), IMethod.class, result);
	}

	protected ConversionResult<?> convertTRCClass(TRCClass trcClass,
			ConversionSpecification spec, ISphere tb) throws ConversionException {
		if(spec.getDesiredType().equals(IEquivalenceTesting.class.getName())
				|| spec.getDesiredType().equals(IPrettyPrinter.class.getName())) {
			return wrap(spec, Fidelity.Exact, IEquivalenceTesting.class, new TptpClass(trcClass));
		}
		IType result = MethodDetailer.findType(trcClass);
		Fidelity fidelity = Fidelity.Exact;
		if(tb != null) {
			for(MethodDetailer md : getMethodDetailers(tb).values()) {
				if(md.contains(trcClass)) { fidelity = fidelity.least(md.getFidelity()); }
			}
		} else {
			fidelity = spec.getFidelity();
		}
		return wrap(spec, fidelity, IType.class, result);
	}

	protected ConversionResult<TRCClass> convertJDTType(IType type,
			ConversionSpecification spec, ISphere tb) throws ConversionException {
		if(tb == null) { return null; }		// we can't do nothing without a sphere

		try {
			if(type.isInterface()) {
				return convertInterfaceType(spec, type, tb);
			}
		} catch (JavaModelException e) {
			throw new ConversionException(e);
		}
		for(MethodDetailer md : getMethodDetailers(tb).values()) {
			TRCClass result = md.findType(type);
			if(result != null) {
				return wrap(spec, md.getFidelity(), TRCClass.class, result);
			}
		}
		return wrap(spec, spec.getFidelity(), TRCClass.class, null);
	}

	protected  ConversionResult<TRCClass> convertInterfaceType(
			ConversionSpecification spec, IType iface, ISphere tb) throws ConversionException {
		Collection<IType> implementors = 
			JavaModelHelper.getDefault().getImplementingClasses(iface, new NullProgressMonitor());
		Collection<MethodDetailer> mds = getMethodDetailers(tb).values();
		if(mds.isEmpty()) { return null; }
		ConversionResult<TRCClass> cr = new ConversionResult<TRCClass>(TRCClass.class, Fidelity.Equivalent);
		Fidelity resultingFidelity = Fidelity.Exact;
		for(IType iClazz : implementors) {
			for(MethodDetailer md : mds) {
				TRCClass trcClazz = md.findType(iClazz);
				if(trcClazz != null) {
					cr.addResult(trcClazz);
					resultingFidelity = resultingFidelity.least(md.getFidelity());
				}
			}
		}
		cr.setFidelity(resultingFidelity);
		return cr;
	}

	protected ConversionResult<TRCMethod> convertJDTMethod(IMethod method,
			ConversionSpecification spec, ISphere tb) throws ConversionException {
		if(tb == null) { return null; }		// we can't do nothing without a sphere
		try {
			if(method.getDeclaringType().isInterface()) {
				return convertInterfaceMethod(spec, method, tb);
			}
		} catch (JavaModelException e) {
			throw new ConversionException(e);
		}
		Fidelity resultingFidelity = Fidelity.Exact;
		ConversionResult<TRCMethod> cr = new ConversionResult<TRCMethod>(TRCMethod.class, Fidelity.Equivalent);
		for(MethodDetailer md : getMethodDetailers(tb).values()) {
			TRCMethod trcMethod = md.findMethod(method);
			if(trcMethod != null) {
				cr.addResult(trcMethod);
				resultingFidelity = resultingFidelity.least(md.getFidelity());
			}
		}
		cr.setFidelity(resultingFidelity);
		return cr;
	}

	protected ConversionResult<TRCMethod> convertInterfaceMethod(ConversionSpecification spec, 
			IMethod ifaceMethod, ISphere tb) throws ConversionException {
		Collection<IMethod> implementors = JavaModelHelper.getDefault().getImplementingMethods(ifaceMethod, new NullProgressMonitor());
		Collection<MethodDetailer> mds = getMethodDetailers(tb).values();
		if(mds.isEmpty()) { return null; }
		ConversionResult<TRCMethod> cr = new ConversionResult<TRCMethod>(TRCMethod.class, Fidelity.Equivalent);
		Fidelity resultingFidelity = Fidelity.Exact;
		for(IMethod clMethod : implementors) {
			for(MethodDetailer md : mds) {
				TRCMethod trcMethod = md.findMethod(clMethod);
				if(trcMethod != null) {
					cr.addResult(trcMethod);
					resultingFidelity = resultingFidelity.least(md.getFidelity());
				}
			}
		}
		cr.setFidelity(resultingFidelity);
		return cr;
	}

	@SuppressWarnings("unchecked")
	protected Map<ISphere,MethodDetailer> getMethodDetailers(ISphere sourceTB) throws ConversionException {
		if(sourceTB == null) {
			throw new ConversionException("no configured TPTP information sources!");
		}
		Map<ISphere,MethodDetailer> detailers = sourceTB.getAll(TptpSphereFactory.CONVERSION_DETAILS, MethodDetailer.class);
		if(detailers != null && !detailers.isEmpty()) { return detailers; }

		Map<ISphere,Collection> sources =
			sourceTB.getAll(TptpSphereFactory.TRACE_ROOTS, Collection.class);
		if(sources == null || sources.isEmpty()) {
			throw new ConversionException("no configured TPTP information sources!");
		}
		detailers = new HashMap<ISphere, MethodDetailer>();
		for(ISphere tb : sources.keySet()) {
			Collection<EObject> roots = (Collection<EObject>)sources.get(tb);
			Fidelity fidelity = tb.get(TptpSphereFactory.TRACE_FIDELITY, Fidelity.class);
			MethodDetailer md = new MethodDetailer(fidelity, roots);
			((Sphere)tb).set(TptpSphereFactory.CONVERSION_DETAILS, md);
			detailers.put(tb, md);
		}
		return detailers;
	}
}
