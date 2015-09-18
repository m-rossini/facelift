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
 * Created on May 2, 2005
 */
package br.com.auster.facelift.queries.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.queries.builder.QueryFactory;
import br.com.auster.facelift.queries.dao.QueryDAO;
import br.com.auster.facelift.queries.interfaces.QueryConstants;
import br.com.auster.facelift.queries.interfaces.QueryException;
import br.com.auster.facelift.queries.interfaces.QueryManager;
import br.com.auster.facelift.queries.model.ColumnObject;
import br.com.auster.facelift.queries.model.Query;
import br.com.auster.facelift.queries.model.ViewObject;
import br.com.auster.facelift.services.ConfigurationException;

/**
 * Implementation of the Query Manager service. Handles all operations using a Map to hold information of the configured tables,
 *   and a DAO implementation to execute the SQL statments.
 * 
 * @author framos
 * @version $Id: QueryManagerImpl.java 181 2005-12-16 11:15:30Z framos $
 */
public class QueryManagerImpl implements QueryManager {

    
    
	
	public static final String CONFIGURATION_DRIVER_CONNECTION 		= "iq.jdbc.driver";
	public static final String CONFIGURATION_URL_CONNECTION 		= "iq.jdbc.url";
	public static final String CONFIGURATION_USERNAME_CONNECTION	= "iq.jdbc.username";
	public static final String CONFIGURATION_PASSWORD_CONNECTION	= "iq.jdbc.password";
	
	public static final String CONFIGURATION_JNDI_NAME 				= "iq.jndi.path";
	
    // ----------------------
    // Instance variables
    // ----------------------

	private Map views;
	private Map connections;
	private static final Logger log = LogFactory.getLogger(QueryManagerImpl.class);
    
    
	
    // ----------------------
    // Constructors
    // ----------------------
	
    public QueryManagerImpl() {
		views = new HashMap();
		connections = new HashMap();
    }
    
    
    public Collection listViews() {
        return views.values();
    }

	
	
    // ----------------------
    // Public methods
    // ----------------------
	
	/**
	 * Inherited from <code>QueryManager</code>
	 */
    public ViewObject loadViewDetails(String _name) {
		log.debug("returning details for table '" + _name + "'");
        return (ViewObject) views.get(_name);
    }

	/**
	 * Inherited from <code>QueryManager</code>
	 */
    public Query createQuery(ViewObject _object) throws QueryException {
		Connection conn = null;
		try {
			conn = openConnection(_object);
			return QueryFactory.createQuery(conn, _object);
		} catch (SQLException sqle) {
			throw new QueryException("SQL error while creating query", sqle);
		} catch (NamingException ne) {
			throw new QueryException("SQL error while creating query", ne);
		} finally {
			closeConnection(conn);
		}
    }

	/**
	 * Inherited from <code>QueryManager</code>
	 */
    public Collection executeQuery(Query _query) throws QueryException {
        QueryDAO dao = new QueryDAO();
        Connection conn = null;
		log.debug("executing query");
        try { 
            conn = openConnection(_query.getSourceView());
            String sql = _query.getSQLStatement();
			log.debug("query built : " + sql);
            return dao.executeSQL(conn, sql);
        } catch (NamingException ne) {
            throw new QueryException("Naming Exception", ne);
        } catch (SQLException sqle) {
            throw new QueryException("SQL Exception", sqle);
        } finally {
            closeConnection(conn);
        }
    }

	/**
	 * Inherited from <code>Service</code>
	 */
    public void init(Element _configuration) throws ConfigurationException {
		// configuring datasources
		connections.clear();
		log.info("Configuring interactive query service");
		try {
			String dbfile = DOMUtils.getAttribute(_configuration, "datasource-config", true);
			configureDatasources(DOMUtils.openDocument(IOUtils.openFileForRead(dbfile, DOMUtils.getBooleanAttribute(_configuration, "encrypted"))));
		} catch (IOException ioe) {
			throw new ConfigurationException("error parsing datasource file", ioe);
		} catch (SAXException saxe) {
			throw new ConfigurationException("error parsing datasource file", saxe);
		} catch (ParserConfigurationException pce) {
			throw new ConfigurationException("error parsing datasource file", pce);
		} catch (GeneralSecurityException gse) {
			throw new ConfigurationException("error parsing datasource file", gse);
		}
		// configuring view definition files
		views.clear();
        NodeList list = DOMUtils.getElements(DOMUtils.getElement(_configuration, "views", true), "view-definition");
        for (int i=0; i < list.getLength(); i++) {
            Element node = (Element) list.item(i);
			String filename = DOMUtils.getText(node).toString();
			try {
				String viewName = DOMUtils.getAttribute(node, "name", true);
				configureViews(viewName, DOMUtils.openDocument(IOUtils.openFileForRead(filename, DOMUtils.getBooleanAttribute(node, "encrypted"))));
			} catch (SAXException saxe) {
				throw new ConfigurationException("error parsing configuration file", saxe);
			} catch (ParserConfigurationException pce) {
				throw new ConfigurationException("error parsing configuration file", pce);
			} catch (IOException ioe) {
				throw new ConfigurationException("error parsing configuration file", ioe);
			} catch (GeneralSecurityException gse) {
				throw new ConfigurationException("error parsing configuration file", gse);
			} catch (QueryException qe) {
				throw new ConfigurationException("error parsing configuration file", qe);
			}
        }
    }
	
	
	
    // ----------------------
    // Private methods
    // ----------------------
	
	/**
	 * Creates a connection to the persistence resource, to be used by the DAO implementation 
	 */
    private Connection openConnection(ViewObject _view) throws SQLException, NamingException {
        Map connProps = (Map) connections.get(_view.getDataSource());
        
        try {
			if (connProps.containsKey(CONFIGURATION_DRIVER_CONNECTION)) {
	            Class.forName((String) connProps.get(CONFIGURATION_DRIVER_CONNECTION));
				log.debug("opening database connection to " + connProps.get(CONFIGURATION_URL_CONNECTION) + 
						  " with driver " + connProps.get(CONFIGURATION_DRIVER_CONNECTION));
	            return DriverManager.getConnection((String) connProps.get(CONFIGURATION_URL_CONNECTION),
	                                               (String) connProps.get(CONFIGURATION_USERNAME_CONNECTION),
	                                               (String) connProps.get(CONFIGURATION_PASSWORD_CONNECTION));
			} else {
				Context ctx = new InitialContext(new Hashtable(connProps));
				log.debug("looking up for datasource at " + (String) connProps.get(CONFIGURATION_JNDI_NAME));
				DataSource ds = (DataSource) ctx.lookup((String) connProps.get(CONFIGURATION_JNDI_NAME));
				return ds.getConnection();
			}
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("cannot find driver class", cnfe);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException("driver not configured properly", npe);
        }
    }
    
	/**
	 * Closes the previously opened connection 
	 */
    private void closeConnection(Connection _conn) {
        try {			
            if (_conn != null) {
                _conn.close();
            }
			log.debug("database connection closed.");
        } catch (SQLException sqle) {
            log.error("error closing connection", sqle);
        }
    }
    
	/**
	 * Reads and stores the configured information for a single table
	 */
    private void configureViews(String _name, Element _configuration) throws QueryException {
        ViewObject view = new ViewObject(_name);
        log.debug("configuring table " + view.getName());
		view.setDisplayCaption(DOMUtils.getAttribute(_configuration, "display-name", true));
		view.setDataSource(DOMUtils.getAttribute(_configuration, "datasource", true));
		
		configureColumns(view, DOMUtils.getElement(_configuration, "columns", true));
		configureRelations(view, DOMUtils.getElement(_configuration, "relations", false));
		
        views.put(view.getName(), view);
        log.info("table " + view.getName() + " added successfully");
    }
    
	/**
	 * Reads and stores the configured information for database connections
	 */
    private void configureDatasources(Element _configuration) {
        
        NodeList list = DOMUtils.getElements(_configuration, "datasource");
        for (int i=0; i < list.getLength(); i++) {
            Element node = (Element) list.item(i);
			String dsName = DOMUtils.getAttribute(node, "name", true);
			Map props = new HashMap();
			NodeList propList = DOMUtils.getElements(node, "property");
	        for (int j=0; j < propList.getLength(); j++) {
	            Element propNode = (Element) propList.item(j);
				String propName = DOMUtils.getAttribute(propNode, "name", true);
				String text = DOMUtils.getText(propNode).toString();
				props.put(propName, text);
				log.debug("found connection property : " + propName + " ->" + text);
	        }			
            connections.put(dsName, props);
			log.debug("finished configuration of datasource :" + dsName);
        }
    }
    
	/**
	 * Decomposes the configured columns of a table, to store that information into the table definition object
	 */
    private void configureColumns(ViewObject _view, Element _configuration) {
        NodeList list = DOMUtils.getElements(_configuration, "column");
        for (int i=0; i < list.getLength(); i++) {
            Element node = (Element) list.item(i);
            ColumnObject col = new ColumnObject(DOMUtils.getAttribute(node, "name", true));
			String displayName = DOMUtils.getAttribute(node, "display-name", false);
			if ((displayName == null) || (displayName.trim().length() <= 0)) {
				displayName = col.getName();
			}
            col.setDisplayCaption(displayName);
            col.setOwnerTable(DOMUtils.getAttribute(node, "table", true));
			col.setVisible(DOMUtils.getBooleanAttribute(node, "visible"));
            String type = DOMUtils.getAttribute(node, "type", true);
            if (! QueryConstants.COLUMN_TYPES.containsKey(type)) {
				type = QueryConstants.COLUMN_TYPE_VARCHAR;
				log.warn("column type " + type + " not defined. Defaulting to varchar type");
            } 
			col.setType(type);
			log.debug("added column " + col.getName() + " of type " + col.getType());
			_view.addColumn(col);
        }
    }

    private void configureRelations(ViewObject _view, Element _configuration) throws QueryException {
		
		if (_configuration == null) { return; }
        NodeList list = DOMUtils.getElements(_configuration, "relation");
        for (int i=0; i < list.getLength(); i++) {
            Element node = (Element) list.item(i);
			String fromCol = DOMUtils.getAttribute(node, "from-table", true) + "." + DOMUtils.getAttribute(node, "from-column", true);
			String toCol   = DOMUtils.getAttribute(node, "to-table", true)   + "." + DOMUtils.getAttribute(node, "to-column", true);
			_view.addRelation(new ViewObject.Relationship(_view.getColumn(fromCol), _view.getColumn(toCol)));
        }
    }
	
}
