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
 * Created on Feb 28, 2005
 */
package br.com.auster.facelift.services.plugin.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.services.plugin.FaceliftPlugin;
import br.com.auster.facelift.services.plugin.PluginContext;
import br.com.auster.facelift.services.plugin.PluginExecutor;
import br.com.auster.facelift.services.plugin.PluginService;

/**
 * Ths implementation of the plugin service, where all its configuration is stored in a
 * 	XML file. This configuration is loaded at startup time.
 * <P>
 * 
 * @author framos
 * @version $Id$
 */
public class XMLPluginService implements PluginService {
    
	
    
    // ----------------------
    // Instance variables
    // ----------------------

	private Map plugins;
    private String executorClass;
    private Logger log = LogFactory.getLogger(XMLPluginService.class);
    
    
    
    // ----------------------
    // Constructors
    // ----------------------
	
    public XMLPluginService() throws ConfigurationException {
        super();
    }

	
	
    // ----------------------
    // Public methods
    // ----------------------
	
	/**
     * Inherited from <code>PluginService</code>
	 */
    public synchronized void checkPlugins(PluginContext _context) {
        if (plugins == null) {
            throw new IllegalStateException("plugin service not configured");
        }
        if (_context == null) {
            throw new IllegalArgumentException("cannot execute plugins without a pre-defined context");
        }
        XMLPluginPoint pp = new XMLPluginPoint(_context.getPluginPoint(), _context.getConditionValue());
		log.info("looking up for plugins @ '" + _context.getPluginPoint() + "' with value = '" + _context.getConditionValue() + "'");
        List pluginList = (List) plugins.get(pp);
        if (pluginList == null) {
			log.debug("no plugins found");
            return;
        }
        try {
            Collections.sort(pluginList);
            for (Iterator iterator = pluginList.iterator(); iterator.hasNext(); ) {
                FaceliftPlugin plugin = initPlugin((XMLPluginDefinition)iterator.next(), _context);
                if (plugin == null) {
                    continue;
                }
				try {
		            log.debug("running plugin '" + plugin.getClass() + "'");
	                PluginExecutor executor = newExecutor();
	                executor.execute(plugin);
				} catch (ConfigurationException ce) {
					throw ce;
				} catch (Exception e) {
					// do not stop just because a plugin raised an exception
					log.error("caught exception while executing plugin", e);
				}
            }
        } catch (ConfigurationException ce) {
            log.error("plugins could not be called due to configuration exception", ce);
        }
    }

	/**
     * Inherited from <code>PluginService</code>
	 */
    public PluginExecutor newExecutor() throws ConfigurationException {
        if (executorClass == null) {
            throw new ConfigurationException("executor class not set");
        }
        try {
            return (PluginExecutor) Class.forName(executorClass).newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new ConfigurationException("executor class not found", cnfe);
        } catch (InstantiationException ie) {
            throw new ConfigurationException("could not create instance of executor", ie);
        } catch (IllegalAccessException iae) {
            throw new ConfigurationException("could not call executor class default constructor", iae);
        }
    }

	/**
     * Inherited from <code>Service</code>
	 */
    public void init(Element _configuration) throws ConfigurationException {
        try {
			log.info("Configuring plugin service");
            executorClass = DOMUtils.getAttribute(_configuration, "executor", true);
			log.debug("\texecutor='" + executorClass + "'");
            NodeList list = DOMUtils.getElements(DOMUtils.getElement(_configuration, "plugins", true), "plugin");
            plugins = new HashMap();
            for (int i=0; i < list.getLength(); i++) {
                Element pluginConf = (Element) list.item(i);
                String value = DOMUtils.getAttribute(pluginConf, "trigger-value", false);
                if ((value != null) && (value.trim().length() <= 0)) { 
                    value = null;
                }
                XMLPluginPoint pp = new XMLPluginPoint(DOMUtils.getAttribute(pluginConf, "plugin-point", true), value);
                List pluginList = (List) plugins.get(pp);
                if (pluginList == null) {
                    pluginList = new ArrayList();
                    plugins.put(pp, pluginList);
                }
				log.info("configuring plugin @ '" + pp.pluginPoint + "' with value '" + pp.value + "'");
                pluginList.add(createPluginDefinition(pluginConf));
            }
        } catch (Exception e) {
            throw new ConfigurationException("could not configure XML plugin service", e);
        }
    }

	
	
    // ----------------------
    // Protected & Private methods
    // ----------------------
	
	/**
	 * Initializes a plugin implementation prior to sending it to a plugin executor object to be runned. It instantiates
	 * 	the plugin class, sets the configured parameters and the current context. If any of these operations could not 
	 *  be performed correctly, it will be logged and <code>null</code> is returned.
	 *  <P>
	 */
    protected FaceliftPlugin initPlugin(XMLPluginDefinition _pluginDefn, PluginContext _context) {
        if (_pluginDefn == null) {
            log.error("no plugin definition found");
            return null;
        }
        try {
            FaceliftPlugin plugin = (FaceliftPlugin) Class.forName(_pluginDefn.className).newInstance();
            plugin.setConfigurationParameters(_pluginDefn.parameters);
            plugin.setContext(_context);
            return plugin;
        } catch (ClassNotFoundException cnfe) {
            log.error("plugin class not found", cnfe);
        } catch (InstantiationException ie) {
            log.error("cannot instantiate plugin class", ie);
        } catch (IllegalAccessException iae) {
            log.error("cannot access plugin methods", iae);
        }
        return null;
    }	
	
	/**
	 * Used during service configuration time. It create instances of the inner class <code>XMLPluginDefintion</code>
	 * 	which will store all plugin information needed when the plugin is triggered. That means, its class name, all 
	 * 	configuration parameters and its priority. 
	 */
    private XMLPluginDefinition createPluginDefinition(Element _configuration) {
        
        XMLPluginDefinition def = new XMLPluginDefinition();
        def.className = DOMUtils.getAttribute(_configuration, "class-impl", true);
        log.info("plugin class = '" + def.className + "'");
        String priority = DOMUtils.getAttribute(_configuration, "priority", false);
        if ((priority == null) || (priority.length() <= 0)) {
            priority = "99";
        }
        try {
            def.priority = Math.abs(Integer.parseInt(priority)) % 100;
        } catch (NumberFormatException nfe) {
            log.warn("Priority overwritten to 99 due to invalid configuration : " + priority);
            def.priority = 99;
        }
        log.debug("priority = " + def.priority);
        def.parameters = new HashMap();
        Element paramElement = DOMUtils.getElement(_configuration, "parameters", false);
        if (paramElement != null) {
            NodeList parameters = DOMUtils.getElements(paramElement, "parameter");
            for (int i=0; i < parameters.getLength(); i++) {
                Element param = (Element) parameters.item(i);
                def.parameters.put(DOMUtils.getAttribute(param, "name", true),
                                   DOMUtils.getText(param).toString());            
            }
        }
        log.debug("parameter list = " + def.parameters);
        return def;
    }
    
    
	/**
	 * Use as <strong>key</strong> to identify the list of plugins related to a 
	 * 	specific context, taking into consideration no only the plugin point but also the
	 * 	condition value. 
	 */
    protected static class XMLPluginPoint implements Comparable {
        
        public String pluginPoint;
        public String value;
        
        public XMLPluginPoint(String _pluginPoint, String _value) {
            pluginPoint = _pluginPoint;
            value = _value;
        }
        
        public boolean equals(Object _other) {
            return (compareTo(_other) == 0);
        }
        
        public int hashCode() {
            int result = 17;
            result = 37*result + (this.pluginPoint != null ? this.pluginPoint.hashCode() : 0 );
            result = 37*result + (this.value != null ? this.value.hashCode() : 0 );
            return result;
        }
        
        public int compareTo(Object _other) {
            XMLPluginPoint other = (XMLPluginPoint) _other;
            int compare = 0;
            // comparing plugin point
            if ((this.pluginPoint != null) && (other.pluginPoint != null)) {
                compare = this.pluginPoint.compareTo(other.pluginPoint);
            } else if ((this.pluginPoint == null) && (other.pluginPoint == null)) {
                // do nothing... asuming both instances are equal
            } else {
                return 1;
            }
            if (compare != 0) { return compare; }
            // comparing value 
            if ((this.value != null) && (other.value != null)) {
                compare = this.value.compareTo(other.value);
            } else if ((this.value == null) && (other.value == null)) {
                // do nothing... asuming both instances are equal
            } else {
                return 1;
            }
            return compare;
        }
    }
 
    /**
     * Holds all information related to plugin configuration. A list of these objects are 
     * 	stored by plugin point, to identify all plugins associated to that plugin point. 
     */
    protected static class XMLPluginDefinition implements Comparable {
        
        
        public String className;
        public int priority;
        public Map parameters;

        
        public boolean equals(Object _other) {
            XMLPluginDefinition other = (XMLPluginDefinition) _other;
            return this.className.equals(other.className);
        }
        
        public int hashCode() {
            int result = 17;
            result = 37*result + (this.className != null ? this.className.hashCode() : 0 );
            return result;
        }
        
        public int compareTo(Object _other) {
            XMLPluginDefinition other = (XMLPluginDefinition) _other;
            return (other.priority - this.priority);
        }
        
        public String toString() {
            return "plugin : '" + this.className + "' @priority=" + this.priority;
        }
        
    }
}
