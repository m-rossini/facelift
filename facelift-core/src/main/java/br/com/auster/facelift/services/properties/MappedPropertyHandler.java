/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on Mar 3, 2005
 */
package br.com.auster.facelift.services.properties;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.ConfigurationException;

/**
 * A simple implementation of the property handler service, which stores the properties in instances of <code>Map</code>. 
 * Also, the realms are stored in a Map too, to allow easy access the their configured properties.
 * <P>
 * @author framos
 * @version $Id: MappedPropertyHandler.java 161 2005-11-10 18:35:12Z framos $
 */
public class MappedPropertyHandler implements PropertyHandler {

    
	
    // ----------------------
    // Instance variables
    // ----------------------

    private Map properties;
    private String defaultRealm;
    
	private Logger log = LogFactory.getLogger(MappedPropertyHandler.class);
    
    
	
    // ----------------------
    // Constructors
    // ----------------------    

    public MappedPropertyHandler() throws ConfigurationException {
    }
    
    
    
    // ----------------------
    // Interface methods
    // ----------------------
       
    /**
     * Inherited from <code>RequestManager</code>
     */
    public String getProperty(String _key) {
        return getProperty(_key, defaultRealm);
    }

    /**
     * Inherited from <code>RequestManager</code>
     */
    public String getProperty(String _key, String _realm) {
        if (properties == null) {
            throw new IllegalStateException("service not initialized");
        }
        Properties props = (Properties) properties.get(_realm);
        if (props == null) {
            throw new IllegalArgumentException("property realm not defined");
        }
        return props.getProperty(_key);
    }

    /**
     * Inherited from <code>RequestManager</code>
     */
    public Set getRealms() {
        if (properties == null) {
            throw new IllegalStateException("service not initialized");
        }
        return properties.keySet();
    }

    /**
     * Inherited from <code>RequestManager</code>
     */
    public String getDefaultRealm() {
        if (defaultRealm == null) {
            throw new IllegalStateException("service not initialized");
        }
        return defaultRealm;
    }

    /**
     * Inherited from <code>RequestManager</code>
     */
    public void init(Element _configuration) throws ConfigurationException {
        log.info("Configuring properties service");
        properties = new HashMap();
        defaultRealm = DOMUtils.getAttribute(_configuration, "default-realm", true);
		log.info("default realm is '" + defaultRealm + "'");        
        NodeList realms = DOMUtils.getElements(DOMUtils.getElement(_configuration, "realms", true), "realm");
        if (realms == null) {
            throw new ConfigurationException("list of realms not found");
        }
        
        try {
            for (int i=0; i < realms.getLength(); i++) {
                Element current = (Element) realms.item(i);
                String name = DOMUtils.getAttribute(current, "name", true);
				String location = DOMUtils.getAttribute(current, "file", true);
                InputStream in = IOUtils.openFileForRead(location, DOMUtils.getBooleanAttribute(current, "encrypted", false));
                Properties p = new Properties();
                p.load(in);
				log.info("configuring realm '" + name + "' from file " + location);
                properties.put(name, p);
            }
        } catch (IOException ioe) {
            throw new ConfigurationException("I/O error while configuring properties", ioe);
        } catch (GeneralSecurityException gse) {
            throw new ConfigurationException("I/O error while configuring properties", gse);
		}
    }

}
