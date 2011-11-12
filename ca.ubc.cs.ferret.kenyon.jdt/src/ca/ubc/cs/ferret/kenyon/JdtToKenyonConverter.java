package ca.ubc.cs.ferret.kenyon;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.display.DelegatingPrettyPrinter;
import ca.ubc.cs.ferret.display.IPrettyPrinter;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.types.AbstractTypeConverter;
import ca.ubc.cs.ferret.types.ConversionException;
import ca.ubc.cs.ferret.types.ConversionResult;
import ca.ubc.cs.ferret.types.ConversionSpecification;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;
import ca.ubc.cs.objhdl.JdtMapper;
import ca.ubc.cs.objhdl.ObjectMapping;
import edu.se.evolution.kenyon.graph.Node;
import edu.se.evolution.kenyon.graph.schemas.JavaSchema;

public class JdtToKenyonConverter extends AbstractTypeConverter {
	public JdtToKenyonConverter() { }
	
	public ConversionResult<?> convert(Object providedInstance,
			ConversionSpecification spec, ISphere sphere)
			throws ConversionException {
		KenyonEclipseProjectMapper projectMap = 
			sphere.get(KenyonSphereHelper.KEY_PROJECTS_MAPPINGS, KenyonEclipseProjectMapper.class);
		try {
			if(providedInstance instanceof IMember) {
				if(spec.getDesiredClass() == IKHandlesSource.class) {
					IMember member = (IMember)providedInstance;
					KSQLHandlesSource source = new KSQLHandlesSource();
					String hi = member.getHandleIdentifier();
					hi = projectMap.rewriteJDTHandleIdentifier(hi);
					source.addHandle(0, JdtMapper.HANDLE_TYPE_JAVA + ":" + hi);
					int ltIndex = hi.indexOf('<');
					if(ltIndex >= 0) {
						// In case there is no mapping, use of % to mask out project and package root part
						// handles are of form "=projectname/pkgroot<..."						
						String pname = hi.substring(1, ltIndex);
						source.addHandle(1, JdtMapper.HANDLE_TYPE_JAVA + ":%"
								+ hi.substring(ltIndex));
					}
					return wrap(spec, Fidelity.Equivalent, IKHandlesSource.class,
							source);
				}
			} else if(providedInstance instanceof Node) {
				Object resolved = resolve(((Node)providedInstance).getName(), projectMap);
				if(resolved == null) { return null; }
				if(spec.getDesiredClass() == IPrettyPrinter.class) {
					return wrap(spec, Fidelity.Exact, IPrettyPrinter.class,
							new DelegatingPrettyPrinter(resolved));
				} else {
					if(!spec.getDesiredClass().isInstance(resolved)) { return null; }
					// approximate as we have no idea if they even roughly correspond
					// we could check that the revision number is the same, but otherwise
					// we don't have enough information from Kenyon to be certain
					ConversionResult<Object> result = 
						new ConversionResult(spec.getDesiredClass(), Fidelity.Approximate);
					result.addResult(resolved);
					return result;
				}
			} else if(providedInstance instanceof IFile) {
				if(spec.getDesiredClass() == IKHandlesSource.class) {
					IFile file = (IFile)providedInstance;
					KSQLHandlesSource source = new KSQLHandlesSource();
					String hdl = ObjectMapping.describe(file);
					hdl = projectMap.rewriteObjectHandle(hdl);
					source.addHandle(0, hdl);
//					int slIndex = hi.indexOf('/');
//					if(slIndex >= 0) {
//						// In case there is no mapping, use of % to mask out project and package root part
//						// handles are of form "=projectname/pkgroot<..."						
//						String pname = hi.substring(1, slIndex);
//						source.addHandle(1, JdtMapper.HANDLE_TYPE_JAVA + ":%"
//								+ hi.substring(slIndex));
//					}
					return wrap(spec, Fidelity.Equivalent, IKHandlesSource.class,
							source);
				}
			}
		} catch(ClassNotFoundException e) {
			FerretPlugin.log(e);
		}
		return null;
	}

	/**
	 * 
	 * @param id the kenyon object handle identifier (e.g., a URL)
	 * @param projectMapping
	 * @return the resolved object
	 */
	public static Object resolve(String id, KenyonEclipseProjectMapper projectMapping) {
		Object ro = ObjectMapping.resolve(id);
		// this bit of JDT-ness is unfortunate 
		if(ro != null && 
				(!(ro instanceof IJavaElement) || ((IJavaElement)ro).exists())) {
			return ro;
		}
		Object best = ro;
		for(String handle : projectMapping.rewriteKenyonHandle(id)) {
			ro = ObjectMapping.resolve(handle);
			if(ro != null && 
					(!(ro instanceof IJavaElement) || ((IJavaElement)ro).exists())) {
				return ro;
			}
			if(best == null) { best = ro; }
		}
		return best;
	}

}
