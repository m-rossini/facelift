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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class is the default implementation of a service locator. It uses a XML file as configuration for the services
 * 	to be made available, as well as logging, plugins and properties default services.
 * <P>
 * 
 * @author framos
 * @version $id$
 */
public class DefaultServiceLocator extends ServiceLocator {

    
	
    // ----------------------
    // Instance variables
    // ----------------------

	private Map mappings;
    private Map localCaching;
    private Logger log = LogFactory.getLogger(DefaultServiceLocator.class);
    

	
    // ----------------------
    // Constructors
    // ----------------------
    
    protected DefaultServiceLocator() {
        mappings = new HashMap();
        params = new HashMap();
        localCaching = new HashMap();
    }

	
    
    // ----------------------
    // Public methods
    // ----------------------
    
	/**
	 * Inherited from <code>ServiceLocator</code>
	 */
    public Object getService(String _serviceId) {
        ServiceEntry entry = (ServiceEntry) mappings.get(_serviceId);
        if (entry == null) {
            throw new ServiceNotFoundException("service '" + _serviceId + "' not defined / configured");
        }
        try {
            return getServiceReference(entry);
        } catch (ConfigurationException ce) {
            throw new ServiceNotFoundException(ce.getMessage(), ce);
        }
    }

	
	
    // ----------------------
    // Protected & Private methods
    // ----------------------
	
	/**
	 * Initializes and returns the service instance to be sent to the client application. If identifies if the service is 
	 * 	a local or remote one, so that the correct initialization steps are taken.
	 * 
	 * @param _entry the service configuration information, previously loaded
	 * @return the reference to the specified service 
	 * @exception ConfigurationException if any error while initializing the service was detected 
	 */
    private Object getServiceReference(ServiceEntry _entry) throws ConfigurationException {
        Object svc = null;
		log.debug("lookup for service '" + _entry.id + "'");
        if (_entry.type.equals("local")) {
            if (localCaching.containsKey(_entry.id)) {
                svc = localCaching.get(_entry.id);
            } else {
                try {
                    svc = Class.forName(_entry.klass).newInstance();
					((Service)svc).init(_entry.configuration);
                    localCaching.put(_entry.id, svc);
                } catch (ClassNotFoundException cnfe){
                    throw new ConfigurationException("service class not found", cnfe);
                } catch (InstantiationException ie){
                    throw new ConfigurationException("cannot create service instance", ie);
                } catch (IllegalAccessException iae){
                    throw new ConfigurationException("cannot execute service constructor", iae);
                } catch (SecurityException se) {
                    throw new ConfigurationException("security error", se);
                } catch (IllegalArgumentException iae) {
                    throw new ConfigurationException("illegal argument", iae);
                }
            }
        } else if (_entry.type.equals("remote")) {
            return lookupRemoteService(_entry);
        } else {
            throw new ConfigurationException("uknown service type");
        }
        if (svc == null) {
            throw new ServiceNotFoundException("service not found");
        }
        return svc;
    }
    
        
	/**
	 * Inherited from <code>ServiceLocator</code>
	 */
    protected void configureSingleService(String _id, String _klass, String _type, Element _configuration) throws ConfigurationException {
        ServiceEntry entry = new ServiceEntry(_id, _klass, _type, _configuration);
        log.info("found service [" + _id + "] of type " + _type);
        log.debug("[" + _id + "] class-impl = '" + _klass + "'");
        mappings.put(_id, entry);
    }

	/**
	 * Looks up for remote service references. It uses the service defined parameters to locate an acquire this
	 * 	remote reference.
	 * 
	 * @param _entry the service configuration previously loaded
	 * @return the remote reference to the service
	 * @throws ConfigurationException if any error while initializing the service was detected
	 */
    private Object lookupRemoteService(ServiceEntry _entry) throws ConfigurationException {
        
        try {
            // get all j2ee mandatory attrs from parms.
            Context context = null;
            Element jndiContext = null;
            if (_entry.configuration != null) {
                jndiContext = DOMUtils.getElement(_entry.configuration, "jndi-context", false);
            }
            if (jndiContext != null) {
                Hashtable lookupConfig = new Hashtable();
                NodeList parameters = DOMUtils.getElements(jndiContext, "parameter");
                for (int i=0; i < parameters.getLength(); i++) {
                    Element currentParameter = (Element) parameters.item(i);
                    lookupConfig.put(DOMUtils.getAttribute(currentParameter, "name", true),
                                     DOMUtils.getText(currentParameter).toString());
                }
                context = new InitialContext(lookupConfig);
            } else {
                context = new InitialContext();
            }
            // lookup
            Object remoteObject = context.lookup(DOMUtils.getAttribute(_entry.configuration, "jndi-name", true));
            Class remoteClass = Class.forName(_entry.klass);
            return PortableRemoteObject.narrow(remoteObject, remoteClass);
            
        } catch (IllegalArgumentException iae) {
            log.error("illegal argument exception", iae);
            throw new ConfigurationException(iae);
        } catch (ClassNotFoundException cnfe) {
			log.error("class not found exception", cnfe);
            throw new ConfigurationException(cnfe);
        } catch (NamingException ne) {
			log.error("naming exception", ne);
            throw new ConfigurationException(ne);
        }
    }
    
    
	/**
	 * Internal class to handle service information
	 */
    private static class ServiceEntry {
        
        public String id;
        public String klass;
        public String type;
        public Element configuration;
        
        public ServiceEntry(String _id, String _klass, String _type, Element _params) {
            id = _id;
            klass = _klass;
            type = _type;
            configuration = _params;
        }
    }
    
}
