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
package br.com.auster.facelift.requests.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.interfaces.RequestManager;
import br.com.auster.facelift.requests.interfaces.RequestManagerException;
import br.com.auster.facelift.requests.model.InputFile;
import br.com.auster.facelift.requests.model.OutputFile;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;
import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.services.PluggableService;
import br.com.auster.persistence.PersistenceResourceAccessException;
import br.com.auster.persistence.PersistenceService;

/**
 * The current implmementation of the Request Manager service.
 * 
 * @author framos
 * @version $Id: RequestManagerImpl.java 244 2006-09-13 15:51:47Z framos $
 */
public class RequestManagerImpl extends PluggableService implements RequestManager { 

	
    
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
        Session session = null;
        try {
            session = (Session) persistence.openResourceConnection();
            Criteria criteria = session.createCriteria(Request.class);
            if (_criteria.getLabel() != null) {
                criteria.add( Expression.ilike("label", String.valueOf(_criteria.getLabel()), MatchMode.ANYWHERE) );
                log.debug("filtering search by label=" + _criteria.getLabel());
            }
            if (_criteria.getStartDate() != null) {
                criteria = criteria.createCriteria("trails");
                criteria.add( Expression.ge("trailDate", _criteria.getStartDate()) );
                log.debug("filtering search by startdate >=" + _criteria.getStartDate());
            }
            if (_criteria.getEndDate() != null) {
                if (_criteria.getStartDate() == null) {
                    criteria = criteria.createCriteria("trails");
                }
                log.debug("filtering search by startdate <=" + _criteria.getEndDate());
                criteria.add( Expression.le("trailDate", _criteria.getEndDate()) );
            }
            return criteria.list();
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session);
        }
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public Request loadRequestDetail(long _requestId) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
        try {
            session = (Session) persistence.openResourceConnection();
            Criteria criteria = session.createCriteria(Request.class);
            criteria.add( Expression.eq("requestId", new Long(_requestId)) );
            log.debug("loading details for request id=" + _requestId);
            return (Request) criteria.uniqueResult();
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session);
        }
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public int createMultipleRequests(Set _requests) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
		Object t = null;
		if (_requests == null) {
			throw new IllegalArgumentException("list of requests cannot be null");
		}
		ArrayList execParameters = null;
        try {
            session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			Iterator iterator = _requests.iterator();
			while (iterator.hasNext()) {
				createSingleRequest(session, (Request)iterator.next());
			}
			// calling plugins
			execParameters = new ArrayList();
			execParameters.add(_requests);
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session, t);
        }
		log.debug("processing requests created. calling plugins now");
		this.callPlugins(RequestManager.PLUGINPOINT_CREATE_MULTIPLE_REQUEST, null, execParameters );
		return _requests.size();
    }

    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
    public long createRequest(Request _request) throws RequestManagerException, PersistenceResourceAccessException {
        
        Session session = null;
		Object t = null;
        // checking if request was well created
        if ((_request.getInputFiles() == null) || (_request.getInputFiles().size() <= 0)) {
            throw new RequestManagerException("new requests must have at least one input file");
        }
		ArrayList execParameters = null;
        try {
            session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			createSingleRequest(session, _request);
	        log.debug("Created request with id =" + _request.getRequestId());
			// calling plugins
			execParameters = new ArrayList();
			execParameters.add(_request);
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session, t);
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
        Session session = null;
		Object t = null;
		Request request = null;
        try {
            session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			request = (Request) session.load(Request.class, new Long(_requestId), LockMode.UPGRADE);
			// setting two-way relationship
			_trail.setRequest(request);
			request.getTrails().add(_trail);
			// adding output files if exist
	        if ((_files != null) && (_files.length > 0)) {
	            _trail.setOutputFiles(Arrays.asList(_files));
	        }
			// updating request status
	        request.setLatestStatus(_trail.getStatus());
			// saving trail info & request updates
            session.update(request);
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
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
		throw new UnsupportedOperationException("method not implemented in the class");
	}
	
    /**
     * Implements the business method defined in the <code>RequestManager</code> interface
     */
	public List findOutputFiles(List _requestIdList) throws RequestManagerException, PersistenceResourceAccessException {
		throw new UnsupportedOperationException("method not implemented in the class");
	}	
	
	public void init(Element _configuration) throws ConfigurationException {
		try {
			log.info("Configuring Processing Request service");
			String klass = DOMUtils.getAttribute(_configuration, "persistence", true);
			log.info("persistence class =" + klass);
			persistence = (PersistenceService) Class.forName(klass).newInstance();
			this.init(DOMUtils.getElement(_configuration, "persistence-configuration", true));
		} catch (InstantiationException ie) {
			throw new ConfigurationException("could not initialize persistence service", ie);
		} catch (IllegalAccessException iae) {
			throw new ConfigurationException("could not create persistence service", iae);
		} catch (ClassNotFoundException cnfe) {
			throw new ConfigurationException("could not find persistence service class", cnfe);
		}
	}

    
    // ----------------------
    // Private methods
    // ----------------------
    
	/**
	 * Creates the specified request into the database, along with all its associated maps & lists
	 */
	private void createSingleRequest(Session _session, Request _request) throws HibernateException {
        _session.save(_request);
		log.debug("request created. Iterating over its souce files");
        // iterate over input files to insert : not cascading due to update problems
        Iterator iterator = _request.getInputFiles().iterator();
        while (iterator.hasNext()) {
            _session.save((InputFile)iterator.next());
        }
		log.debug("source files for new request were saved");
    }

}
