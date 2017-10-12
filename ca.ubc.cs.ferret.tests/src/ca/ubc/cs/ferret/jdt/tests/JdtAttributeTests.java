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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ca.ubc.cs.clustering.ClusteringPlugin;
import ca.ubc.cs.clustering.attrs.IAttributeSource;
import ca.ubc.cs.clustering.attrs.IClassifier;
import ca.ubc.cs.ferret.jdt.JavaModelHelper;
import ca.ubc.cs.ferret.jdt.attributes.MemberClassVsInterfaceProvider;
import ca.ubc.cs.ferret.jdt.attributes.MethodThrowsProvider;
import java.util.Collection;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JdtAttributeTests {

    IType javaLangString;
    IType javaUtilCollection;
	private IType javaLangDeprecated;
	private IType javaLangManagementMemoryType;

    @BeforeClass
    public static void createWorkspace() throws Exception {
        JdtTests.createWorkspace();
    }
    
    @Before
    public void setUp() {
    	javaLangString = JavaModelHelper.getDefault().resolveType("java.lang.String"); 
    	javaUtilCollection = JavaModelHelper.getDefault().resolveType("java.util.Collection");
    	javaLangDeprecated = JavaModelHelper.getDefault().resolveType("java.lang.Deprecated");	// annotation
    	javaLangManagementMemoryType = JavaModelHelper.getDefault().resolveType("java.lang.management.MemoryType"); // enum
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

    @Test
    public void testClassVsInterface() throws JavaModelException {
    		MemberClassVsInterfaceProvider provider = new MemberClassVsInterfaceProvider();
    		assertArrayEquals(new String[] { "class", "interface", "enum", "annotation", "lambda" }, provider.getCategories());
    		assertEquals("class", provider.getCategory(javaLangString));
    		assertEquals("class", provider.getCategory(javaLangString.getMethods()[0]));
		assertEquals("interface", provider.getCategory(javaUtilCollection));
		assertEquals("enum", provider.getCategory(javaLangManagementMemoryType));
		assertEquals("annotation", provider.getCategory(javaLangDeprecated));
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
