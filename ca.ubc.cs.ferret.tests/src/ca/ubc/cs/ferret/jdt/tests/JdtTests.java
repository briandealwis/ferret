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
package ca.ubc.cs.ferret.jdt.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class JdtTests {
    protected static TestProject testProject;
    public JavaModelHelper jmh;
    public IType javaLangObject;
    
    @BeforeClass
    public static void createWorkspace() throws CoreException, InterruptedException, InvocationTargetException {
    	testProject = new TestProject("test-project");
        IPackageFragment testPkg = testProject.createPackage("test");
        IType testType = testProject.createType(testPkg, "Test.java", "public class Test {public class Foo {} }" );
        testType.createMethod("public final void setSourceRange(int startPosition, int length) { }",null, false, null);
        testType.createMethod("public String setSourceRange(StringBuilder source, int length) { }",null, false, null);
        testProject.getJavaProject().open(new NullProgressMonitor());
    }
    
    @AfterClass
    public static void destroyWorkspace() {
        try {
//        	ResourcesPlugin.getWorkspace().getRoot().delete(true, true, new NullProgressMonitor());
            testProject.getProject().delete(true, new NullProgressMonitor());
		} catch (CoreException e) {
			fail("Could not remove test project: " + e);
		}
    }
    
    @Before
    public void setUp() throws Exception {
        jmh = JavaModelHelper.getDefault();
        testResolveType();  // populate javaLangObject
    }
    
    @After
    public void tearDown() {
        jmh = null;
    }
    
    @Test
    public void testResolveType() {
        javaLangObject = jmh.resolveType("java.lang.Object"); 
        assertNotNull(javaLangObject);
        assertEquals("java.lang.Object", javaLangObject.getFullyQualifiedName());
    }
    
    @Test
    public void testIsImmediateSubclass() {
        IType jls = jmh.resolveType("java.lang.String");
        
        try {
            assertTrue(jmh.isImmediateSubtype(jls, javaLangObject));
            assertTrue(jmh.isSubtype(jls, javaLangObject));
        } catch (JavaModelException e) {
            fail("JavaModelException");
        }
    }
    
    @Test
    public void testGetSupertypes() {
        IType jls = jmh.resolveType("java.lang.String");
        assertEquals(0, jmh.getSupertypes(javaLangObject, new NullProgressMonitor()).length);
        assertFalse(0 == jmh.getSupertypes(jls, new NullProgressMonitor()).length);
    }
    
    @Test
    public void testGetReturnType() {
        IMethod toString = javaLangObject.getMethod("toString", new String[0]);
        assertNotNull(toString);
        assertEquals("java.lang.String", jmh.getReturnType(toString));
    }
    
    @Test
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

    @Ignore("Need to find better example of constructors")
    @Test
    public void testGetConstructors() {
    	// Note: this requires that plugin org.eclipse.ui.ide be added to the search path
        IType fei = jmh.resolveType("org.eclipse.ui.part.FileEditorInput");
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
    
    @Test
    public void testResolveEnclosedTypes() {
    	IType testFoo = jmh.resolveType("test.Test$Foo");
        assertEquals("test.Test$Foo", testFoo.getFullyQualifiedName());
    }
    
    @Test
    public void testUsedFields() {
        IType jls = jmh.resolveType("java.lang.String");
        IMethod io = jls.getMethod("indexOf", new String[] { "I", "I" });
        assertNotNull(io);
        IField usedFields[] = jmh.getUsedFields(io, new NullProgressMonitor());
        assertTrue(usedFields.length > 0);

        io = jls.getMethod("startsWith", new String[] { "Ljava.lang.String;" });
        assertNotNull(io);
        usedFields = jmh.getUsedFields(io, new NullProgressMonitor());
        assertEquals(0, usedFields.length);
    }
    
    @Test
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

    @Test
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

    @Test
    public void testIsThrowable() {
        assertTrue(jmh.isThrowable(jmh.resolveType("java.lang.Exception"), new NullProgressMonitor()));
        assertTrue(jmh.isThrowable(jmh.resolveType("java.lang.Error"), new NullProgressMonitor()));
        assertTrue(jmh.isThrowable(jmh.resolveType("java.lang.Throwable"), new NullProgressMonitor()));
        assertFalse(jmh.isThrowable(jmh.resolveType("java.lang.String"), new NullProgressMonitor()));
    }

    @Test
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
    
    @Test
    public void testBackgroundingCommonElements() {
    	assertNotNull(javaLangObject);
    	assertTrue(FerretPlugin.isCommonElement(javaLangObject));
    	IType testFoo = jmh.resolveType("test.Test$Foo");
    	assertNotNull(testFoo);
    	assertFalse(FerretPlugin.isCommonElement(testFoo));
    }
    
    @Test
    public void testJdtIsFieldPredicateRelationOnNonField() {
		Object o = new Object();
		ISphere tb = new Sphere("testJdtIsFieldPredicateRelationOnNonField()");
		IRelationFactory opf = new JdtIsFieldRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), o);
		assertNull(rel);
    }
    
    @Test
    public void testJdtIsFieldPredicateOnField() {
        IType jls = jmh.resolveType("java.lang.String");
        IMethod io = jls.getMethod("indexOf", new String[] { "I", "I" });
        assertNotNull(io);
        IField usedFields[] = jmh.getUsedFields(io, new NullProgressMonitor());
        assertTrue(usedFields.length > 0);
	
		ISphere tb = new Sphere("testJdtIsFieldPredicateOnField()");
		IRelationFactory opf = new JdtIsFieldRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), usedFields[0]);
		assertNotNull(rel);
		assertTrue(rel.hasNext());
		assertNotNull(rel.next());
    }

    @Test
    public void testJdtIsClassPredicateRelationOnNonClass() {
		Object o = new Object();
		ISphere tb = new Sphere("testJdtIsClassPredicateRelationOnNonClass()");
		IRelationFactory opf = new JdtIsClassRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), o);
		assertNull(rel);
    }
    
    @Test
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

    
    @Test
    public void testJdtIsInterfacePredicateRelationOnNonInterface() {
		Object o = new Object();
		ISphere tb = new Sphere("testJdtIsInterfacePredicateRelationOnNonInterface()");
		IRelationFactory opf = new JdtIsInterfaceRelation();
		IRelation rel = opf.configure(new NullProgressMonitor(), tb.createResolverState(null), o);
		assertNull(rel);
    }
    
    @Test
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
    
    @Test
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

	@Test
	public void testResolvePackage() {
		assertNotNull(jmh.resolvePackage("test", testProject.getJavaProject()));
		assertNull(jmh.resolvePackage("does.not.exist", testProject.getJavaProject()));
	}
}
