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
package ca.ubc.cs.ferret.pde.queries;

import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.model.AbstractSingleParmConceptualQuery;
import ca.ubc.cs.ferret.pde.PdeModelHelper;
import ca.ubc.cs.ferret.references.AbstractReference;
import ca.ubc.cs.ferret.references.FileReference;
import ca.ubc.cs.ferret.references.URLReference;
import ca.ubc.cs.ferret.references.ZipEntryReference;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.core.ISourceObject;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.osgi.framework.Bundle;

public abstract class PdeSingleParmConceptualQuery<T> 
		extends AbstractSingleParmConceptualQuery<T> {
    protected final String PLUGIN_FILE_NAME = "plugin.xml";
    protected Object modelHelperValidationContext;

	public PdeSingleParmConceptualQuery() {
        super();
    }

    protected String[] getNotableAttributes() {
        return new String[] { "point", "id", "targetId" };
    }
    
    protected String describePath(IPluginElement element, String attribute, IPluginExtension extension) {
        return describePath(element, attribute, extension, getNotableAttributes());
    }

    protected String describePath(IPluginElement element, String attribute,
            IPluginExtension extension, String notableAttributes[]) {
        StringBuffer generatedName = new StringBuffer();
        generatedName.append(element.getPluginBase().getId());
        generatedName.append("/");
        generatedName.append(PLUGIN_FILE_NAME);
        generatedName.append(": ");

        generatedName.append(" attribute '");
        generatedName.append(attribute);
        generatedName.append("' in <");
        generatedName.append(element.getName());
//        boolean addedAttributes = false;
        for(int i = 0; i < notableAttributes.length; i++) {
            if(element.getAttribute(notableAttributes[i]) != null) {
//                generatedName.append(addedAttributes ? ',' : '[');
                generatedName.append(' ');
                generatedName.append(notableAttributes[i]);
                generatedName.append("='");
                generatedName.append(element.getAttribute(notableAttributes[i]).getValue());
                generatedName.append("'");
//                addedAttributes = true;
            }
        }
//        if(addedAttributes) { generatedName.append(']'); }
        generatedName.append("> in  extension to ");
        generatedName.append(extension.getPoint());

        if(extension.getName().length() > 0) {
            generatedName.append(" named '");
            generatedName.append(extension.getName());
            generatedName.append("' ");
            if(extension.getId().length() > 0) {
                generatedName.append('(');
                generatedName.append(extension.getId());
                generatedName.append(") ");
            }
        } else if(extension.getId() != null) {
            generatedName.append(" with id ");
            generatedName.append(extension.getId());
        }

        return generatedName.toString();
    }

    protected AbstractReference createXMLWrapper(Object attr, IPluginModelBase pluginModel)
            throws IOException {
        int offset = 0, length = 0;
        
        if(attr instanceof IPluginObject) {
            AbstractReference ref = PdeModelHelper.getDefault().generateReference(pluginModel);
            IDocument document = PdeModelHelper.getDefault().readFileContents(ref.getInputStream(),
            		ref.getContentType().getDefaultCharset());         
            try {
                offset = document.getLineOffset(((ISourceObject)attr).getStartLine());
                length = document.getLineOffset(((ISourceObject)attr).getStopLine()) - offset;
                ref.setOffset(offset);
                ref.setLength(length);
            } catch (BadLocationException e) {
                // do nothing
            }
            return ref;
        } else if (attr instanceof IConfigurationElement) {
            Bundle b = Platform.getBundle(((IConfigurationElement)attr).getNamespace());
            URL url = b.getEntry("/" + PLUGIN_FILE_NAME);
            url = Platform.resolve(url);
            File f = new File(url.getFile());
            // FIXME: how to get the offset and length...?
            if(f.exists()) {
                return new FileReference(new Path(url.getFile()), offset, length);
            } else if(url.getProtocol().equals("jar")) {
                String joint = new URL(url.getPath()).getPath();
                int index = joint.indexOf('!');
                f = new File(url.getPath().substring(0, index));
                if(f.exists()) {
                    return new ZipEntryReference(f.getPath(), url.getPath().substring(index+1), offset, length);
                }
            }    
            return new URLReference(url, offset, length);
        } else {
            Class<?> ifs[] = attr.getClass().getInterfaces();
            System.out.println("Building XML wrapper: unknown attr type:" + attr);
            for(int i = 0; i < ifs.length; i++) {
                System.out.println("  ref implements: " + ifs[i]);
            }
            System.out.print("");   // purely for breakpoint purposes
            throw new FerretFatalError("unknown attribute type: " + attr);
        }
    }


    protected String deriveXPath(IConfigurationElement config, String attribute, 
            IExtension extension, String notableAttributes[]) {
        List<IConfigurationElement> elements = new LinkedList<IConfigurationElement>();
        IConfigurationElement current = config;
        elements.add(current);
        while(current.getParent() instanceof IConfigurationElement) {
            current = (IConfigurationElement)current.getParent();
            elements.add(current);
        }
        Collections.reverse(elements);

        StringBuffer generatedXPath = new StringBuffer();
        generatedXPath.append("/extention[@point=");
        generatedXPath.append(extension.getExtensionPointUniqueIdentifier());
        generatedXPath.append("]");
        for (IConfigurationElement ce : elements) {
            generatedXPath.append('/');
            generatedXPath.append(ce.getName());
            boolean addedAttributes = false;
            for(int i = 0; i < notableAttributes.length; i++) {
                if(ce.getAttribute(notableAttributes[i]) != null) {
                    generatedXPath.append(addedAttributes ? ',' : '[');
                    generatedXPath.append('@');
                    generatedXPath.append(notableAttributes[i]);
                    generatedXPath.append("='");
                    generatedXPath.append(ce.getAttribute(notableAttributes[i]));
                    generatedXPath.append("'");
                    addedAttributes = true;
                }
            }
            if(addedAttributes) { generatedXPath.append(']'); }
        }
        generatedXPath.append("/@");
        generatedXPath.append(attribute);
        return generatedXPath.toString();
    }


    protected String deriveXPath(IPluginElement config, String attribute, 
            IPluginExtension extension, String notableAttributes[]) {
        List<IPluginElement> elements = new LinkedList<IPluginElement>();
        IPluginElement current = config;
        elements.add(current);
        while(current.getParent() instanceof IPluginElement) {
            current = (IPluginElement)current.getParent();
            elements.add(current);
        }
        Collections.reverse(elements);

        StringBuffer generatedXPath = new StringBuffer();
        generatedXPath.append("/extention[@point=");
        generatedXPath.append(extension.getPoint());
        generatedXPath.append("]");
        for(IPluginElement pe : elements) {
            generatedXPath.append('/');
            generatedXPath.append(pe.getName());
            boolean addedAttributes = false;
            for(String notableAttr : notableAttributes) {
                if(pe.getAttribute(notableAttr) != null) {
                    generatedXPath.append(addedAttributes ? ',' : '[');
                    generatedXPath.append('@');
                    generatedXPath.append(notableAttr);
                    generatedXPath.append("='");
                    generatedXPath.append(pe.getAttribute(notableAttr).getValue());
                    generatedXPath.append("'");
                    addedAttributes = true;
                }
            }
            if(addedAttributes) { generatedXPath.append(']'); }
        }
        generatedXPath.append("/@");
        generatedXPath.append(attribute);
        return generatedXPath.toString();
    }

	@Override
	public void run(IProgressMonitor monitor) {
		super.run(monitor);
		modelHelperValidationContext =
			PdeModelHelper.getDefault().getValidationContext();
	}


    public boolean isValid() {
    	return PdeModelHelper.getDefault().isValid(modelHelperValidationContext);
    }
}
