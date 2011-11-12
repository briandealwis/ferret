package ca.ubc.cs.ferret.tests.tptp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.hyades.loaders.util.XMLLoader;
import org.eclipse.hyades.models.hierarchy.TRCCollectionMode;
import org.eclipse.hyades.models.hierarchy.TRCMonitor;
import org.eclipse.hyades.models.trace.TRCClass;
import org.eclipse.hyades.models.trace.TRCMethod;
import org.eclipse.hyades.trace.ui.internal.util.PDCoreUtil;
import org.eclipse.hyades.trace.views.internal.FilteringUtil;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ObjectOrientedRelations;
import ca.ubc.cs.ferret.tptp.TptpSphereFactory;
import ca.ubc.cs.ferret.tptp.TptpSphereHelper;
import ca.ubc.cs.ferret.tptp.jdt.MethodDetailer;
import ca.ubc.cs.ferret.types.ConversionSpecification.Fidelity;


public class JdtToTptpTests extends TestCase {
	protected MethodDetailer md;
	protected TRCMonitor monitor;
	protected EObject processes[];
	
	protected void setUp() throws Exception {
		String traceFileName = "../ac-profile-data-gffc-20060127.trcxml";
		File log = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString(),
				traceFileName);
		assertTrue(log.exists());
		
		int collectionMode = TRCCollectionMode.HEAP_AND_EXECUTION_FULL;
		monitor = PDCoreUtil.createMonitor(ResourcesPlugin.getWorkspace().getRoot(), traceFileName);
        if (!PDCoreUtil.isZipFile(log.toString())) {
            InputStream readStream = new BufferedInputStream(new FileInputStream(log));
//            logLength = log.length();
            XMLLoader processor = new XMLLoader(monitor);
            processor.setCollectionMode(collectionMode);
            processor.loadEvents(readStream, 0, -1);
            processor.cleanUp();
        } else {
            ZipFile zf = new ZipFile(log);
            Enumeration<? extends ZipEntry> entries = zf.entries();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
//                logLength = zipEntry.getSize();
//                if (logLength < log.length())
//                    logLength = Long.MAX_VALUE;

                XMLLoader processor = new XMLLoader(monitor);
                processor.setCollectionMode(collectionMode);
                processor.loadEvents(zf.getInputStream(zipEntry), 0, -1);
                processor.cleanUp();
            }
        }

        List<TRCMonitor> monitors = new ArrayList<TRCMonitor>(1);
        monitors.add(monitor);
		md = new MethodDetailer(Fidelity.Exact, monitors);
		processes = TptpSphereFactory.getProcesses(monitors);
	}

	public void testCheckResolve() {
		List<TRCMethod> methods = FilteringUtil.getFilteredMethods(null, processes);
		for(TRCMethod method : methods) {
			IMethod jdtMethod = md.findMethod(method);
			if(jdtMethod == null && !TptpSphereHelper.getDefault().isConstructor(method)
					&& !TptpSphereHelper.getDefault().isInitializer(method)) {
				System.out.println("WARNING: couldn't map " + method.getDefiningClass().getName() 
						+ "." + method.getName() + method.getSignature());
			}
			if(jdtMethod == null) { continue; }
			try {
				assertEquals(method.getSignature(), MethodDetailer.createTPTPSignature(jdtMethod));
			} catch (JavaModelException e) {
				fail(e.getMessage());
			}
			List<TRCMethod> found =
				TptpSphereHelper.findMethods(method.getDefiningClass().getPackage().getName(), 
					method.getDefiningClass().getName(), method.getName(),
					method.getSignature(), processes);
			assertEquals(1, found.size());
		}
	}
	
	   public void testTptpIsInterfacePredicateRelationOnNonInterface() {
			Object o = new Object();
			try {
				ISphere tb = new TptpSphereFactory().createSphere(new NullProgressMonitor());
				IRelation rel = tb.resolve(new NullProgressMonitor(), ObjectOrientedRelations.OP_IS_INTERFACE, o);
				assertFalse(rel.hasNext());

				List<TRCClass> classes = FilteringUtil.getFilteredClasses(null, processes);
				for(TRCClass cl : classes) {
					rel = tb.resolve(new NullProgressMonitor(), ObjectOrientedRelations.OP_IS_INTERFACE, cl);
					assertFalse(rel.hasNext());
				}
			} catch (FerretConfigurationException e) {
				fail(e.getMessage());
			}
	    }
	    
	   public void testTptpIsClassPredicateRelationOnNonClass() {
			Object o = new Object();
			try {
				ISphere tb = new TptpSphereFactory().createSphere(new NullProgressMonitor());
				IRelation rel = tb.resolve(new NullProgressMonitor(), ObjectOrientedRelations.OP_IS_CLASS, o);
				assertNull(rel);
			} catch(UnsupportedOperationException e) {
				// this is good
			} catch (FerretConfigurationException e) {
				fail(e.getMessage());
			}
	    }

	   public void testTptpIsInterfacePredicateRelationOnClass() {
			try {
				ISphere tb = new TptpSphereFactory().createSphere(new NullProgressMonitor());

				List<TRCClass> classes = FilteringUtil.getFilteredClasses(null, processes);
				for(TRCClass cl : classes) {
					IRelation rel = tb.resolve(new NullProgressMonitor(), ObjectOrientedRelations.OP_IS_CLASS, cl);
					assertTrue(rel.hasNext());
				}
			} catch (FerretConfigurationException e) {
				fail(e.getMessage());
			}
	    }

}
