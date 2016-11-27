/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.clustering.ClusteringPlugin;
import ca.ubc.cs.clustering.attrs.IAttributeSource;
import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.jdt.attributes.MethodThrowsProvider;

public class JdtAttributeTests {

    IType javaLangString;

    @BeforeClass
    public static void createWorkspace() throws Exception {
        JdtTests.createWorkspace();
    }
    
    @Before
    public void setUp() {
    	javaLangString = JavaModelHelper.getDefault().resolveType("java.lang.String"); 
    	assertNotNull(javaLangString);
    }

    @Test
    public void testTypeAttributes() {
        IAttributeSource ts = ClusteringPlugin.getDefault().getAttributeSourceManager().getAttributeSource(javaLangString);
        assertFalse(ts.getAttributeNames().size() == 0);
        for(String name : ts.getAttributeNames()) {
            assertTrue(ts.getAttributeDomain(name) == null || ts.getAttributeDomain(name).size() > 0);
            assertTrue(ts.getAttribute(name, javaLangString) != null 
            		|| name.equals("ca.ubc.cs.ferret.jdt.classifier.MemberDeclaringType"));
        }
    }

    @Test
    public void testMethodAttributes() {
        IAttributeSource ts;
        try {
        	Object obj = javaLangString.getMethods()[0];
            ts = ClusteringPlugin.getDefault().getAttributeSourceManager().getAttributeSource(obj);
            assertFalse(ts.getAttributeNames().size()== 0);
            for(String name : ts.getAttributeNames()) {
                assertTrue(ts.getAttributeDomain(name) == null || ts.getAttributeDomain(name).size() > 0);
                assertNotNull(ts.getAttribute(name, obj));
            }
        } catch (JavaModelException e) {
            fail("Unexpected JavaModelException: " + e);
        }
    }
    
    @Test
    public void testFieldAttributes() {
        IAttributeSource ts;
        try {
        	Object obj = javaLangString.getFields()[0];
            ts = ClusteringPlugin.getDefault().getAttributeSourceManager().getAttributeSource(obj);
            assertFalse(ts.getAttributeNames().size() == 0);
            for(String name : ts.getAttributeNames()) {
                assertTrue(ts.getAttributeDomain(name) == null || ts.getAttributeDomain(name).size() > 0);
                assertNotNull(ts.getAttribute(name, obj));
            }
        } catch (JavaModelException e) {
            fail("Unexpected JavaModelException");
        }
    }

    @Test
    public void testClassFileAttributes() {
    	Object obj = javaLangString.getClassFile();
        IAttributeSource ts = ClusteringPlugin.getDefault().getAttributeSourceManager().getAttributeSource(obj);
        assertFalse(ts.getAttributeNames().size() == 0);
        for(String name : ts.getAttributeNames()) {
            assertTrue(ts.getAttributeDomain(name) == null || ts.getAttributeDomain(name).size() > 0);
            assertNotNull(ts.getAttribute(name, obj));
        }
    }

    @Test
    public void testMethodThrowsAttributeProvider() {
    	try {
    		IMethod throwsMethod = null;
    		for(IMethod m : javaLangString.getMethods()) {
    			if(m.getExceptionTypes().length > 0) {
    				throwsMethod = m;
    				break;
    			}
    		}
    		assertNotNull(throwsMethod);
    		assertTrue(throwsMethod.exists());
    		IClassifier<IMethod,Object> av = new MethodThrowsProvider();
    		Object value = av.getCategory(throwsMethod);
    		assertNotNull(value);
    		assertTrue(value instanceof Collection);
			assertTrue(((Collection<?>)value).size() == 1);
			Object ex = ((Collection<?>)value).iterator().next();
    		assertTrue(ex instanceof IType);
    		assertEquals("java.io.UnsupportedEncodingException", 
    				((IType)ex).getFullyQualifiedName());
    	} catch(JavaModelException e) {
    		fail("JavaModelException: " + e);
    	}
    }

    @After
    public void tearDown() {
        javaLangString = null;
    }
    
    @AfterClass
    public static void destroyWorkspace() throws Exception {
        JdtTests.destroyWorkspace();
    }
    
    
}
