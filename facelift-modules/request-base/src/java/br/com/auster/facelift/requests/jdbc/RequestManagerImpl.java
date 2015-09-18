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
package br.com.auster.facelift.requests.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.persistence.PersistenceResourceAccessException;
import br.com.auster.persistence.PersistenceService;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.interfaces.RequestManager;
import br.com.auster.facelift.requests.interfaces.RequestManagerException;
import br.com.auster.facelift.requests.model.OutputFile;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;
import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.services.PluggableService;

/**
 * The current implmementation of the Request Manager service.
 * 
 * @author framos
 * @version $Id: RequestManagerImpl.java 377 2007-08-21 21:06:36Z framos $
 */
public class RequestManagerImpl extends PluggableService implements RequestManager  { 

	
    
    // -------------------------
    // Instance variables
    // -------------------------
	
	protected PersistenceService persistence;
    private Logger log = LogFactory.getLogger(RequestManagerImpl.class);
    
    
    
    // ----------------------
    // Interface methods
    // ----------------------
    
    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public List findRequests(RequestCriteria _criteria) throws RequestManagerException, PersistenceResourceAccessException {
        Connection session = null;
		List resultList = null;
		if (_criteria == null) {
			throw new IllegalArgumentException("cannot search for requests without a filtering criteria");
		}
        try {
            session = (Connection) persistence.openResourceConnection();
			RequestDAO dao = new RequestDAO();
			List listOfIds = dao.selectRequestList(session, _criteria);
			resultList = new ArrayList(listOfIds.size());
			for (Iterator iterator = listOfIds.iterator(); iterator.hasNext(); ) {
				Request request = dao.selectRequest(session, Long.parseLong((String)iterator.next()));
				resultList.add(request);
			}
			return resultList;
        } catch (SQLException sqle) {
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public Request loadRequestDetail(long _requestId) throws RequestManagerException, PersistenceResourceAccessException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            log.debug("loading details for request id=" + _requestId);
			RequestDAO dao = new RequestDAO();
			Request request = dao.selectRequest(session, _requestId);
			request.setInputFiles(dao.selectInputFiles(session, _requestId));
			TrailDAO trailDAO = new TrailDAO();
			request.setTrails(trailDAO.selectRequestTrails(session, _requestId));
			return request;
        } catch (SQLException sqle) {
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public int createMultipleRequests(Set _requests) throws RequestManagerException, PersistenceResourceAccessException {
        Connection session = null;
//		Object t = null;
		if (_requests == null) {
			throw new IllegalArgumentException("list of requests cannot be null");
		}
		ArrayList execParameters = null;
        try {
//		    this transaction will be dealt with in the WebRequestImpl service
//			t = persistence.beginTransaction(session);
            session = (Connection) persistence.openResourceConnection();
			RequestDAO dao = new RequestDAO();
			dao.createMultipleRequest(session, _requests);
			// calling plugins
			execParameters = new ArrayList();
			execParameters.add(_requests);
        } catch (SQLException sqle) {
//			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
//			persistence.closeResourceConnection(session, t);
			persistence.closeResourceConnection(session);
        }
		log.debug("processing requests created. calling plugins now");
		this.callPlugins(RequestManager.PLUGINPOINT_CREATE_MULTIPLE_REQUEST, null, execParameters );
		return _requests.size();
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public long createRequest(Request _request) throws RequestManagerException, PersistenceResourceAccessException {

        Connection session = null;
		Object t = null;
		if (_request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		ArrayList execParameters = null;
        try {
//		    this transaction will be dealt with in the WebRequestImpl service
			t = persistence.beginTransaction(session);
            session = (Connection) persistence.openResourceConnection();
			RequestDAO dao = new RequestDAO();
			dao.createRequest(session, _request);
			execParameters = new ArrayList();
			execParameters.add(_request);
        } catch (SQLException sqle) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session, t);
			persistence.closeResourceConnection(session);
        }
		log.debug("processing requests created. calling plugins now");
		this.callPlugins(RequestManager.PLUGINPOINT_CREATE_SINGLE_REQUEST, null, execParameters );

		return _request.getRequestId();

    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public Request updateRequestStatus(long _requestId, Trail _trail) throws RequestManagerException, PersistenceResourceAccessException {
        return updateRequestStatus(_requestId, _trail, null);
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public Request updateRequestStatus(long _requestId, Trail _trail, OutputFile[] _files) throws RequestManagerException, PersistenceResourceAccessException {
        log.debug("request with id =" + _requestId + " had its status updated to " + _trail.getStatus());
        Connection session = null;
		Object t = null;
		Request request = null;
        try {
			t = persistence.beginTransaction(session);
            session = (Connection) persistence.openResourceConnection();
			RequestDAO dao = new RequestDAO();

			// adding output files if exist
	        if ((_files != null) && (_files.length > 0)) {
	            _trail.setOutputFiles(Arrays.asList(_files));
	        }
			// updating request status
//			request = dao.selectRequest(session, _requestId);
			request = new Request(); 
			request.setRequestId(_requestId);
	        request.setLatestStatus(_trail.getStatus());
			
			dao.updateRequestStatus(session, _requestId, _trail);
			
        } catch (SQLException sqle) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session, t);			
        }
		ArrayList execParameters = new ArrayList();
		execParameters.add(request);
		execParameters.add(_trail);
		this.callPlugins(RequestManager.PLUGINPOINT_UPDATE_STATUS_REQUEST, null, execParameters );
		return request;
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
	public List findOutputFiles(long _requestId) throws RequestManagerException, PersistenceResourceAccessException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            log.debug("loading generated files for request id=" + _requestId);
			OutputFileDAO dao = new OutputFileDAO();
			return dao.selectRequestOutputFiles(session, _requestId);
        } catch (SQLException sqle) {
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
	}
	
    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
	public List findOutputFiles(List _requestIdList) throws RequestManagerException, PersistenceResourceAccessException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            log.debug("loading generated files for " + _requestIdList.size() + " requests");
			OutputFileDAO dao = new OutputFileDAO();
			return dao.selectRequestOutputFiles(session, _requestIdList);
        } catch (SQLException sqle) {
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
	}
		
	
	public void init(Element _configuration) throws ConfigurationException {
		try {
			log.info("Configuring Processing Request service");
			String klass = DOMUtils.getAttribute(_configuration, "persistence", true);
			log.info("persistence class =" + klass);
			persistence = (PersistenceService) Class.forName(klass).newInstance();
			persistence.init(DOMUtils.getElement(_configuration, "persistence-configuration", true));
        } catch (br.com.auster.persistence.ConfigurationException ce) {
            throw new ConfigurationException("could not initialize persistence service", ce);
		} catch (InstantiationException ie) {
			throw new ConfigurationException("could not initialize persistence service", ie);
		} catch (IllegalAccessException iae) {
			throw new ConfigurationException("could not create persistence service", iae);
		} catch (ClassNotFoundException cnfe) {
			throw new ConfigurationException("could not find persistence service class", cnfe);
		}
	}
	
	
}
