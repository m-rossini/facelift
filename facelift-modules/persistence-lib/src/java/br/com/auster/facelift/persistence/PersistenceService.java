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
 * Created on Mai 10, 2005
 */
package br.com.auster.facelift.persistence;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.services.Service;
import br.com.auster.facelift.services.ServiceLocator;
import br.com.auster.facelift.services.plugin.PluginContext;
import br.com.auster.facelift.services.plugin.PluginService;

/**
 * Defines the basic opearations a service, which will apply operations of data persistence, must implement. Also, creates the
 * 	capability of such services to have plugin points.
 * <P> 
 * 
 * @author framos
 * @version $Id$
 */
public abstract class PersistenceService implements Service {

	
	/**
	 * Creates new connections to the underlying persistence mechanism. It abstracts the process involved in the creation
	 * of database connections, hibernate sessions, etc.
	 * <P> 
	 * 
	 * @return the resource connection object
	 * @throws PersistenceResourceAccessException if could not create a new connection object to the persistence resource
	 */
	public abstract Object openResourceConnection() throws PersistenceResourceAccessException;

	/**
	 * Creates new connections to the underlying persistence mechanism. It abstracts the process involved in the creation
	 * of database connections, hibernate sessions, etc.
	 * <P> 
	 * The use of a map of properties is available in case there is need to access runtime information when creating this 
	 * connection object.
	 * <P>
	 *   
	 * @return the resource connection object
	 * @param _properties the map of properties
	 * @throws PersistenceResourceAccessException if could not create a new connection object to the persistence resource
	 */
	public abstract Object openResourceConnection(Map _properties) throws PersistenceResourceAccessException;
	
	/**
	 * For environments  which do not have automatic transaction management (like EJBs), this method is available to start,
	 * 	when configured, a transaction in some transaction manager service. If there is no transaction manager, or for any
	 *  other reason a transaction could not be started, then <code>null</code> is returned. 
	 * 
	 * @param _connection the current connection 
	 * @return a transaction object, or <code>null</code> when it was not possible to create it
	 * @throws PersistenceResourceAccessException  if could not use the persistence connection object to create the transaction
	 */
	public abstract Object beginTransaction(Object _connection) throws PersistenceResourceAccessException;
	
	/**
	 * Closes a previously created connection object. This method should execute all finishing and cleanup operations like
	 * committing transactions and releasing resources.
	 * <P>
	 *   
	 * @param _connection the connection object to be closed
	 * @throws PersistenceResourceAccessException if could not close or execute any of the cleanup operations
	 */
	public abstract void closeResourceConnection(Object _connection) throws PersistenceResourceAccessException;

	/**
	 * Closes a previously created connection object. This method should execute all finishing and cleanup operations like
	 * committing transactions and releasing resources.
	 * <P>
	 * Also, if the specified transaction object is not <code>null</code>, then it is committed. If, when committing it an
	 * 	exception is raised,then it will try to rollback the transaction and return the exception to the calling system.
	 *   
	 * @param _connection the connection object to be closed
	 * @param _transaction the transaction controller object
	 * @throws PersistenceResourceAccessException if could not close or execute any of the cleanup operations
	 */
	public abstract void closeResourceConnection(Object _connection, Object _transaction) throws PersistenceResourceAccessException;
	
	/**
	 * Closes a previously created connection object. This method should execute all finishing and cleanup operations like
	 * committing transactions and releasing resources.
	 * <P>  
	 * The use of a map of properties is available in case there is need to access runtime information when creating this 
	 * connection object.
	 * <P>
	 *   
	 * @param _connection the connection object to be closed
	 * @param _properties the map of properties
	 * @throws PersistenceResourceAccessException if could not close or execute any of the cleanup operations
	 */
	public abstract void closeResourceConnection(Object _connection, Map _properties) throws PersistenceResourceAccessException;	
	
	/**
	 * Closes a previously created connection object. This method should execute all finishing and cleanup operations like
	 * committing transactions and releasing resources.
	 * <P>  
	 * The use of a map of properties is available in case there is need to access runtime information when creating this 
	 * connection object.
	 * <P>
	 * Also, if the specified transaction object is not <code>null</code>, then it is committed. If, when committing it an
	 * 	exception is raised,then it will try to rollback the transaction and return the exception to the calling system.
	 *   
	 * @param _connection the connection object to be closed
	 * @param _properties the map of properties
	 * @param _transaction the transaction controller object
	 * @throws PersistenceResourceAccessException if could not close or execute any of the cleanup operations
	 */
	public abstract void closeResourceConnection(Object _connection, Map _properties, Object _transaction) throws PersistenceResourceAccessException;

	/**
	 * Executes the commit operation of the transaction. If, for some reason it could not commit it, then the rollback operation
	 * 	will be executed.
	 * 
	 * @param _transaction the transaction controller object
	 * @throws PersistenceResourceAccessException any exception raised during transaction commit/rollback operations
	 */
	public abstract void commitTransaction(Object _transaction) throws PersistenceResourceAccessException;

	/**
	 * Rolls-back the executed operations for this transaction. 
	 *  
	 * @param _transaction the transaction controller object
	 * @throws PersistenceResourceAccessException any exception raised during transaction commit/rollback operations
	 */
	public abstract void rollbackTransaction(Object _transaction) throws PersistenceResourceAccessException;
	
	/**
	 * Triggers the execution of all configured plugins, according to the specified plugin point and condition value.
	 * This method is be called whenever the service class requires that plugins are executed. 
	 * <P>   
	 * The decision on wether or not validate the condition value is up to the plugin service, which will be based on
	 * its configuration file.
	 * <P>  
	 * 
	 * @param _pluginToken the plugin point identifier
	 * @param _operationValue the value used as condition for this plugin point 
	 * @param _parameters the list of parameters to be passed to each plugin triggered
	 */
	public void callPlugins(String _pluginToken, String _conditionValue, List _parameters) {
		ServiceLocator locator = ServiceLocator.getInstance();
        PluginService service = locator.getPluginService();
        PluginContext context = new PluginContext();
        context.setPluginPoint(_pluginToken);
		context.setConditionValue(_conditionValue);
        context.setExecutionParameters(_parameters);
        service.checkPlugins(context);
	}
	
	/**
	 * Initializes the configuration for the presistence service
	 * <P>
	 * Inherited from <code>Service</code>
	 */
	public abstract void init(Element _configuration) throws ConfigurationException;
}
