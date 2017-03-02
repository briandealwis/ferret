/*
 * Copyright 2004  University of British Columbia
 * @author bsd
 */
package ca.ubc.cs.ferret.jdt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import ca.ubc.cs.ferret.EclipseFuture;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ExtendibleSourceRange;
import ca.ubc.cs.ferret.model.IExtendibleSourceRange;

/**
 * Provide helper functions for reasoning using the java model. 
 * @author bsd
 */
public class JavaModelHelper implements IElementChangedListener {
    
    private static final String KEY_METHODS_OVERRIDDEN = "methods-overridden";
	private static final String KEY_METHOD_OVERRIDERS = "method-overriders";
	private static final String KEY_METHOD_RETURNING = "methodReturning";
	private static final String KEY_ALTERNATIVE_METHOD_IMPLEMENTATIONS = "alternativeMethodImplementations";
	private static final String KEY_TYPE_ORDER = "typeOrder";
	private static final String KEY_FIELD_SETTERS = "field-setters";
	private static final String KEY_FIELD_READERS = "field-readers";
	private static final String KEY_METHOD_INSTANTIATING = "methodInstantiating";
	private static final String KEY_CONSTRUCTOR_REFERENCES = "constructorReferences";
	private static final String KEY_CAST = "cast";
	private static final String KEY_THROWS = "throws";
	private static final String KEY_CATCH = "catch";
	private static final String KEY_INSTANCEOF = "instanceof";
	private static final String KEY_TYPE_REFERENCES = "typeReferences";
	private static final String KEY_METHOD_REFERENCES = "methodReferences";
	private static final String KEY_SUPERTYPE_HIERARCHY = "supertypeHierarchy";
	protected static final String KEY_TYPE_HIERARCHY = "typeHierarchy";
	private static JavaModelHelper singleton;

    private JavaModelHelper() {
        /* do nothing */
    }

    public static JavaModelHelper getDefault() {
    	if(singleton == null) {
    		singleton = new JavaModelHelper();
    		singleton.start();
    	}
        return singleton;
    }
    
    public void start() {
        reset();
        JavaCore.addElementChangedListener(this, ElementChangedEvent.POST_CHANGE);
    }

    public void stop() {
        JavaCore.removeElementChangedListener(this);
    }

    protected Cache<ResultKey,EclipseFuture<Object>> modelSearchCache;
    protected Cache<String,EclipseFuture<IType>> typeCache;
    protected int numberQueries = 0;
    protected int numberSatisfiedFromCache = 0;
    protected int javaModelCounter = 0;
    
    public void reset() {
    	if(FerretPlugin.hasDebugOption("debug/cacheMaintenance")) {
    		System.out.println("JavaModelHelper: resetting caches");
    		if(modelSearchCache != null) {
    			System.out.println(summarize());
    		}
    	}
    	if(modelSearchCache == null) {
    		modelSearchCache = newFutureMap(getCacheSize());
    	} else {
    	    	if(FerretPlugin.hasDebugOption("debug/cacheMaintenance")) {
    	    		System.out.println("JavaModelHelper: reset(): cancelling futures...");
    	    	}
    		synchronized(modelSearchCache) {
        		modelSearchCache.invalidateAll();
    		}
    	}
        if(typeCache == null) {
        		typeCache = newFutureMap(getCacheSize());
        } else {
			synchronized(typeCache) {
				if(FerretPlugin.hasDebugOption("debug/cacheMaintenance")) {
					System.out.println("JavaModelHelper: reset(): cancelling futures...");
				}
				typeCache.invalidateAll();
        	}
        }
        numberQueries = numberSatisfiedFromCache = 0;
    }
    
    private static <K, V> Cache<K, EclipseFuture<V>> newFutureMap(int size) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		CacheBuilder<K, EclipseFuture<V>> builder = (CacheBuilder)CacheBuilder.newBuilder();
		
		Cache<K, EclipseFuture<V>> cache = builder.initialCapacity(size).removalListener(notif -> {
			if (!notif.getValue().isDone()) {
				notif.getValue().cancel(true);
			}
		}).build();
		return cache;
	}

	protected int getCacheSize() {
		return Math.max(250, FerretPlugin.getMaximumBackgroundCount() * 3);
	}

	public String summarize() {
        StringBuffer report = new StringBuffer();
        int totalElementCount = 0;
        int totalJEs = 0;
        int totalJEsNonExisting = 0;
        report.append("JavaModelHelper usage summary\n");
        report.append("  type-cache: " + typeCache.size() + " types cached\n");
        report.append("  search cache: " + modelSearchCache.size() + " cached searches\n");
        Multiset<String> keyTypes = HashMultiset.create();
        for(ResultKey k : modelSearchCache.asMap().keySet()) {
        	keyTypes.add(k.resultType);
        }
        report.append("    search types: ");
        for(String k : keyTypes.elementSet()) {
        	report.append(k + "[" + keyTypes.count(k) + "] ");
        }
        report.append("\n");
        Multiset<Class<?>> nonJETypes = HashMultiset.create();
        for(Object o : modelSearchCache.asMap().values()) {
            if(o instanceof Collection) {
                totalElementCount += ((Collection<?>)o).size();
                for(Object je : (Collection<?>)o) {
                    if(je instanceof IJavaElement) {
                        totalJEs++;
                        if(!((IJavaElement)je).exists()) {
                            totalJEsNonExisting++;
                        }
                    } else {
                    	nonJETypes.add(je.getClass());
                    }
                }
            } else {
                totalElementCount++;
                if(o instanceof IJavaElement) {
                    totalJEs++;
                    if(!((IJavaElement)o).exists()) {
                        totalJEsNonExisting++;
                    }
                } else {
                	nonJETypes.add(o.getClass());
                }
            }
        }
        report.append("     " + totalElementCount + " # cached elements\n");
        report.append("     => " + totalJEs + " are JavaElements, of which " + 
                totalJEsNonExisting + " do not exist\n");
        report.append("         non-JE types: ");
        for(Class<?> c : nonJETypes.elementSet()) {
        	report.append(c.getName());
        	report.append("[");
        	report.append(nonJETypes.count(c));
        	report.append("] ");
        }
        report.append("\n");
        report.append("    " + numberSatisfiedFromCache + " out of " + numberQueries +
                " queries issued satisfied from cache (" +
                (numberQueries > 0 ? numberSatisfiedFromCache * 100 / numberQueries : 0) + "%)\n");
        return report.toString();
    }
    
    public int getJavaModelCounter() {
    	return javaModelCounter;
    }
  
    private <K,V> V resolveOperation(Cache<K, EclipseFuture<V>> cache, K key, Callable<V> creator) {
		Thread.yield();	// try to make UI more responsive
		EclipseFuture<V> future;
        numberQueries++;
        boolean found;
		synchronized(cache) {
			future = cache.getIfPresent(key);
			found = future != null;
			if(future == null || future.isCancelled()) {
				cache.put(key, future = new EclipseFuture<V>(key));
				found = false;
				logCache("no future found for " + key + "; creating " + future);
			}
		}
		if(!found) {
    		return performCreation(future, creator);			
		} else {
			numberSatisfiedFromCache++;
	        try {
	        	if(!future.isDone()) {
					logCache("waiting on future: " + future.toString());
		        	V result = future.get(60, TimeUnit.SECONDS);
		        	assert future.isDone();
		        	logCache("future " + future + " resulted in " + FerretPlugin.prettyPrint(result));
		        	return result;
	        	}
				numberSatisfiedFromCache++;
	        	return future.get(60, TimeUnit.SECONDS);
	        } catch(TimeoutException e) {
	        	FerretPlugin.log(new Status(IStatus.ERROR, FerretJdtPlugin.pluginID, FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
	        			getClass().getSimpleName() + ": future timed out (will be cancelled): " + future.toString(), null));
	        	future.cancel(true);	// cancel the future and try again
	        	return resolveOperation(cache, key, creator);
	        } catch(CancellationException e) {
	        	logCache("future was cancelled: " + future.toString());
	        	return resolveOperation(cache, key, creator);	// try again
	        }
		}
    }
    
    private <K,V> void storeInCache(Cache<K, EclipseFuture<V>> cache, K key, V value) {
		synchronized(cache) {
			EclipseFuture<V> future = cache.getIfPresent(key);
			if(future == null || future.isCancelled()) {
				cache.put(key, future = new EclipseFuture<V>(key));
				future.set(value);
			}
		}
    }

    @SuppressWarnings("unchecked")
	private <V> V resolveCache(ResultKey key, Callable<V> creator) {
    	Object result = resolveOperation(modelSearchCache, key, (Callable<Object>)creator);
    	return (V)result; 
    }

    private <V> void storeInCache(ResultKey key, V value) {
    	storeInCache(modelSearchCache, key, (Object)value);
    }

    private void logCache(String message) {
    	if(FerretPlugin.hasDebugOption("debug/cacheMaintenance")) {
    		System.out.println("JMH[" + Thread.currentThread() + "]: " + message);
    	}
	}

	private <K,V> V performCreation(EclipseFuture<V> future, Callable<V> creator) {
		Thread.yield();	// try to make UI more responsive
		try {
			V result = creator.call();
			logCache("Created and set value for " + future.toString());
			future.set(result);
			return result;
		} catch(Throwable t) {			
			logCache("Exception while creating value for " + future.toString() + ": " + t.getMessage());
			future.cancel(true);
			if(t instanceof Error){
				throw (Error)t;
			} else if(t instanceof RuntimeException) {
				throw (RuntimeException)t;
			} else {
				return null;
			}
		}
    }

    /* On JDT change, we wipe out our caches */
    public void elementChanged(ElementChangedEvent event) {
    	// System.out.println("JDT elementChanged: " + event);
    	if(contentChanged(event.getDelta())) {
    		if(FerretPlugin.hasDebugOption("debug/cacheMaintenance")) {
    			System.out.println("JDT Element Changed: Resetting JDT cache");
    		}
    		reset();
    		javaModelCounter++;
    	}
    }

    protected boolean contentChanged(IJavaElementDelta delta) {   
    	if((delta.getFlags() & (IJavaElementDelta.F_REORDER | IJavaElementDelta.F_SOURCEATTACHED 
    			| IJavaElementDelta.F_SOURCEDETACHED)) == 0) {
    		return true;
    	}
    	for(IJavaElementDelta child : delta.getAffectedChildren()) {
    		if(contentChanged(child)) { return true; }
    	}
    	return false;
    }
    
    protected ASTParser parser = ASTParser.newParser(AST.JLS3);
    
    protected synchronized ASTNode getAST(IJavaElement container, int offset, int length,
            IProgressMonitor monitor)  {
        parser.setSourceRange(offset, length);
        if(container instanceof ICompilationUnit) {
            parser.setSource((ICompilationUnit)container);
        } else if(container instanceof IClassFile) {
            parser.setSource((IClassFile)container);
        }
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);

        try {
//            System.out.println("Getting AST for " + FerretPlugin.prettyPrint(container));
//            StopWatch watch = new StopWatch();
//            watch.start();
            ASTNode node = parser.createAST(monitor);
//            watch.stop();
//            System.out.println("   total=" + watch.toString());
            return node;
        } catch(IllegalStateException e) {
            FerretPlugin.log(new Status(IStatus.ERROR, FerretJdtPlugin.pluginID,
                    FerretErrorConstants.EXCEPTION_HANDLED,
                    "unable to generate AST for " + container, e));
            return null;
        }
    }

    public static void logJME(Exception e) {
    	if(FerretJdtPlugin.logJavaModelExceptions()) {
			FerretPlugin.logHandledException(e);
    	}
    }
    
    protected void performSearch(SearchPattern pattern, IJavaSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) {
        if(pattern == null) {
			// FerretPlugin.log(new Status(IStatus.WARNING,
			// FerretJdtPlugin.pluginID, 0,
			// "JavaModelHelper search provided null pattern", null));
            return;
        }
    	SearchEngine searchEngine = new SearchEngine();
    	try {
    		searchEngine.search(pattern,
    				new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
    				scope, requestor,  monitor);
    	} catch (CoreException e) {
            FerretPlugin.log(new Status(IStatus.ERROR, FerretPlugin.pluginID, 12,
                    "Unexpected CoreException while performing Java searches", e));
        } catch(NullPointerException e) {
        	handleJDTNPE(e);
        }
    }

    /* Type relations */
    
    public Collection<IType> getAllSubclasses(IType cl, IProgressMonitor monitor) {
        List<IType> list = new ArrayList<IType>();
        for(IType subtype : getAllSubtypes(cl, monitor)) {
            try {
                if(subtype.isClass())  { list.add(subtype); }
            } catch(JavaModelException e) { logJME(e); }
        }
        return list;
    }

    public IType[] getSubclasses(IType cl, IProgressMonitor monitor) {
        ITypeHierarchy subtypesHierarchy = getSubTypeHierarchyFor(cl, monitor);
        if(subtypesHierarchy == null) { return new IType[0]; }
        return subtypesHierarchy.getSubclasses(cl);
    }
    

    public IType[] getSubtypes(IType t, IProgressMonitor monitor) {
        ITypeHierarchy subtypesHierarchy = getSubTypeHierarchyFor(t, monitor);
        if(subtypesHierarchy == null) { return new IType[0]; }
        return subtypesHierarchy.getSubtypes(t);
    }

    public IType[] getAllSubtypes(IType t, IProgressMonitor monitor) {
        ITypeHierarchy subtypesHierarchy = getSubTypeHierarchyFor(t, monitor);
        if(subtypesHierarchy == null) { return new IType[0]; }
        return subtypesHierarchy.getAllSubtypes(t);
    }
    
	public IType[] getAllSupertypes(IType t, IProgressMonitor monitor) {
        ITypeHierarchy supertypesHierarchy = getSuperTypeHierarchyFor(t, monitor);
        if(supertypesHierarchy == null) { return new IType[0]; }
        return supertypesHierarchy.getAllSupertypes(t);
	}

    public IType[] getSubinterfaces(IType iface, IProgressMonitor monitor) {
        List<IType> list = new LinkedList<IType>();
        IType subtypes[] = getSubtypes(iface, monitor);
        for(int i = 0; i < subtypes.length; i++) {
            try {
                if(subtypes[i].isInterface()) { list.add(subtypes[i]); }
            } catch(JavaModelException e) {
                logJME(e);
            }
        }
        return list.toArray(new IType[list.size()]);
    }


    /**
     * Determine the supertypes of <code>type</code>. List is in partial order.
     * Note that interfaces do not have java.lang.Object as a supertype
     * @return  supertypes of <code>type</code>
     * @author  bsd
     */
    public IType[] getSupertypes(IType type, IProgressMonitor monitor) {
    	if(type.getFullyQualifiedName().equals("java.lang.Object")) { return new IType[0]; }
        ITypeHierarchy typeHierarchy = getSuperTypeHierarchyFor(type, monitor);
        if(typeHierarchy == null) {
        	FerretPlugin.log(new Status(IStatus.WARNING, FerretJdtPlugin.pluginID, FerretErrorConstants.UNEXPECTED_RUNTIME_OCCURRENCE,
        			"JMH.getSuperTypeHierarchyFor(" + FerretPlugin.prettyPrint(type) + ") returned null!?", null));
        	return new IType[0];
        }
        return typeHierarchy.getSupertypes(type);
    }

    /**
     * Determine the superclass of <code>type</code>. List is in partial order.
     * @return  supertypes of type
     * @author  bsd
     */
    public IType getSuperclass(IType type, IProgressMonitor monitor) {
		if(monitor.isCanceled()) { throw new OperationCanceledException(); }
//		Getting the type hierarchy is too slow:
//        ITypeHierarchy typeHierarchy = getSuperTypeHierarchyFor(type, monitor);
//        return typeHierarchy.getSuperclass(type);
    	try {
    		if(type.getSuperclassName() == null) { 
    			return null;
			}
			return resolveType(type.getSuperclassName(), type);
		} catch (JavaModelException e) {
			logJME(e);
			return null;
		}
    }

    /**
     * Determine the superclasses of <code>type</code>. List is in partial order.
     * @return  supertypes of type
     * @author  bsd
     */
    public IType[] getAllSuperclasses(IType type, IProgressMonitor monitor) {
        ITypeHierarchy typeHierarchy = getSuperTypeHierarchyFor(type, monitor);
        return typeHierarchy.getAllSuperclasses(type);
    }
    
    /**
     * Determine the superinterfaces of <code>type</code>. List is in partial order.
     * @return  supertypes of type, but without java.lang.Object
     * @author  bsd
     */
    public IType[] getAllSuperinterfaces(IType type, IProgressMonitor monitor) {
        ITypeHierarchy typeHierarchy = getSuperTypeHierarchyFor(type, monitor);
        return typeHierarchy.getAllSuperInterfaces(type);
    }
        
    /**
     * Return true if <code>cl</code> is an immediate subtype of <code>potentialSuper</code>.
     * @param cl
     * @param potentialSuper
     * @return
     * @throws JavaModelException
     */
    public boolean isImmediateSubtype(IType cl, IType potentialSuper) throws JavaModelException {
    	String scts = cl.getSuperclassTypeSignature();
    	String fqdn = null;
    	if(scts == null) {
    		// Then superclass is java.lang.Object
    		fqdn = "java.lang.Object";
    	} else if(Signature.getSignatureQualifier(scts).length() == 0) {
    		fqdn = resolveTypeName(Signature.getSignatureSimpleName(scts), cl);
    	} else {
    		fqdn = Signature.getSignatureQualifier(scts) + "."
					+ Signature.getSignatureSimpleName(scts);
    	}
        return fqdn.equals(potentialSuper.getFullyQualifiedName());
    }


    /**
     * Return true if <code>cl</code> is a subtype of <code>potentialSuper</code>.
     * @param cl
     * @param potentialSuper
     * @return true if cl is a subtype of potentialSuper
     * @throws JavaModelException
     */
	public boolean isSubtype(IType cl, IType potentialSuper) throws JavaModelException {
		return isSubtype(cl, potentialSuper, getSuperTypeHierarchyFor(cl, new NullProgressMonitor()));
	}

	public boolean isSubtype(IType cl, IType potentialSuper, ITypeHierarchy superTypeHierarchy) {
        IType supers[] = superTypeHierarchy.getSupertypes(cl);
        for (int i = 0; i < supers.length; i++) {
            if(potentialSuper.equals(supers[i])) {
                return true;
            }
        }
        return false;
	}

	public boolean referencesType(String sig, IType type, IMember container) throws JavaModelException {
		return referencesType(sig, type, type.getElementName(),
				type.getFullyQualifiedName(), container);
	}
		
	public boolean referencesType(String sig, IType type, String typeSimpleName,
			String typeFQName, IMember container) throws JavaModelException {
		if(Signature.getTypeSignatureKind(sig) == Signature.BASE_TYPE_SIGNATURE) {
			return false;
		}
		if(basicReferencesType(sig, type, typeSimpleName,
				typeFQName, container)) {
			return true;
		}
		String typeArgs[] = Signature.getTypeArguments(sig);
		if(typeArgs.length > 0) {
			for(String typeArg : typeArgs) {
				if(referencesType(typeArg, type, typeSimpleName,
						typeFQName, container)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Answer true if the Java type signature <code>sig</code> contains a
	 * reference to <code>type</cde>.  E.g., Map&lt;Foo,Bar&gt; has a reference
	 * to <code>Foo</code>.
	 * @param sig the type signature to check
	 * @param type the type checking for a reference
	 * @param typeSimpleName the simple name of <code>type</code>
	 * @param typeFQName the fully-qualified name of <code>type</code>
	 * @param container the IMember containing the reference in <code>sig</code>
	 * @return true if referenced
	 * @throws JavaModelException
	 */
	protected boolean basicReferencesType(String sig, IType type, String typeSimpleName,
			String typeFQName, IMember container) throws JavaModelException {
		switch(Signature.getTypeSignatureKind(sig)) {
		case Signature.ARRAY_TYPE_SIGNATURE: 
		case Signature.CLASS_TYPE_SIGNATURE:
		case Signature.WILDCARD_TYPE_SIGNATURE:
			if(!typeSimpleName.equals(Signature.getSignatureSimpleName(sig))) {
				return false;
			}
			String fqParm;
			if(Signature.getSignatureQualifier(sig).length() == 0) {
				fqParm = JavaModelHelper.getDefault()
				.resolveTypeName(Signature.getSignatureSimpleName(sig), container);
			} else {
				fqParm = Signature.getSignatureQualifier(sig) + "."
				+ Signature.getSignatureSimpleName(sig);
			}
			return typeFQName.equals(fqParm);
			
		case Signature.CAPTURE_TYPE_SIGNATURE:
		case Signature.TYPE_VARIABLE_SIGNATURE:
		case Signature.BASE_TYPE_SIGNATURE:	// void, int, long, etc.
		default:
			return false;
		}
	}
	
	public String resolveTypeName(String typeName, IMember referencingMember) throws JavaModelException {
	    IType type;
		if(!typeName.equals(Signature.getSimpleName(typeName))) {
		    // if the name is already qualified, there's nothing to do
		    return typeName;
		}
		if(referencingMember == null || referencingMember instanceof IType) {
			type = (IType)referencingMember;
		} else {
			type = referencingMember.getDeclaringType();
		}
		if(type == null) { return typeName; }
        String matches[][] = type.resolveType(typeName);
        if(matches == null || matches.length == 0) { return typeName; }
        // note: if matches.length > 1 (i.e. ambiguous) then we just choose the first
        if(matches.length > 1) {
            FerretPlugin.log(new Status(IStatus.WARNING, FerretJdtPlugin.pluginID, 0,
                    "resolveType(" + typeName + ", " + type.getElementName() +
                    ") has multiple resolutions; using first", null));
        }
        if(matches[0][0].length() == 0) { return matches[0][1]; }       // default package
        return matches[0][0] + "." + matches[0][1];
	}
    
	/**
	 * Resolve the given type name within the workspace.
	 * @param typeName
	 * @return the resolved type, or null if not found
	 */
    public IType resolveType(String typeName) {
    	return resolveType(typeName, null);
	}
    
	public IType resolveType(String typeName, IMember t) {
        // basicResolveType and resolveEnclosedType add results to type cache
		try {
			typeName = resolveTypeName(typeName, t);
		} catch (JavaModelException e) {
			logJME(e);
			return null;
		}

		int dollarIndex = typeName.lastIndexOf(Signature.C_DOLLAR); 
        if(dollarIndex < 0) { return  basicResolveType(typeName, t); }
        String parentName = typeName.substring(0, dollarIndex);
        IType result;
        if((result = resolveType(parentName, t)) == null) { return null; }
        return resolveEnclosedType(result, parentName, typeName.substring(dollarIndex + 1));
    }

    protected IType basicResolveType(final String typeName, final IJavaElement referencingMember) {
    	return resolveOperation(typeCache, typeName, new Callable<IType>() {
			public IType call() throws Exception {
		    	CollectingSearchRequestor requestor =  new CollectingSearchRequestor();
		    	SearchPattern pattern = SearchPattern.createPattern(typeName, 
		    			IJavaSearchConstants.TYPE,
		    			IJavaSearchConstants.DECLARATIONS,
		    			SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		    	// We tried using a JavaElement search scope, but it doesn't work
		    	// often resolve the types
		    	IJavaSearchScope scope = SearchEngine.createWorkspaceScope(); 
		    	performSearch(pattern, scope, requestor, new NullProgressMonitor());
		    	List<Object> results = new ArrayList<Object>(requestor.getValues());
		    	if(results.size() > 1) {
		    		FerretPlugin.log(new Status(IStatus.WARNING, FerretJdtPlugin.pluginID, 0,
		    				"resolveType(" + typeName + ") has multiple resolutions; using first", null));
		    	}
		    	return results.isEmpty() ? null : (IType)results.get(0);
			}});
    }

    /**
     * Resolve the child type defined within the provided type.  Assumes <code>remainder</code>
     * has no further $.
     * @param parent the parent type
     * @param fqParentName the parent's fully-qualified name
     * @param simpleName the type name within the parent
     * @return the resolved type, or null if not found
     */
    protected IType resolveEnclosedType(final IType parent, final String fqParentName, 
    		final String simpleName) {
    	if(parent == null) { return null; }
    	final String fqThisName = fqParentName + Signature.C_DOLLAR + simpleName;		// expected name
    	return resolveOperation(typeCache, fqThisName, new Callable<IType>() {
			public IType call() throws Exception {
		    	try {
		    		int occurrence = Integer.parseInt(simpleName);
		    		return findAnonymousType(parent, fqThisName, occurrence);
		    	} catch(NumberFormatException e) {
		    		return parent.getType(simpleName); 
		    	}
			}});
    }
    
    /**
     * Search for anonymous type, whose fully-resolved name should be fqName, within this container hierarchy.
     * @param member	 container to be searched
     * @param fqSoughtName fully qualified type name of type being sought
     * @param occurrence the type number
     * @return
     */
    protected IType findAnonymousType(IMember member, final String fqSoughtName, int occurrence) {
    	if(member.getCompilationUnit() == null) {
    		if(member instanceof IType) {
    			IType enclosed[];
				try {
					enclosed = ((IType)member).getTypes();
				} catch (JavaModelException e1) {
					return null;
				}
    			for(IType e : enclosed) {
    				if(fqSoughtName.equals(e.getFullyQualifiedName())) { return e; }
    			}
    			for(IType e : enclosed) {
    				IType result = findAnonymousType(e, fqSoughtName, occurrence);
    				if(result != null) { return result; }
    			}
    		}
    		return null; 
    	}
    	// Oh, this is grotty: unfortunately the occurrence information in ITypes is
    	// not related to anonymous type names.  According to 
    	// <http://dev.eclipse.org/newslists/news.eclipse.tools.jdt/msg16048.html>
    	// we need to parse the source code and using ITypeBinding.getBinaryName().
    	ASTParser parser = ASTParser.newParser(AST.JLS3);
    	parser.setSource(member.getCompilationUnit());
    	parser.setResolveBindings(true);	// ouch
    	ASTNode ast = parser.createAST(new NullProgressMonitor());
    	final List<IType> result = new ArrayList<IType>(1);
    	ast.accept(new ASTVisitor() {
    		public boolean visit(AnonymousClassDeclaration node) {
    			ITypeBinding binding = node.resolveBinding();
    			String fqTypeName = binding.getBinaryName();
			if(fqTypeName != null) {
			    IType type = (IType)binding.getJavaElement();
			    storeInCache(typeCache, fqTypeName, type);
			    if(fqTypeName.equals(fqSoughtName)) { result.add(type); }
			}
    			return true;
    		}
    	});
    	return result.isEmpty() ? null : result.get(0);
    }
    
	public IType resolveSignature(String sig, IMember container) {
		switch(Signature.getTypeSignatureKind(sig)) {
		case Signature.ARRAY_TYPE_SIGNATURE: 
		case Signature.CLASS_TYPE_SIGNATURE:
		case Signature.WILDCARD_TYPE_SIGNATURE:
			String fqParm;
			if(Signature.getSignatureQualifier(sig).length() == 0) {
				fqParm = Signature.getSignatureSimpleName(sig);
			} else {
				fqParm = Signature.getSignatureQualifier(sig) + "."
						+ Signature.getSignatureSimpleName(sig);
			}
			return resolveType(fqParm, container);
			
		case Signature.CAPTURE_TYPE_SIGNATURE:
		case Signature.TYPE_VARIABLE_SIGNATURE:
		case Signature.BASE_TYPE_SIGNATURE:	// void, int, long, etc.
		default:
			return null;
		}
	}

	public String getReturnType(IMethod method) {
		try {
			String returnTypeSignature = method.getReturnType();
			// FIXME: must handle Signature.TYPE_VARIABLE_SIGNATURE and ARRAY_TYPE_SIGNATURE
			// -- but I'm unsure how.  Perhaps it's just a documentation thing?  (I.e. We could just just return the name
			// of the main type.   
			if(Signature.getTypeSignatureKind(returnTypeSignature) != Signature.CLASS_TYPE_SIGNATURE) {
				return Signature.toString(returnTypeSignature);
			}
			return resolveTypeName(Signature.toString(returnTypeSignature), method);
		} catch(JavaModelException e) {
			logJME(e);
			return null;
		}
	}

    /**
     * Return the list of classes implementing the provided interface or abstract class.  
     */
    public Collection<IType> getImplementingClasses(IType input, IProgressMonitor monitor) {
		try {
        	if(input.isClass() && !Flags.isAbstract(input.getFlags())) {
        		return Collections.singleton(input);
        	}
        } catch(JavaModelException e) {
        	logJME(e);
            return Collections.EMPTY_LIST;
		}

        List<IType> list = new ArrayList<IType>();
        for(IType subtype : getAllSubtypes(input, monitor)) {
            try {
                if(subtype.isClass() && !Flags.isAbstract(subtype.getFlags()))  {
                	list.add(subtype); 
            	}
            } catch(JavaModelException e) { logJME(e); }
        }
        return list;
    }

    protected ITypeHierarchy getSuperTypeHierarchyFor(final IType type, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_SUPERTYPE_HIERARCHY, type);
        return resolveCache(key, new Callable<ITypeHierarchy>() {
			public ITypeHierarchy call() throws Exception {
				return getSubTypeHierarchyFor(type, monitor); 
			}});
    }
    
    protected ITypeHierarchy getSubTypeHierarchyFor(final IType type, final IProgressMonitor monitor) {
    	ResultKey key = new ResultKey(KEY_TYPE_HIERARCHY, type);
        return resolveCache(key, new Callable<ITypeHierarchy>() {
			public ITypeHierarchy call() throws Exception {
		    	if(monitor.isCanceled()) { throw new OperationCanceledException(); }
				ITypeHierarchy result = type.newTypeHierarchy(monitor);
				
		    	// Be clever: a type hierarchy for A is also a type hierarchy for all subtypes of A.
		    	for(IType subtype : result.getAllSubtypes(type)) {
		    		storeInCache(new ResultKey(KEY_TYPE_HIERARCHY, subtype), result);
		    		storeInCache(new ResultKey(KEY_SUPERTYPE_HIERARCHY, subtype), result);
		    	}
		    	for(IType supertype : result.getAllSupertypes(type)) {
		    		storeInCache(new ResultKey(KEY_SUPERTYPE_HIERARCHY, supertype), result);
		    	}
		    	return result;
			}});
    }

    public Set<IJavaElement> getReferences(final IMethod method, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_METHOD_REFERENCES, method);
        return resolveCache(key, new Callable<Set<IJavaElement>>() {
			public Set<IJavaElement> call() throws Exception {
	            CollectingSearchRequestor requestor =  new CollectingSearchRequestor();
	            // FIXME: could check the protection of the method and use it to tailor the
	            // search scope: e.g. if protected, just in hierarchy; if private, just in class; etc.
	            performSearch(SearchPattern.createPattern(method, IJavaSearchConstants.REFERENCES), 
	            		SearchEngine.createWorkspaceScope(), requestor, new SubProgressMonitor(monitor, 4));
	            if(monitor.isCanceled()) { throw new OperationCanceledException(); }
	            return requestor.getValues();
			}});
    }
    
    /**
     *  Return all references to the provided type.  May answer with IType, IMethod, I...
     */
    public Collection<IJavaElement> getReferences(final IType type, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_TYPE_REFERENCES, type);
        return resolveCache(key, new Callable<Set<IJavaElement>>() {
			public Set<IJavaElement> call() throws Exception {
		        CollectingSearchRequestor requestor =  new CollectingSearchRequestor() {
		            public Object filterSearchMatch(SearchMatch match) throws CoreException {
		                // What other types of matches could we expect?
		                if (!(match instanceof TypeReferenceMatch)) {
		                    System.out.println("JavaModelHelper.getReferences(IType) found " + match);
		                    return null;
		                } else if(match.getElement() instanceof IImportDeclaration) {
		                	return null;
		                }
		                return "default";
		            }
		        };
		        performSearch(
		        		SearchPattern.createPattern(type, IJavaSearchConstants.REFERENCES), 
		        		SearchEngine.createWorkspaceScope(), requestor,
		                new SubProgressMonitor(monitor, 4));
		        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		        return  requestor.getValues();
			}});
    }

    public Set<IMember> getInstanceofLocations(final IType type, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_INSTANCEOF, type);
        return resolveCache(key, new Callable<Set<IMember>>() {
			public Set<IMember> call() throws Exception {
		        monitor.beginTask("Finding instance-of of " + type.getElementName(), 100);
		        Set<IMember> result = new HashSet<IMember>();
		        processTypeReferences(type, new InstanceofStatementFinder(), 
		        		result, new SubProgressMonitor(monitor, 100));
		        monitor.done();
		        return result;
			}});
    }
    
    public Set<IMember> getCatchLocations(final IType exception, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_CATCH, exception);
        return resolveCache(key, new Callable<Set<IMember>>() {
			public Set<IMember> call() throws Exception {
		        monitor.beginTask("Finding catches of " + exception.getElementName(), 100);
		        Set<IMember> result = new HashSet<IMember>();
		        processTypeReferences(exception, new CatchesStatementFinder(), 
		        		result, new SubProgressMonitor(monitor, 100));
		        monitor.done();
		        return result;
			}});
    }
    
    public Set<IMember> getThrowLocations(final IType throwableType, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_THROWS, throwableType);
        return resolveCache(key, new Callable<Set<IMember>>() {
			public Set<IMember> call() throws Exception {
		        monitor.beginTask("Finding throws of " + throwableType.getElementName(), 100);
		        Set<IMember> result = new HashSet<IMember>(); // Should be JavaElementReference
		        processTypeReferences(throwableType, new ThrowsStatementFinder(), 
		        		result, new SubProgressMonitor(monitor, 100));
		        monitor.done();
		        return result;
			}});
    }
    
    public Set<IMember> getCastLocations(final IType type, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_CAST, type);
        return resolveCache(key, new Callable<Set<IMember>>() {
			public Set<IMember> call() throws Exception {
		        monitor.beginTask("Finding casts tp " + type.getElementName(), 100);
		        Set<IMember> result = new HashSet<IMember>();
		        processTypeReferences(type, new CastStatementFinder(), 
		        		result, new SubProgressMonitor(monitor, 100));
		        monitor.done();
		        return result;
			}});
    }
    
    protected void processTypeReferences(IType type, StatementFinder finder, 
    		Set<IMember> results, IProgressMonitor monitor) {
        Set<IType> types = new HashSet<IType>();
        types.add(type);   // FIXME: expand this set?
        Set<String> typeNames = new HashSet<String>();
        for(IType t : types) { typeNames.add(t.getFullyQualifiedName()); }
        
        Multimap<ITypeRoot,IMember> refs = HashMultimap.create();
 
        for(IType t : types) {
            // Group by their declaring container (classfiles or compilation units), then try resolving with binding
            // We try to help caching by running them in sequential order
            for(Object element : getReferences(t, new SubProgressMonitor(monitor, 50))) {
                if(monitor.isCanceled()) { throw new OperationCanceledException(); }
                if(element instanceof IMember && !(element instanceof IType)) {
                	IMember member = (IMember)element;
                    refs.put(member.getTypeRoot(), member);
                }
            }
        }
        IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 50);
        subMonitor.beginTask("Identifying statements", refs.keySet().size());
        for(ITypeRoot container : refs.keySet()) {
            processStatements(container, refs.get(container), types, typeNames, 
            		finder, results, new SubProgressMonitor(subMonitor, 1));
            if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        }
        subMonitor.done();
    }


    // Note: this is public purely for testing purposes and is not intended for front-line use
    public void processStatements(ITypeRoot container, Collection<IMember> candidates, 
    		Set<IType> types, Set<String> typeNames, StatementFinder finder,
            Collection<IMember> results, IProgressMonitor monitor) {
        // First eliminate those methods that don't actually throw anything
        //    Figure out the source range actually required
        // Then do a detailed AST parse to identify throws and ensure they actually do a throw
        monitor.beginTask("Processing statements for " + finder.getClass().getName() 
        		+ " in " + container, 5 + 2 * candidates.size());
        try {
        	List<IMember> shortlist = new ArrayList<IMember>(candidates.size());
        	int startPosition = -1, stopPosition = -1;
        	for(IMember candidate : candidates) {
        		monitor.worked(1);
                if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        		try {
        			String source = candidate.getSource();
        			if(source == null) { 
        				FerretPlugin.log(new Status(IStatus.INFO, FerretJdtPlugin.pluginID, 0,
        						"unable to obtain source for " + candidate, null));
        				continue;
        			}
        			if(!finder.preliminaryMatch(source)) { continue; }
        			ISourceRange sr = candidate.getSourceRange();
        			if(startPosition < 0 || sr.getOffset() < startPosition) { startPosition = sr.getOffset(); }
        			if(stopPosition < 0 || stopPosition < sr.getOffset() + sr.getLength()) { stopPosition = sr.getOffset() + sr.getLength(); } 
        			shortlist.add(candidate);
        		} catch(JavaModelException e) {
        			FerretPlugin.log(new Status(IStatus.WARNING, FerretJdtPlugin.pluginID, 0,
        					"unable to obtain source for " + candidate, e));
        			continue;
        		}
        	}
        	if(shortlist.isEmpty()) { return; }
        	ASTNode cu = getAST(container, startPosition, stopPosition - startPosition,
                    new SubProgressMonitor(monitor, 5));
        	if(cu == null) { return; }
        	IExtendibleSourceRange sr = new ExtendibleSourceRange(startPosition, stopPosition - startPosition);
        	finder.configure(types, typeNames);
        	for(IMember candidate : shortlist) {
                if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        		if(finder.check(cu, candidate)) {
        			results.add(candidate);    // new JavaReference(method, sr)
        		}
        		monitor.worked(1);
        	}
        } finally { monitor.done(); }
    }

    public Set<IMember> getConstructorReferences(final IType type, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_CONSTRUCTOR_REFERENCES, type);
        return resolveCache(key, new Callable<Set<IMember>>() {
			public Set<IMember> call() throws Exception {
		        SearchPattern constructorPatterns =
		            SearchPattern.createPattern(type.getFullyQualifiedName(), // + "(*)",
		                IJavaSearchConstants.CONSTRUCTOR, IJavaSearchConstants.REFERENCES,
		                SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		//            SearchPattern.createPattern(type, IJavaSearchConstants.REFERENCES,
		//                SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		        
		        CollectingSearchRequestor requestor =  new CollectingSearchRequestor() {
		            protected Object filterSearchMatch(SearchMatch match) throws CoreException {
		                try {
		                    if(match.getAccuracy() == SearchMatch.A_INACCURATE
		                            && match.getElement() instanceof IMethod
		                            && ((IMethod)match.getElement()).isConstructor()
		                            && isConstructorKeywordReference(match)) {
		                        return null;
		                    }
		                } catch(JavaModelException e) {
		                    return null;
		                }
		                return "default";
		            }
		        };
		        performSearch(constructorPatterns, SearchEngine.createWorkspaceScope(), 
		        		requestor, new SubProgressMonitor(monitor, 4));
		        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		        Set<IMember> result = new HashSet<IMember>();
		        for(Object ref : requestor.getValues()) {
		            if(ref instanceof IMember) { result.add((IMember)ref); }
		        }
		        return result;
			}});
    }
    
    protected static String keywords[] = new String[] { "this(", "super(" };
    protected boolean isConstructorKeywordReference(SearchMatch match) throws JavaModelException {
        IMethod constructor = (IMethod)match.getElement();
        String source = constructor.getSource();
        if(source == null) {
            // FIXME: this test fails for .class files. Consider using class disassembler?
            // ToolFactory.createDefaultClassFileDisassembler()
            return false;
        }
        ISourceRange constructorRange = constructor.getSourceRange();
        // Look for "this(" or "super("
        int offset = match.getOffset() - constructorRange.getOffset();
        for(String keyword : keywords) {
            if(source.length() > offset + keyword.length() && 
            		source.substring(offset, offset + keyword.length()).equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the methods creating instances of the provided type.  If <code>instantiatedType</code>
     * is an interface, then look for creations of any of its implementing types.  These results do
     * not include super() or this() calls from within constructors. 
     * @param instantiatedType
     * @param monitor
     * @return
     */
    public Map<IType,Set<IMember>> getMethodsInstantiatingType(final IType instantiatedType, 
    		final IProgressMonitor monitor) {
            ResultKey key = new ResultKey(KEY_METHOD_INSTANTIATING, instantiatedType);
            return resolveCache(key, new Callable<Map<IType,Set<IMember>>>() {
    			public Map<IType,Set<IMember>> call() throws Exception {
    				try {
    					monitor.beginTask("Finding methods instantiating provided type", 10);
    					Map<IType,Set<IMember>> result = new HashMap<IType,Set<IMember>>();
    					Collection<IType> typesToCheck;
    					boolean isClass = true;
    					try {
    						isClass = instantiatedType.isClass();
    					} catch(JavaModelException e) {/* do nothing */}
        				if(isClass) {
    						typesToCheck = new ArrayList<IType>(1);
    						typesToCheck.add(instantiatedType);
    						monitor.worked(1);
    					} else {
    						typesToCheck = getImplementingClasses(instantiatedType, new SubProgressMonitor(monitor, 1));
    					}
    					IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 9);
    					subMonitor.beginTask("Finding instantiators", typesToCheck.size());
    					for(IType type : typesToCheck) {
    						result.put(type, getMethodsInstantiatingClass(type, new SubProgressMonitor(subMonitor, 1)));
    					}
    					subMonitor.done();
    					if(monitor.isCanceled()) { throw new OperationCanceledException(); }
    					return result;
    				} finally {
    					monitor.done();
    				}
    			}});
    }
      
    public Set<IMember> getMethodsInstantiatingClass(IType type, IProgressMonitor monitor) {
        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        Set<IMember> accepted = new HashSet<IMember>();
        
        for (IMember reference : getConstructorReferences(type, monitor)) {
            if(monitor.isCanceled()) { throw new OperationCanceledException(); }
            if(reference instanceof IMethod) {
                IMethod method = (IMethod) reference;
                // Don't include own constructors, or from super constructors.  
                // This assumes that constructor doesn't itself instantiate an object of its supertype, as
                // might occur, for example, for sophisticated Collection classes implemented using
                // one of its superclasses; this seems an unlikely case though.
                try {
					if(!method.isConstructor() || 
					        !(type.equals(method.getDeclaringType()) || isImmediateSubtype(method.getDeclaringType(), type))) {
					    // fqTypeName.equals(method.getDeclaringType().getFullyQualifiedName())
					    accepted.add(method);
					}
				} catch (JavaModelException e) {
					logJME(e);
				}
            } else if (reference instanceof IInitializer) {
                accepted.add((IInitializer)reference);
            } else if (reference instanceof IField) {
                accepted.add((IField)reference);
            } else {
            	// Subtypes not specifying their own constructor have references
            	// to this constructor
//            	FerretPlugin.log(new Status(IStatus.WARNING, FerretJdtPlugin.pluginID, 0,
//            			"Strange reference to constructor for " + FerretPlugin.prettyPrint(type) + ": "
//            			+ FerretPlugin.prettyPrint(reference) + " (implements "
//            			+ FerretPlugin.prettyPrint(reference.getClass().getInterfaces()) + ")", null));
            }
        }
		return accepted;
	}

	public Set<IMethod> getMethodsReturningExactType(final IType type, final IProgressMonitor monitor) {
		ResultKey key = new ResultKey(KEY_METHOD_RETURNING, type);
		return resolveCache(key, new Callable<Set<IMethod>>() {
			public Set<IMethod> call() throws Exception {
				Set<IMethod> result = new HashSet<IMethod>();
				String fqn = type.getFullyQualifiedName();
				for (IJavaElement next : getReferences(type, new SubProgressMonitor(monitor, 1))) {
					if(next instanceof IMethod) {
						IMethod method = (IMethod)next;
						String fqReturnTypeName = getReturnType(method);
						if (fqReturnTypeName != null && fqReturnTypeName.equals(fqn)) {
							result.add(method);
						}
					}
				}
				monitor.done();
				return result;
			}});
	}
		
    public Set<IMethod> getAlternativeImplementations(final IMethod method, final IProgressMonitor monitor) {
    	ResultKey key = new ResultKey(KEY_ALTERNATIVE_METHOD_IMPLEMENTATIONS, method);
    	return resolveCache(key, new Callable<Set<IMethod>>() {
    		public Set<IMethod> call() throws Exception {
    			// FIXME: verify that this does the right thing
    			// Probably not: we should do the following:
    			//   * if a method implementation, then check for all interfaces that
    			//      this method implements, and search for other implementations
    			//      of that
    			//   * if an interface, look for implementors of all super-interfaces
    			//      specifying this method

    			// FindDeclarationsInHierarchy also uses IJavaSearchConstants.IGNORE_RETURN_TYPE
    			SearchPattern pattern = SearchPattern.createPattern(method, 
    					IJavaSearchConstants.DECLARATIONS 
    					| IJavaSearchConstants.IGNORE_DECLARING_TYPE);
    			CollectingSearchRequestor requestor = new CollectingSearchRequestor();
    			performSearch(pattern,
    					SearchEngine.createHierarchyScope(method.getDeclaringType()),
    					requestor, monitor);
    			if(monitor.isCanceled()) { throw new OperationCanceledException(); }
    			Set<IMethod> result = new HashSet(requestor.getValues());
    			result.remove(method);
    			return result;
    		}});
    }

    /**
     * A reimplementation of org.eclipse.core.internal.runtime.AdapterManager.computeClassOrder().
     * Compute the equivalent types for <code>type</code> from most specific to
     * least specific.  
     * 
     * [UNSURE OF THIS: Note that  <code>java.lang.Object</code> is deliberately not included
     * in this list.  This will not produce the expected result when <code>type</code> is an interface,
     * returning only the immediate superinterfaces.]
     * @param t
     * @return the equivalent types
     */
    public IType[] computeTypeOrder(final IType type, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_TYPE_ORDER, type);
		return resolveCache(key, new Callable<IType[]>() {
			public IType[] call() throws Exception {
				IType t = type;
		        ITypeHierarchy th = getSuperTypeHierarchyFor(t, monitor);
		        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		        List<IType> types = new ArrayList<IType>();
		        do {
		            types.add(t);
		            IType ifs[] = th.getSuperInterfaces(t);
		            if(ifs != null) {
			            for(int i = 0; i < ifs.length; i++) {
			                if(!types.contains(ifs[i])) {
			                    types.add(ifs[i]);
			                }
			            }
		            }
		        } while((t = th.getSuperclass(t)) != null);
		// Is the following necessary?  Shouldn't an adapter be able to adapt any Object?
		//        IType o = types.get(types.size() - 1); // we should at least have java.lang.Object there.
		//        assert(o.getFullyQualifiedName().equals("java.lang.Object"));
		        return types.toArray(new IType[types.size()]);
			}});
    }

    public IField[] getUsedFields(IJavaElement je, IProgressMonitor monitor) {
        SearchEngine engine = new SearchEngine();
        CollectingSearchRequestor resultCollector = new CollectingSearchRequestor();
        try {
            engine.searchDeclarationsOfAccessedFields(je, resultCollector, monitor);
        } catch (JavaModelException e) {
            logJME(e);
            return new IField[0];
        } catch(NullPointerException e) {
        	handleJDTNPE(e); 
        	return new IField[0];
        }
        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        Set<Object> used = resultCollector.getValues();
        return used.toArray(new IField[used.size()]);
    }

    public Collection<IMethod> getMethodsSent(IJavaElement je, IProgressMonitor monitor) {
        SearchEngine engine = new SearchEngine();
        CollectingSearchRequestor resultCollector = new CollectingSearchRequestor();
        try {
            engine.searchDeclarationsOfSentMessages(je, resultCollector, monitor);
        } catch (JavaModelException e) {
            logJME(e);
            return new LinkedList<IMethod>();
        } catch(NullPointerException e) {
        	handleJDTNPE(e);
            return new LinkedList<IMethod>();
        }
        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        Set<IMethod> sent = resultCollector.getValues();
        return sent;
    }

    private void handleJDTNPE(NullPointerException e) {
    	// logging = same as noting by somebody else
    	if(!FerretJdtPlugin.suppressJDTNullPointerExceptions()) { 
    		throw e;
    	}
		return;
	}

	public Collection<IType> getReferencedTypes(IJavaElement je, IProgressMonitor monitor) {
        SearchEngine engine = new SearchEngine();
        CollectingSearchRequestor resultCollector = new CollectingSearchRequestor();
        try {
            engine.searchDeclarationsOfReferencedTypes(je, resultCollector, monitor);
        } catch (JavaModelException e) {
            logJME(e);
            return Collections.EMPTY_SET;
        } catch(NullPointerException e) {
        	handleJDTNPE(e);
        	return Collections.EMPTY_SET;
        }
        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        Collection<IType> results = resultCollector.getValues();
        return results;
	}

    public boolean isThrowable(IType type, IProgressMonitor monitor) {
        if(type.getFullyQualifiedName().equals("java.lang.Throwable")) {
            return true;
        }
        IType sups[] = getSupertypes(type, monitor);
        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
        for(IType supertype : sups) {
            if(supertype.getFullyQualifiedName().equals("java.lang.Throwable")) {
                return true;
            }
        }
        return false;
    }

	public Collection<IMember> getFieldSetters(final IField field, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_FIELD_SETTERS, field);
		return resolveCache(key, new Callable<Collection<IMember>>() {
			public Collection<IMember> call() throws Exception {
		        SearchPattern pattern = SearchPattern.createPattern(field, 
		                IJavaSearchConstants.WRITE_ACCESSES);
		        CollectingSearchRequestor requestor = new CollectingSearchRequestor();
		        performSearch(pattern, SearchEngine.createWorkspaceScope(), requestor, monitor);
		        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		        Set<IMember> result = new HashSet<IMember>();
		        // Sigh: we sometimes have fluke elements in here, like fields. WTF?
		        for(Iterator<?> iter = requestor.getValues().iterator(); iter.hasNext();) {
		        	Object next = iter.next();
		        	if(next instanceof IMember && !(next instanceof IField)) { result.add((IMember)next); }
		        }
		        return result;
			}});
	}

	public Collection<IMember> getFieldReaders(final IField field, final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_FIELD_READERS, field);
		return resolveCache(key, new Callable<Collection<IMember>>() {
			public Collection<IMember> call() throws Exception {
				/* apparently READ_ACCESSES doesn't pick up uses of final vars in case statements,
				 * where as REFERENCES does. */
		        SearchPattern pattern = 
		        	isFinal(field) ? SearchPattern.createPattern(field, IJavaSearchConstants.REFERENCES)
		        			: SearchPattern.createPattern(field, IJavaSearchConstants.READ_ACCESSES);
		        CollectingSearchRequestor requestor = new CollectingSearchRequestor();
		        performSearch(pattern, SearchEngine.createWorkspaceScope(), requestor, monitor);
		        if(monitor.isCanceled()) { throw new OperationCanceledException(); }
		        Set<IMember> result = new HashSet<IMember>();
		        // Sigh: we sometimes have fluke elements in here, like fields. WTF?
		        for(Iterator<?> iter = requestor.getValues().iterator(); iter.hasNext();) {
		        	Object next = iter.next();
		        	if(next instanceof IMember && !(next instanceof IField)) { 
		        		result.add((IMember)next);
	        		} else {
	        			System.out.println("Strange result for field-readers: " + FerretPlugin.prettyPrint(next));
	        		}
		        }
		        return result;
			}});
	}

	protected boolean isFinal(IField field) {
		try {
			return Flags.isFinal(field.getFlags());
		} catch(JavaModelException e) {
			logJME(e);
			return false;
		}
	}

//	public String toString(IJavaElement rootElement) {
//		if(rootElement instanceof IMethod) {
//			IMethod method = (IMethod)rootElement;
//			IType declaringType = method.getDeclaringType();
//			try {
//				StringBuffer buf = new StringBuffer();
//				String signature = method.getSignature();
//				buf.append(declaringType.getElementName());
//				buf.append(".");
//				buf.append(Signature.toString(signature, method.getElementName(), null, false, false));
//				buf.append(" - ");
//				buf.append(Signature.getQualifier(declaringType.getFullyQualifiedName()));
//				return buf.toString();
//			} catch(JavaModelException e) {
//				return declaringType.getElementName() + "." + method.getElementName() + "(...) - " +
//				Signature.getQualifier(declaringType.getFullyQualifiedName());
//			}
//		} else if(rootElement instanceof IType) {
//			IType type = (IType)rootElement;
//			return type.getElementName() + " - " + Signature.getQualifier(type.getFullyQualifiedName());
//		} else if(rootElement instanceof IField) {
//			IField field = (IField) rootElement;
//			return field.getElementName() + " (" + field.getDeclaringType().getFullyQualifiedName() + ")";
//		} else if(rootElement instanceof IJavaElement) {
//			IJavaElement element = (IJavaElement) rootElement;
//			IJavaElement parent = element.getParent();
//			String className = element.getClass().toString();
//			return element.getElementName() + 
//				(parent == null ? "" : " - " + parent.getElementName()) +
//				" [" + Signature.getSimpleName(className) + "]";
//		}
//		return "<error: unknown type: " + rootElement.getClass() + ">";
//	}
	
//	public static Set validateJdtModelCache(IJavaElement e) {
//        Set s = new HashSet();
//        Map childCache = (Map)fetchField(JavaModelManager.getJavaModelManager().cache, "childrenCache");
//        System.out.println("Child cache contains element? " + childCache.containsKey(e));
//        System.out.println("element hash = " + e.hashCode());
//        for(Object o : childCache.keySet()) {
//        	if(o instanceof IMember) {
//        		if(e.getElementName().equals(((IMember)o).getElementName())
//        				&& e.getElementType() == ((IMember)o).getElementType()) {
//        			s.add(o);
//        			System.out.println("Element with name; hashCode() = " + o.hashCode());
//        		}
//        	}
//        }
//        return s;
//	}
	
	public List<IMethod> getMethodOverriders(final IMethod m, 
			final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_METHOD_OVERRIDERS, m);
		return resolveCache(key, new Callable<List<IMethod>>() {
			public List<IMethod> call() throws Exception {
				List<IMethod> results = new ArrayList<IMethod>();
				for(IType subcl : getAllSubclasses(m.getDeclaringType(), monitor)) {
					IMethod found[] = subcl.findMethods(m);
					if(found != null) { Collections.addAll(results, found); }
				}
				return results;
			}});
	}

	public List<IMethod> getMethodsOverridden(final IMethod m, 
			final IProgressMonitor monitor) {
        ResultKey key = new ResultKey(KEY_METHODS_OVERRIDDEN, m);
		return resolveCache(key, new Callable<List<IMethod>>() {
			public List<IMethod> call() throws Exception {
				List<IMethod> results = new ArrayList<IMethod>();
				for(IType subcl : getAllSuperclasses(m.getDeclaringType(), monitor)) {
					IMethod found[] = subcl.findMethods(m);
					if(found != null) { Collections.addAll(results, found); }
				}
				return results;
			}});
	}

	public Collection<IMethod> getImplementingMethods(IMethod ifaceMethod,
			IProgressMonitor monitor) {
		Collection<IType> implClasses = getImplementingClasses(ifaceMethod.getDeclaringType(), monitor);
		Collection<IMethod> methods = new ArrayList<IMethod>();
		for(IType impl : implClasses) {
			if(monitor.isCanceled()) { throw new OperationCanceledException(); }
			IMethod found[] = impl.findMethods(ifaceMethod);
			if(found != null && found.length > 0) {
				Collections.addAll(methods, found);
			}
		}
		return methods;
	}


	public String resolvedMethodSignature(IMethod input) throws JavaModelException {
		String original = input.getSignature().replace('/', '.');
		if(original.indexOf(Signature.C_UNRESOLVED) < 0) { return original; }
		String parmSigs[] = Signature.getParameterTypes(original);
		StringBuilder newSignature = new StringBuilder();
		newSignature.append('(');
		for(String parmSig : parmSigs) {
			newSignature.append(resolvedTypeSignature(parmSig, input));
		}
		newSignature.append(')');
		newSignature.append(resolvedTypeSignature(Signature.getReturnType(original), input));
		return newSignature.toString();
	}
	
	public String resolvedTypeSignature(String typeSignature, IMember source) throws JavaModelException {
		switch(Signature.getTypeSignatureKind(typeSignature)) {
		case Signature.ARRAY_TYPE_SIGNATURE:
			int arrayCount = Signature.getArrayCount(typeSignature);
			return typeSignature.substring(0, arrayCount) 
				+ resolvedTypeSignature(typeSignature.substring(arrayCount), source);
			
		case Signature.CLASS_TYPE_SIGNATURE:
			return Signature.C_RESOLVED + resolveTypeSignature(typeSignature, source) + ";";

		case Signature.TYPE_VARIABLE_SIGNATURE:
			// See MethodDetailer.generateGenericMapping()
//			String equivalent = typeSignature;
//			for(Entry<String,String> mapping : generateGenericMapping(source.getDeclaringType()).entrySet()) {
//				equivalent = equivalent.replace(mapping.getKey(), mapping.getValue());
//			}
//			if(!equivalent.equals(typeSignature)) {
//				return resolvedTypeSignature(equivalent, source);
//			}
			/*FALLTHROUGH*/
		case Signature.CAPTURE_TYPE_SIGNATURE:
		case Signature.WILDCARD_TYPE_SIGNATURE:
		case Signature.BASE_TYPE_SIGNATURE:
		default:
    		return typeSignature;
		}
	}
	
	protected String resolveTypeSignature(String typeSignature, IMember source) throws JavaModelException {
		typeSignature = typeSignature.replace('/', '.');
		if (Signature.getSignatureQualifier(typeSignature).length() == 0) {
			return JavaModelHelper.getDefault().resolveTypeName(
						Signature.getSignatureSimpleName(typeSignature), source);
		}
		return Signature.getSignatureQualifier(typeSignature) + "."
				+ Signature.getTypeErasure(Signature.getSignatureSimpleName(typeSignature));
	}

}
