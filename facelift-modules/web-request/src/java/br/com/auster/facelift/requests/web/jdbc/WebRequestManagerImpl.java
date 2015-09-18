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
package br.com.auster.facelift.requests.web.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.interfaces.RequestManagerException;
import br.com.auster.facelift.requests.jdbc.RequestDAO;
import br.com.auster.facelift.requests.jdbc.RequestManagerImpl;
import br.com.auster.facelift.requests.model.OutputFile;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;
import br.com.auster.facelift.requests.web.interfaces.ResumeCriteria;
import br.com.auster.facelift.requests.web.interfaces.WebRequestCriteria;
import br.com.auster.facelift.requests.web.interfaces.WebRequestLifeCycle;
import br.com.auster.facelift.requests.web.interfaces.WebRequestManager;
import br.com.auster.facelift.requests.web.model.BundleFile;
import br.com.auster.facelift.requests.web.model.NotificationEmail;
import br.com.auster.facelift.requests.web.model.WebRequest;
import br.com.auster.persistence.FetchCriteria;
import br.com.auster.persistence.PersistenceResourceAccessException;

/**
 * The current implementation of the Web Request service
 * 
 * @author framos
 * @version $Id: WebRequestManagerImpl.java 372 2007-07-24 19:34:39Z gbrandao $
 */
public class WebRequestManagerImpl extends RequestManagerImpl implements WebRequestManager {

	// -------------------------
	// Instance variables
	// -------------------------

	private Logger	log	= LogFactory.getLogger(WebRequestManagerImpl.class);

	// -------------------------
	// Public methods
	// -------------------------

	/**
	 * Implementing interface method
	 */
	public long createWebRequest(WebRequest _request) throws RequestManagerException,
	    PersistenceResourceAccessException {
		Connection session = null;
		Object t = null;
		try {
			t = persistence.beginTransaction(session);
			session = (Connection) persistence.openResourceConnection();

			// setting initial status for web request
			_request.setStatus(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED);
			
			// creating list of processing requests
			// CHAMADA DESSE METODO TRANSFERIDA PARA FinishedListener
			//super.createMultipleRequests(_request.getProcessingRequests());

			WebRequestDAO dao = new WebRequestDAO();
			//dao.createRequest(session, _request);
			// chamda alterada para novo metodo que não faz insert no web_request_request
			dao.createTransaction(session, _request);

			WebNotificationDAO notDAO = new WebNotificationDAO();
			notDAO.createNotification(session, _request);

		} catch (SQLException sqle) {
			log.fatal("Error accessing database while trying to create a transaction.", sqle);
			persistence.rollbackTransaction(t);
			throw new RequestManagerException("caught sql exception", sqle);
		} finally {
			persistence.closeResourceConnection(session, t);
		}
		log.debug("created request with id = " + _request.getRequestId());
		ArrayList execParameters = new ArrayList();
		execParameters.add(_request);
		this.callPlugins(WebRequestManager.PLUGINPOINT_CREATE_REQUEST, null, execParameters);
		return _request.getRequestId();
	}
	
	/**
	 * Implementing interface method
	 */
	public void createRequestLink(long _transactionId, Request _request) throws RequestManagerException,
    	PersistenceResourceAccessException {
		
		Connection session = null;
		Object t = null;
		try {
			t = persistence.beginTransaction(session);
			session = (Connection) persistence.openResourceConnection();

			WebRequestDAO dao = new WebRequestDAO();
			dao.createLink(session, _transactionId, _request);

		} catch (SQLException sqle) {
			log.fatal("Error accessing database while trying to create a transaction.", sqle);
			persistence.rollbackTransaction(t);
			throw new RequestManagerException("caught sql exception", sqle);
		} finally {
			persistence.closeResourceConnection(session, t);
		}
		log.debug("created request with id = " + _request.getRequestId());
	}

	/**
	 * Implementing interface method
	 */
	public List findWebRequests(WebRequestCriteria _criteria) throws RequestManagerException,
	    PersistenceResourceAccessException {
		return findWebRequests(_criteria, null);
	}

	/**
	 * Implementing interface method
	 */
	public List findWebRequests(WebRequestCriteria _criteria, FetchCriteria _fetch)
	    throws RequestManagerException, PersistenceResourceAccessException {
		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();

			WebRequestDAO dao = new WebRequestDAO();
			List resultList = dao.selectWebRequestList(session, _criteria, _fetch);
			Iterator iterator = resultList.iterator();
			while (iterator.hasNext()) {
				WebRequest web = (WebRequest) iterator.next();
                log.debug("Calling  a new web counters ");
				web.setCounters(dao.selectWebRequestProcessesToCounters(session, web.getRequestId()));
			}
			log.debug("About to find reansactions.");
			return resultList;
		} catch (SQLException sqle) {
			log.fatal("Error accessing database while retrieving transaction.", sqle);
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

	public int countWebRequests(WebRequestCriteria _criteria) throws RequestManagerException,
	    PersistenceResourceAccessException {
		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();
			WebRequestDAO dao = new WebRequestDAO();
			log.debug("Abount to count transactions.");
			return dao.countWebRequest(session, _criteria);

		} catch (SQLException sqle) {
			log.error("Error while counting transactions.");
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.facelift.requests.web.interfaces.WebRequestManager#getConsequenceCounters(long)
	 */
	public List getConsequenceCounters(long transactionID) throws RequestManagerException,
	    PersistenceResourceAccessException {
		
		Connection session = null;
		try {
			long st = System.nanoTime();
			session = (Connection) persistence.openResourceConnection();
			WebRequestDAO dao = new WebRequestDAO();
			List results = dao.getConsequenceCounters(session, transactionID);
			long et = System.nanoTime();
			if (log.isDebugEnabled()) {
				log.debug("Total time to get consequence counters for transaction id:" + transactionID + 
						" was:" + ( (et-st) /1000/1000 ) + " milliseconds.");
			}
			return results;

		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

    
    
	/**
     * It responsible to delete a web request.
     * 
     * @param id
     * @throws RequestManagerException
     * @throws PersistenceResourceAccessException
     * @see br.com.auster.facelift.requests.web.interfaces.WebRequestManager#deleteWebRequest(long)
     */
    public void deleteWebRequest(long id) throws SQLException,  RequestManagerException, PersistenceResourceAccessException {
        Connection session = null;

        try {
            session = (Connection) persistence.openResourceConnection();

            WebRequestDAO dao = new WebRequestDAO();
            dao.deleteWebRequest(session, id);

        } catch (SQLException sqle) {
            throw new RequestManagerException("caught JDBC SQL exception", sqle);
        } finally {
            persistence.closeResourceConnection(session);
        }
        
    }

    /**
	 * Implementing interface method
	 */
	public WebRequest loadWebRequestDetail(long _id) throws RequestManagerException,
	    PersistenceResourceAccessException {

		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();

			WebRequestDAO dao = new WebRequestDAO();
			WebRequest request = dao.selectWebRequest(session, _id);
			request.setCounters(dao.selectWebRequestProcessesStatus(session, _id));
			request.setConsequenceCounters(dao.getConsequenceCounters(session, _id));
            
			WebBundleDAO bundleDAO = new WebBundleDAO();
			HashSet set = new HashSet();
			// Alterando o tipo de coleção para manter a ordem de incerção
			List list = new ArrayList(bundleDAO.selectBundleFilesCount(session, _id));
			request.setBundleFiles(list);

			WebNotificationDAO notDAO = new WebNotificationDAO();
			set = new HashSet(notDAO.selectNotificationList(session, _id));
			request.setNotifications(set);
			return request;

		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

	/**
	 * Implementing interface method
	 */
	public List findWebRequestProcesses(long _id, RequestCriteria _criteria)
	    throws RequestManagerException, PersistenceResourceAccessException {
		return findWebRequestProcesses(_id, _criteria, null);
	}

	/**
	 * Implementing interface method
	 */
	public List findWebRequestProcesses(long _id, RequestCriteria _criteria, FetchCriteria _fetch)
	    throws RequestManagerException, PersistenceResourceAccessException {
		return findWebRequestProcesses(_id, _criteria, _fetch, true);
	}

	/**
	 * Implementing interface method
	 */
	public List findWebRequestProcesses(long _id, RequestCriteria _criteria, FetchCriteria _fetch,
	    boolean _loadDetails) throws RequestManagerException, PersistenceResourceAccessException {
		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();
			WebRequestRequestsDAO dao = new WebRequestRequestsDAO();
			List tempList = dao.selectProcRequestList(session, _id, _criteria, _fetch);

			Request r = null;
			List resultList = new ArrayList();
			for (Iterator iterator = tempList.iterator(); iterator.hasNext();) {
				long id = Long.parseLong((String) iterator.next());
				if (_loadDetails) {
					r = super.loadRequestDetail(id);
				} else {
					r = new Request();
					r.setRequestId(id);
				}
				resultList.add(r);
			}
			return resultList;
		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

	/**
	 * Implementing interface method
	 */
	public int countWebRequestProcesses(long _id, RequestCriteria _criteria)
	    throws RequestManagerException, PersistenceResourceAccessException {
		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();

			WebRequestDAO dao = new WebRequestDAO();
			int result = dao.countWebRequestProcesses(session, _id, _criteria);
			log.debug("found " + result + " processing requests in the database");
			return result;
		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

	/**
	 * Implementing interface method
	 */
	public Map getWebRequestCounters(long _id) throws RequestManagerException,
	    PersistenceResourceAccessException {
		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();
			log.debug("counting processing requests for web request " + _id);
			WebRequestDAO dao = new WebRequestDAO();
			return dao.selectWebRequestProcessesStatus(session, _id);
		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

	/**
	 * Implementing interface method
	 */
	public Request updateRequestStatus(long _requestId, Trail _trail)
	    throws RequestManagerException, PersistenceResourceAccessException {
		return updateRequestStatus(_requestId, _trail, null);
	}

	/**
	 * Implementing interface method
	 */
	public Request updateRequestStatus(long _requestId, Trail _trail, OutputFile[] _outputFiles)
	    throws RequestManagerException, PersistenceResourceAccessException {
		Request procRequest = super.updateRequestStatus(_requestId, _trail, _outputFiles);
		// update web request counters & status if needed
		Connection session = null;
		ArrayList execParameters = null;
		try {
			session = (Connection) persistence.openResourceConnection();
			WebRequestDAO dao = new WebRequestDAO();
			WebRequest req = dao.selectWebRequestByProcRequest(session, _requestId, false);
			// building list of attributes to call plugins
			execParameters = new ArrayList();
			execParameters.add(req);
			execParameters.add(procRequest);
			execParameters.add(_trail);
		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
		this.callPlugins(WebRequestManager.PLUGINPOINT_UPDATE_STATUS_PROC_REQUEST, null,
		    execParameters);
		return procRequest;
	}

	/**
	 * Implementing interface method
	 */
	public int updateAllRequestProcessesStatus(long _requestId, Trail _trail)
	    throws RequestManagerException, PersistenceResourceAccessException {
		Connection session = null;
		Object t = null;
		ArrayList execParameters = null;
		int total = -1;
		try {
			t = persistence.beginTransaction(session);
			session = (Connection) persistence.openResourceConnection();

			WebRequestRequestsDAO dao = new WebRequestRequestsDAO();
			total = dao.updateStatusForAll(session, _requestId, _trail);

			WebRequestDAO webDAO = new WebRequestDAO();
			WebRequest request = webDAO.selectWebRequest(session, _requestId);

			execParameters = new ArrayList();
			execParameters.add(request);
			execParameters.add(_trail);
		} catch (SQLException sqle) {
			persistence.rollbackTransaction(t);
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session, t);
		}
		this.callPlugins(WebRequestManager.PLUGINPOINT_UPDATE_MULTIPLE_STATUS_PROC_REQUEST, null,
		    execParameters);
		return total;
	}

	public void updateWebRequestStatus(long _requestId, int _newStatus)
	    throws RequestManagerException, PersistenceResourceAccessException {
		updateWebRequestStatus(_requestId, _newStatus, null);
	}

	/**
	 * Implementing interface method
	 */
	public void updateWebRequestStatus(long _requestId, int _newStatus, Date _endDate)
	    throws RequestManagerException, PersistenceResourceAccessException {
		// setting volatile information
		log.debug("web request [" + _requestId + "] changed status to " + _newStatus);
		Connection session = null;
		Object t = null;
		WebRequest request = loadWebRequestDetail(_requestId);
		try {
			// loading request with WRITE lock
			t = persistence.beginTransaction(session);
			session = (Connection) persistence.openResourceConnection();
			WebRequestDAO dao = new WebRequestDAO();
			request.setStatus(_newStatus);
			if (_endDate != null) {
				request.setEndDate(new Timestamp(_endDate.getTime()));
			}
			dao.updateWebRequest(session, _requestId, request);
		} catch (SQLException sqle) {
			persistence.rollbackTransaction(t);
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session, t);
		}
		ArrayList execParameters = new ArrayList();
		execParameters.add(request);
		this.callPlugins(WebRequestManager.PLUGINPOINT_UPDATE_STATUS_WEB_REQUEST, String
		    .valueOf(_newStatus), execParameters);
	}

	/**
	 * Implementing interface method
	 */
	public List resumeUnfinishedRequests(ResumeCriteria _criteria) throws RequestManagerException,
	    PersistenceResourceAccessException {
		Connection session = null;
		List resultList = null;
		try {
			FetchCriteria fetch = null;
			WebRequestCriteria criteria = new WebRequestCriteria();
			session = (Connection) persistence.openResourceConnection();
			WebRequestDAO dao = new WebRequestDAO();
			// if a start date was selected
			if (_criteria.getCreationDate() != null) {
				criteria.setStartDate(_criteria.getCreationDate());
			}
			// if a limit for requests where selected
			if (_criteria.getLimit() > 0) {
				fetch = new FetchCriteria();
				fetch.setSize(_criteria.getLimit());
			}
			criteria.setStatus(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED);
			resultList = dao.selectWebRequestList(session, criteria, fetch);
			// select in process if there is no limit specified (limit < 0) OR if
			// limit was specified (limit > 0) but it was not reached yet (result-size
			// < limit)
			if (((_criteria.getLimit() > 0) && (resultList.size() < _criteria.getLimit()))
			    || (_criteria.getLimit() < 0)) {
				// only re-set fetch size if limit was specified
				if (fetch != null) {
					fetch.setSize(_criteria.getLimit() - resultList.size());
				}
				criteria.setStatus(WebRequestLifeCycle.REQUEST_LIFECYCLE_INPROCESS);
				resultList.addAll(dao.selectWebRequestList(session, criteria, fetch));
			}
			// if resume is filtered by startDate
			log.debug("preparing to resume " + resultList.size() + " web requests");
		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
		if (resultList != null) {
			this.callPlugins(WebRequestManager.PLUGINPOINT_RESUME_REQUEST, null, resultList);
		} else {
			log.info("no web requests found to resume");
		}
		return resultList;
	}

	/**
	 * Implementing interface method
	 */
	public Set getNotificationsForWebRequest(long _id) throws RequestManagerException,
	    PersistenceResourceAccessException {
		WebRequest request = loadWebRequestDetail(_id);
		return request.getNotifications();
	}

	public void setAllNotificationsAsSent(long _requestId) throws RequestManagerException,
	    PersistenceResourceAccessException {
		setAllNotificationsAsSent(_requestId, null);
	}

	/**
	 * Implementing interface method
	 */
	public void setNotificationAsSent(long _notificationId) throws RequestManagerException,
	    PersistenceResourceAccessException {
		setNotificationAsSent(_notificationId, null);
	}

	/**
	 * Implementing interface method
	 */
	public void setAllNotificationsAsSent(long _requestId, Date _sentTime)
	    throws RequestManagerException, PersistenceResourceAccessException {
		Connection session = null;
		Object t = null;
		try {
			// loading request with WRITE lock
			t = persistence.beginTransaction(session);
			session = (Connection) persistence.openResourceConnection();
			WebNotificationDAO dao = new WebNotificationDAO();
			List notifications = dao.selectNotificationList(session, _requestId);
			log.debug("updating notification status to all emails set for request " + _requestId);
			if (_sentTime == null) {
				log.warn("no sent time specified. Setting for now");
				_sentTime = Calendar.getInstance().getTime();
			}
			for (Iterator notIterator = notifications.iterator(); notIterator.hasNext();) {
				NotificationEmail email = (NotificationEmail) notIterator.next();
				setNotificationAsSent(email.getNotificationId(), _sentTime);
			}
		} catch (SQLException sqle) {
			persistence.rollbackTransaction(t);
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session, t);
		}
	}

	/**
	 * Implementing interface method
	 */
	public void setNotificationAsSent(long _notificationId, Date _sentTime)
	    throws RequestManagerException, PersistenceResourceAccessException {
		Connection session = null;
		Object t = null;
		NotificationEmail notification = null;
		try {
			// loading request with WRITE lock
			t = persistence.beginTransaction(session);
			session = (Connection) persistence.openResourceConnection();
			WebNotificationDAO dao = new WebNotificationDAO();
			notification = dao.selectNotification(session, _notificationId);
			if (_sentTime == null) {
				log.warn("no sent time specified. Setting for now");
				_sentTime = Calendar.getInstance().getTime();
			}
			notification.setSentDatetime(new Timestamp(_sentTime.getTime()));
			dao.updateNotification(session, _notificationId, notification);
			log.debug("updated notification to " + notification.getEmailAddress() + " as sent @ "
			    + notification.getSentDatetime());
		} catch (SQLException sqle) {
			persistence.rollbackTransaction(t);
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session, t);
		}
	}

	/**
	 * Implementing interface method
	 */
	public void addBundleFile(long _requestId, BundleFile _file) throws RequestManagerException,
	    PersistenceResourceAccessException {
		addBundleFile(_requestId, Arrays.asList(new BundleFile[] { _file }));
	}

	/**
	 * Implementing interface method
	 */
	public void addBundleFile(long _requestId, Collection _filelist) throws RequestManagerException,
	    PersistenceResourceAccessException {
		Connection session = null;
		Object t = null;
		try {
			t = persistence.beginTransaction(session);
			session = (Connection) persistence.openResourceConnection();
			WebBundleDAO dao = new WebBundleDAO();
			dao.createBundlefiles(session, _requestId, _filelist);
		} catch (SQLException sqle) {
			persistence.rollbackTransaction(t);
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session, t);
		}
	}

	public List findGeneratedOutputFiles(long _requestId) throws RequestManagerException,
	    PersistenceResourceAccessException {
		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();
			WebRequestRequestsDAO dao = new WebRequestRequestsDAO();
			return dao.selectGeneratedOutputFiles(session, _requestId);
		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

	public void removeBundleFiles(long _requestId) throws RequestManagerException,
	    PersistenceResourceAccessException {
		Connection session = null;
		try {
			session = (Connection) persistence.openResourceConnection();
			WebBundleDAO dao = new WebBundleDAO();
			dao.removeFiles(session, _requestId);
		} catch (SQLException sqle) {
			throw new RequestManagerException("caught JDBC SQL exception", sqle);
		} finally {
			persistence.closeResourceConnection(session);
		}
	}

}
