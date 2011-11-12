package ca.ubc.cs.ferret.jdt.ops;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.model.AbstractCollectionBasedRelation;

public class ProvidedTypesRelation extends AbstractCollectionBasedRelation<IPackageFragment> {

	public ProvidedTypesRelation() {}

	@Override
	protected Class<IPackageFragment> getInputType() {
		return IPackageFragment.class;
	}

	@Override
	protected Collection<?> realizeCollection(IPackageFragment pkgfrag) {
		Set<IType> results = new HashSet<IType>();
		try {
			monitor.beginTask("finding defined types of " + pkgfrag.getElementName(), 10);
			IProgressMonitor sm;
			pkgfrag.open(new SubProgressMonitor(monitor,3));
			IClassFile cfs[] = pkgfrag.getClassFiles();
			sm = new SubProgressMonitor(monitor, 3);
			sm.beginTask("Examining class files in " + pkgfrag.getElementName(), cfs.length);
			for(IClassFile cf : cfs) {
				IType t = cf.findPrimaryType();
				if(t != null) { results.add(t); }
				sm.worked(1);
			}
			sm.done();
			sm = new SubProgressMonitor(monitor, 3);
			ICompilationUnit cus[] = pkgfrag.getCompilationUnits();
			sm.beginTask("Examining compilation units  in " + pkgfrag.getElementName(), cus.length);
			for(ICompilationUnit cu : cus) {
				Collections.addAll(results, cu.getAllTypes());
				sm.worked(1);				
			}
			sm.done();
			pkgfrag.close();
			monitor.worked(1);
		} catch(JavaModelException e) {
			JavaModelHelper.logJME(e);
		} finally {
			monitor.done();
		}
		return results;
	}
}
