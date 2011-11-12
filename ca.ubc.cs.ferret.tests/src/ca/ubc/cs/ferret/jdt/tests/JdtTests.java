/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenuItem;

import junit.framework.TestCase;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.jdt.ThrowsStatementFinder;
import ca.ubc.cs.ferret.jdt.ops.JdtIsClassRelation;
import ca.ubc.cs.ferret.jdt.ops.JdtIsFieldRelation;
import ca.ubc.cs.ferret.jdt.ops.JdtIsInterfaceRelation;
import ca.ubc.cs.ferret.model.IRelation;
import ca.ubc.cs.ferret.model.IRelationFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.Sphere;
import ca.ubc.cs.ferret.tests.support.TestProject;

public class JdtTests extends TestCase {
    protected static TestProject testProject;
    public JavaModelHelper jmh;
    public IType javaLangObject;
    
    protected long startTime;
    
    public static void createWorkspace() throws CoreException, InterruptedException, InvocationTargetException {
    	testProject = new TestProject("test-project");
        IPackageFragment testPkg = testProject.createPackage("test");
        IType testType = testProject.createType(testPkg, "Test.java", "public class Test {public class Foo {} }" );
        testType.createMethod("public final void setSourceRange(int startPosition, int length) { }",null, false, null);
        testType.createMethod("public String setSourceRange(StringBuilder source, int length) { }",null, false, null);
        testProject.getJavaProject().open(new NullProgressMonitor());
    }
    
    public static void destroyWorkspace() {
        try {
//        	ResourcesPlugin.getWorkspace().getRoot().delete(true, true, new NullProgressMonitor());
            testProject.getProject().delete(true, new NullProgressMonitor());
		} catch (CoreException e) {
			fail("Could not remove test project: " + e);
		}
    }
    
    public void setUp() throws Exception {
    	createWorkspace();
    	
        jmh = JavaModelHelper.getDefault();
        testResolveType();  // populate javaLangObject
        super.setUp();
        System.out.println("Running test: " + getName());
        startTime = System.currentTimeMillis();
    }
    
    public void tearDown() {
        long stopTime = System.currentTimeMillis();
        System.out.println(getName() + ": time=" +
                DurationFormatUtils.formatDurationHMS(stopTime - startTime));
        
        jmh = null;
        destroyWorkspace();
    }
    
    public void testResolveType() {
        javaLangObject = jmh.resolveType("java.lang.Object"); 
        assertNotNull(javaLangObject);
        assertEquals("java.lang.Object", javaLangObject.getFullyQualifiedName());
    }
    
    public void testIsImmediateSubclass() {
        IType jls = jmh.resolveType("java.lang.String");
        
        try {
            assertTrue(jmh.isImmediateSubtype(jls, javaLangObject));
            assertTrue(jmh.isSubtype(jls, javaLangObject));
        } catch (JavaModelException e) {
            fail("JavaModelException");
        }
    }
    
    public void testGetSupertypes() {
        IType jls = jmh.resolveType("java.lang.String");
        assertEquals(0, jmh.getSupertypes(javaLangObject, new NullProgressMonitor()).length);
        assertFalse(0 == jmh.getSupertypes(jls, new NullProgressMonitor()).length);
    }
    
    public void testGetReturnType() {
        IMethod toString = javaLangObject.getMethod("toString", new String[0]);
        assertNotNull(toString);
        assertEquals("java.lang.String", jmh.getReturnType(toString));
    }
    
    public void testComputeTypeOrder() {
        IType hashSet = jmh.resolveType("java.util.HashSet");
        IType types[] = jmh.computeTypeOrder(hashSet, new NullProgressMonitor());
        assertEquals(8, types.length);  // At least, it's 8 in JDK 5
        assertEquals("java.util.HashSet", types[0].getFullyQualifiedName());
        assertEquals("java.util.AbstractSet", types[4].getFullyQualifiedName());
        assertEquals("java.util.AbstractCollection", types[5].getFullyQualifiedName());
        assertEquals("java.util.Collection", types[6].getFullyQualifiedName());
        assertEquals("java.lang.Object", types[7].getFullyQualifiedName());
    }

    public void testGetConstructors() {
    	// Note: this requires that plugin org.eclipse.ui.ide be added to the search path
        IType fei = JavaModelHelper.getDefault().resolveType("org.eclipse.ui.part.FileEditorInput");
        assertNotNull(fei);
        Set<IMember> crefs = jmh.getConstructorReferences(fei, new NullProgressMonitor());
        assertTrue(crefs != null && !crefs.isEmpty());
        for(IMember ref : crefs) {
            try {
                /* OpenFileAction is a specific known instance where the Java SearchEngine fails and
                 * provides a supposed match which is incorrect (see bug 109346) */
                if(ref instanceof IMethod && ref.getElementName().equals("OpenFileAction")
                        && ((IMethod)ref).isConstructor()) {
                    fail("Constructor references includes super refs");
                }
            } catch (JavaModelException e) {
                fail("Unexpected JavaModelException: " + e);
            }
        }
    }
    
    public void testResolveEnclosedTypes() {
    	IType testFoo = jmh.resolveType("test.Test$Foo");
        assertEquals("test.Test$Foo", testFoo.getFullyQualifiedName());
    }
    
    public void testUsedFields() {
        IType jls = jmh.resolveType("java.lang.String");
        IMethod io = jls.getMethod("indexOf", new String[] { "I", "I" });
        assertNotNull(io);
        IField usedFields[] = jmh.getUsedFields(io, new NullProgressMonitor());
        assertEquals(5, usedFields.length);

        io = jls.getMethod("startsWith", new String[] { "Ljava.lang.String;" });
        assertNotNull(io);
        usedFields = jmh.getUsedFields(io, new NullProgressMonitor());
        assertEquals(0, usedFields.length);
    }
    
    public void testSentMethods() {
        IType jls = jmh.resolveType("java.lang.String");
        IMethod io = jls.getMethod("indexOf", new String[] { "I", "I" });
        assertNotNull(io);
        Collection<IMethod> sentMethods = jmh.getMethodsSent(io, new NullProgressMonitor());
        assertEquals(1, sentMethods.size());

        io = jls.getMethod("toString", new String[0]);
        assertNotNull(io);
        sentMethods = jmh.getMethodsSent(io, new NullProgressMonitor());
        assertEquals(0, sentMethods.size());
    }

    public void testReferencedTypes() {
        IType jls = jmh.resolveType("java.lang.String");
        IMethod io = jls.getMethod("indexOf", new String[] { "I", "I" });
        assertNotNull(io);
        Collection<IType> referencedTypes = jmh.getReferencedTypes(io, new NullProgressMonitor());
        assertEquals(1, referencedTypes.size());

        io = jls.getMethod("toString", new String[0]);
        assertNotNull(io);
        referencedTypes = jmh.getReferencedTypes(io, new NullProgressMonitor());
        assertEquals(1, referencedTypes.size());
        assertEquals("String", referencedTypes.iterator().next().getTypeQualifiedName());
    }

    public void testIsThrowable() {
        assertTrue(jmh.isThrowable(jmh.resolveType("java.lang.Exception"), new NullProgressMonitor()));
        assertTrue(jmh.isThrowable(jmh.resolveType("java.lang.Error"), new NullProgressMonitor()));
        assertTrue(jmh.isThrowable(jmh.resolveType("java.lang.Throwable"), new NullProgressMonitor()));
        assertFalse(jmh.isThrowable(jmh.resolveType("java.lang.String"), new NullProgressMonitor()));
    }

    public void testIsThrowableStatement() {
//        ASTParser parser = ASTParser.newParser(AST.JLS3);
//        parser.setSource("class Foo { void main() { throw new Exception(); }}".toCharArray());
        IType fis = jmh.resolveType("java.io.FileInputStream");
        IType iox = jmh.resolveType("java.io.IOException");
        IMethod m = fis.getMethod("getFD", new String[0]);
        assertNotNull(m);
        assertNotNull(iox);
        assertNotNull(fis);
        
        Set<IMember> members = new HashSet<IMember>();
        members.add(m);
        Set<IType> throwableTypes = new HashSet<IType>();
        throwableTypes.add(iox);
        Set<String> throwableTypeNames = new HashSet<String>();
        throwableTypeNames.add(iox.getFullyQualifiedName());
        Set<IMember> results = new HashSet<IMember>();
        
        jmh.processStatements(m.getClassFile(), members, throwableTypes, throwableTypeNames,
        		new ThrowsStatementFinder(), results, new NullProgressMonitor());
        assertFalse(results.isEmpty());
//        IType iox = jmh.resolveType("java.io.IOException");
//        assertNotNull(iox);
//        assertTrue(jmh.isThrowable(iox));
//        Set references = jmh.getThrowLocations(iox, new NullProgressMonitor());
//        assertFalse(references.isEmpty());
    }
    
    public void testBackgroundingCommonElements() {
    	assertNotNull(javaLangObject);
    	assertTrue(FerretPlugin.isCommonElement(javaLangObject));
    	IType testFoo = jmh.resolveType("test.Test$Foo");
    	assertNotNull(testFoo);
    	assertFalse(FerretPlugin.isCommonElement(testFoo));
    }
    
    public void testJdtIsFieldPredicateRelationOnNonField() {
		Object o = new Object();
		ISphere tb = new Sphere("testJdtIsFieldPredicateRelationOnNonField()");
		IRelationFactory opf = new JdtIsFieldRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), o);
		assertNull(rel);
    }
    
    public void testJdtIsFieldPredicateOnField() {
        IType jls = jmh.resolveType("java.lang.String");
        IMethod io = jls.getMethod("indexOf", new String[] { "I", "I" });
        assertNotNull(io);
        IField usedFields[] = jmh.getUsedFields(io, new NullProgressMonitor());
        assertEquals(5, usedFields.length);
	
		ISphere tb = new Sphere("testJdtIsFieldPredicateOnField()");
		IRelationFactory opf = new JdtIsFieldRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), usedFields[0]);
		assertNotNull(rel);
		assertTrue(rel.hasNext());
		assertNotNull(rel.next());
    }

    public void testJdtIsClassPredicateRelationOnNonClass() {
		Object o = new Object();
		ISphere tb = new Sphere("testJdtIsClassPredicateRelationOnNonClass()");
		IRelationFactory opf = new JdtIsClassRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), o);
		assertNull(rel);
    }
    
    public void testJdtIsClassPredicateOnClass() {
        IType jls = jmh.resolveType("java.lang.String");
        assertNotNull(jls);
	
		ISphere tb = new Sphere("testJdtIsFieldPredicateRelation()");
		IRelationFactory opf = new JdtIsClassRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), jls);
		assertNotNull(rel);
		assertTrue(rel.hasNext());
		assertNotNull(rel.next());
    }

    
    public void testJdtIsInterfacePredicateRelationOnNonInterface() {
		Object o = new Object();
		ISphere tb = new Sphere("testJdtIsInterfacePredicateRelationOnNonInterface()");
		IRelationFactory opf = new JdtIsInterfaceRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), o);
		assertNull(rel);
    }
    
    public void testJdtIsInterfacePredicateOnInterface() {
        IType jlc = jmh.resolveType("java.lang.Cloneable");
        assertNotNull(jlc);
        IType jls = jmh.resolveType("java.lang.String");
        assertNotNull(jls);	
		ISphere tb = new Sphere("testJdtIsInterfacePredicateOnInterface()");
		IRelationFactory opf = new JdtIsInterfaceRelation();
		
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), jlc);
		assertTrue(rel.hasNext());
		assertNotNull(rel.next());

		rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), jls);
		assertNotNull(rel);
		assertFalse(rel.hasNext());
    }
    
    public void testJMHResolvedMethodSignature() {
    	IType testFoo = jmh.resolveType("test.Test");
    	IMethod ssr = testFoo.getMethod("setSourceRange", new String[] {"QStringBuilder;", "I" });
        assertNotNull(ssr);
        assertTrue(ssr.exists());
        try {
        	assertEquals("(Ljava.lang.StringBuilder;I)Ljava.lang.String;",
        			JavaModelHelper.getDefault().resolvedMethodSignature(ssr));
        } catch(JavaModelException e) {
        	fail("JME: " + e);
        }
    }

}
