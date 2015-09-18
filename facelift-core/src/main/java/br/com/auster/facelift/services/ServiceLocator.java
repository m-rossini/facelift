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
 * Created on Feb 22, 2005
 */
package br.com.auster.facelift.services;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.audit.AuditLog;
import br.com.auster.facelift.services.audit.NoAuditConfiguredLog;
import br.com.auster.facelift.services.plugin.PluginService;
import br.com.auster.facelift.services.properties.PropertyHandler;



/**
 * Entry point to locate all configured services. Service locators must be configured prior to calling  
 * 	 <code>getService()</code> or any other get methods exposed by its interface.
 * <P>
 * 
 * @author framos
 * @version $id$
 */
public abstract class ServiceLocator {

	

    // ----------------------
    // Class variables
    // ----------------------

	private static ServiceLocator singleton;
    
	private static Logger logger;
    private static PluginService plugin;
    private static PropertyHandler property;
    private static AuditLog audit;

    public static final String FACTORY_CONFIGURATION = "auster.facelift.configuration";
    public static final String ENCRPYTED_CONFIGURATION_FLAG = "auster.facelift.configuration.encrypted";
    public static final String FACTORY_IMPLEMENTATION = "auster.facelift.factory.impl";

    
	
    // ----------------------
    // Instance variables
    // ----------------------
	
    protected Map params;

    
	
    // ----------------------
    // Public mehtods
    // ----------------------
	
	/**
	 * Returns the single instance of the currently configured service locator. If the service locator was not configured, it 
	 * 	is then initialized before returning it to the calling component.
	 * <P>
	 * Due to possible conflicts in multi-threaded and distributed environments, this method is defined synchronize its operation
	 * 	throughout all concurrent calling components. 
	 * 
	 * @return the single instance of the current service locator
	 */
    public static final synchronized ServiceLocator getInstance() {
         if (singleton == null) {
             init(true);
         }
         return singleton;
    }

	/**
	 * Initializes the service locator single instance, reading the specified configuration file, if it was not initialized
	 * 	yet. If the service locator has already being configured, it will do nothing.
	 * <P>
	 * Due to possible conflicts in multi-threaded and distributed environments, this method is defined synchronize its operation
	 * 	throughout all concurrent calling components. 
	 * 
	 */
    public static synchronized void init() {
        init(false);
    }
    
	/**
	 * Initializes the service locator single instance, reading the specified configuration file. Depending on the <code>_force</code>
	 * 	parameter, the configuration is forced to be re-read.
	 * <P>
	 * The configuration file is defined by the system property named <code>auster.facelift.configuration</code>. This property
	 * 	<strong>must</strong> be defined or else the service locator will not be initialized and all configured services will not
	 * 	be made available.
	 * <P>
	 * Due to possible conflicts in multi-threaded and distributed environments, this method is defined synchronize its operation
	 * 	throughout all concurrent calling components.
	 *  
	 * @param _force wether or not to force the re-configuration of the service locator 
	 */
    public static synchronized void init(boolean _force) {

        if ((singleton != null) && (!_force)) {
            return;
        }
        
        String filename = System.getProperty(FACTORY_CONFIGURATION);
        if (filename == null) {
            throw new IllegalStateException("locator configuration file property must be set");
        }
        
        Boolean encrypted = new Boolean(System.getProperty(ENCRPYTED_CONFIGURATION_FLAG));
        try {
        	init(IOUtils.openFileForRead(filename, encrypted.booleanValue()));			
        } catch (IOException ioe) {
            IllegalStateException e = new IllegalStateException("error acessing file");
            e.initCause(ioe);
            throw e;
        } catch (GeneralSecurityException gse) {
            IllegalStateException e = new IllegalStateException("security exception caught");
            e.initCause(gse);
            throw e;
        }
    }
    
    /**
     * Initializes the service locator single instance, reading the configuration data from the specified input stream. This
     * 	can be useful in cases like web applications, where the configuration file is not in the classpath, but is accessible
     * 	using the context reference. 
     * <p>
     * This method also needs to be called prior to any other call to the service locator, or else the {@link #init()} version
     * 	will be triggered.
     * <p>
     *  
     * @param _the input stream to read from
     */
    public static synchronized void init(InputStream _inStream) {
    	
        if (singleton != null) {
            return;
        }
        
        if (_inStream == null) {
            throw new IllegalStateException("locator configuration file property must be set");
        }
    	boolean finished = false;
        try {
            Element root = DOMUtils.openDocument(_inStream);			
            configureLogging(root);			
            configurePlugin(root);
            configureProperties(root);
            configureAudit(root);
            singleton = createServiceLocator(DOMUtils.getAttribute(root, "class-impl", false));
            singleton.configure(root);
            finished = true;
        } catch (ParserConfigurationException pce) {
            IllegalStateException e = new IllegalStateException("error parsing file");
            e.initCause(pce);
            throw e;
        } catch (SAXException saxe) {
            IllegalStateException e = new IllegalStateException("error parsing file");
            e.initCause(saxe);
            throw e;
        } catch (IOException ioe) {
            IllegalStateException e = new IllegalStateException("error acessing file");
            e.initCause(ioe);
            throw e;
        } catch (GeneralSecurityException gse) {
            IllegalStateException e = new IllegalStateException("security exception caught");
            e.initCause(gse);
            throw e;
        } finally {
        	if (!finished) {
        		singleton = null;
        	}
        }    
    }
	
	/**
	 * Returns the single instance of the plugin service, configured forthis service locator. This service is initialized
	 * 	together with the service locator, so there is no need to call its <code>init()</code> method.
	 *  
	 * @return the configured plugin service 
	 */
    public PluginService getPluginService() {
        return plugin;
    }    
	
	/**
	 * Returns the single instance of the audit service, configured for this service locator. This service is initialized
	 * 	together with the service locator, so there is no need to call its <code>init()</code> method.
	 *  
	 * @return the configured property service 
	 */
    public AuditLog getAuditService() {
        return audit;
    }    
    
	/**
	 * Returns the single instance of the property service, configured forthis service locator. This service is initialized
	 * 	together with the service locator, so there is no need to call its <code>init()</code> method.
	 *  
	 * @return the configured property service 
	 */
    public PropertyHandler getPropertiesService() {
        return property;
    }      

	/**
	 * Abstract definition of the method which exposes all user-implemented services to the client applications. This method
	 * 	returns the already configured and initialized instance of the service identified by the <code>_serviceId</code> string.
	 *  This identification must be unique across all configured services.
	 *  <P>
	 * If there is no service configured with the specified name, or if its instantiation and initialization could were not
	 * 	successful, an exception will be raised.  
	 * 
	 * @param _serviceId the unique identification name of a service
	 * @return the configured plugin service
	 */
    public abstract Object getService(String _serviceId);
    
	/**
	 * Returns the pre-configured properties of the current service locator. 
	 *  
	 * @return the map of properties for this service locator
	 */
    public Map getProperties() {
        return params;
    }    
    
	/**
	 * Reads all service locator specific properties and store them. These properties can be retrieved, whenever needed, by the
	 * 	<code>getProperties()</code> method.
	 * 
	 * @param _root the configuration of the service locator
	 */
    public void configure(Element _root) {
        // configuring factory properties
        logger.debug("configuring parameters for service locator");
        getProperties().putAll(createParamMap(DOMUtils.getElement(_root, "params", false)));
        // configuring services
        configureServices(DOMUtils.getElement(_root, "services", true));
    }
    
	
	
    // ----------------------
    // Protected & Private methods
    // ----------------------
	
	/**
	 * Configures the logging mechanism for the current service locator.
	 * 
	 * @param _root the configuration of the log service
	 */
    protected static void configureLogging(Element _root) {
        //configure log prior to anything else
        Element logService = DOMUtils.getElement(_root, "log-service", false);
        if (logService != null) {
        	LogFactory.configureLogSystem(DOMUtils.getElement(logService, "configuration", true));    	
        }
    	logger = LogFactory.getLogger(ServiceLocator.class);
    }

	/**
	 * Configures and initializes the plugin service. All plugin points configured are identified and stored to 
	 * 	be triggered whenever a service requests the plugin service to check for plugins at a specific context.
	 *  
	 * @param _root the configuration of the plugin service
	 */
    protected static void configurePlugin(Element _root) {
        //configure log prior to anything else
        Element pluginService = DOMUtils.getElement(_root, "plugin-service", true);
        if (pluginService != null) {
            try {
                plugin = (PluginService) Class.forName(DOMUtils.getAttribute(pluginService, "class-impl", true)).newInstance();
                plugin.init( (Element) DOMUtils.getElement(pluginService, "configuration", true) );
            } catch (IllegalArgumentException iae) {
                logger.fatal("illegal argument exception", iae);
                throw new IllegalStateException("could not initialize plugin service", iae);
            } catch (InstantiationException ie) {
                logger.fatal("instantiation exception", ie);
                throw new IllegalStateException("could not initialize plugin service", ie);
            } catch (IllegalAccessException iae) {
                logger.fatal("illegal access exception", iae);
                throw new IllegalStateException("could not initialize plugin service", iae);
            } catch (ClassNotFoundException cnfe) {
                logger.fatal("class not found exception", cnfe);
                throw new IllegalStateException("could not initialize plugin service", cnfe);
            } catch (ConfigurationException ce) {
                logger.fatal("service configuration exception", ce);
                throw new IllegalStateException("could not initialize plugin service", ce);
            } catch (SecurityException se) {
                logger.fatal("security exception", se);
                throw new IllegalStateException("could not initialize plugin service", se);
            }
        }
    }

    /**
     * Configures and initializes the properties service. All realms are loaded and made available through this 
     * 	 service. 
     * 
     * @param _root the configuration of the property service 
     */
    protected static void configureProperties(Element _root) {
        Element propService = DOMUtils.getElement(_root, "properties-service", true);
        try {
            property = (PropertyHandler)Class.forName(DOMUtils.getAttribute(propService, "class-impl", true)).newInstance(); 
            property.init( (Element) DOMUtils.getElement(propService, "configuration", true) );
        } catch (Exception e) {
            logger.fatal("Could not initialize properties service.");
            throw new IllegalStateException("could not initialize properties service", e);
        }
    }    
    
    /**
     * Configures and initializes the audit service. 
     * 
     * @param _root the configuration of the audit service 
     */
    protected static void configureAudit(Element _root) {
        Element propService = DOMUtils.getElement(_root, "audit-service", false);
        if (propService == null) {
        	audit = new NoAuditConfiguredLog();
        	return;
        }
        try {
            audit = (AuditLog)Class.forName(DOMUtils.getAttribute(propService, "class-impl", true)).newInstance(); 
            audit.init( (Element) DOMUtils.getElement(propService, "configuration", true) );
        } catch (Exception e) {
            logger.fatal("Could not initialize audit service.");
            throw new IllegalStateException("could not initialize audit service", e);
        }
    }  
    
    /**
     * Initializes the service locator itself. This method is called from the <code>init()</code> and triggers
     * 	the configuration of all core services (plugins, log and properties) as well as user-defined and implemented
     * 	services.
     * 
     * @param _klass the service locator implementation class
     * @return a service locator instance
     */
    protected static ServiceLocator createServiceLocator(String _klass) {
        if ((_klass == null) || (_klass.trim().length() <= 0)) {
            logger.info("service locator not configured. Setting default locator implementation"); 
            return new DefaultServiceLocator();
        }
        try {
            return (ServiceLocator) Class.forName(_klass).newInstance();
        } catch (InstantiationException ie) {
            IllegalStateException e = new IllegalStateException("cannot create instace of service locator");
            e.initCause(ie);
            throw e;
        } catch (IllegalAccessException iae) {
            IllegalStateException e = new IllegalStateException("illegal access to locator constructor");
            e.initCause(iae);
            throw e;
        } catch (ClassNotFoundException cnfe) {
            IllegalStateException e = new IllegalStateException("service locator class not found");
            e.initCause(cnfe);
            throw e;
        }
    }

	/**
	 * Builds a <code>java.util.Map</code> out of a parameter list in XML format. The XML schema for this 
	 *  is defined as a tag named <code>param</code> with the key valued as the <code>key</code> attribute and
	 *  the value is the text enclosed by the tag itself.
	 *   
	 * @param _root the parent XML tag of the list of <code>param</code> to be read 
	 * @return the map built out of the list of XML tags
	 */
    protected Map createParamMap(Element _root) {        
        Map params = new HashMap();
        if (_root == null) {
            return params;
        }
        NodeList list = DOMUtils.getElements(_root, "param");        
        if (list != null) {
            for (int i=0; i < list.getLength(); i++) {
                Element service = (Element) list.item(i);
                String key = DOMUtils.getAttribute(service, "key", true);
                String value = DOMUtils.getAttribute(service, "value", true);
                logger.debug("locator parameter [" + key + "] configured with value " + value);
                params.put(key, value);
            }
        }
        return params;
    }
    
	/**
	 * Reads all information related to the user-defined services. The information read here is stored afterwards 
	 * 	to be used whenever a service is requested by a client application.
	 * 
	 * @param _root the configuration of all services 
	 */
    protected void configureServices(Element _root) {
        NodeList list = DOMUtils.getElements(_root, "service");
        if (list != null) {
            for (int i=0; i < list.getLength(); i++) {
                Element currentService = (Element) list.item(i);
                String id = DOMUtils.getAttribute(currentService, "id", true);
                String klass = DOMUtils.getAttribute(currentService, "class-impl", true);
                String type = DOMUtils.getAttribute(currentService, "type", true);
                Element params= DOMUtils.getElement(currentService, "configuration", false);
                // creating service instance
                try {
                    configureSingleService(id, klass, type, params);
                } catch (ConfigurationException ce) {
					// just log and let the configuration go on, so other services can 
					// be configured and made available 
					logger.error("could not configure service '" + id + "'", ce);
                }
            }
        }
    }
    
	/**
	 * Stores all service information to be used later. Since the structure used to store the service information is dependent
	 * 	of the service locator implementation, this method's implementation is postponed to the real service locator classes. 
	 * 
	 * @param _id the unique identification name of a service
	 * @param _klass the service implementation class
	 * @param _type if it's a local or remote service
	 * @param _params the configuration parameters, specific for this service
	 * 
	 * @throws ConfigurationException if any error, while preparing and configuring the service, where detected
	 */
    protected abstract void configureSingleService(String _id, String _klass, String _type, Element _params) throws ConfigurationException;
    
}
