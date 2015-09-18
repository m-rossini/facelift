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
package br.com.auster.facelift.requests.web.hibernate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.com.auster.common.log.LogFactory;
import br.com.auster.facelift.requests.hibernate.RequestManagerImpl;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.interfaces.RequestManagerException;
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
import br.com.auster.persistence.OrderClause;
import br.com.auster.persistence.PersistenceResourceAccessException;

/**
 * The current implementation of the Web Request service
 * 
 * @author framos
 * @version $Id: WebRequestManagerImpl.java 353 2007-05-08 21:20:06Z pvieira $
 */
public class WebRequestManagerImpl extends RequestManagerImpl 
								   implements WebRequestManager {

	
   
    // -------------------------
    // Instance variables
    // -------------------------
	
    private Logger log = LogFactory.getLogger(WebRequestManagerImpl.class);
    
    
	
    // -------------------------
    // Public methods
    // -------------------------
	
    /**
     * Implementing interface method
     */
    public long createWebRequest(WebRequest _request) throws RequestManagerException, PersistenceResourceAccessException {
        // setting initial status for web request
        _request.setStatus(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED);
		// creating list of processing requests
		super.createMultipleRequests(_request.getProcessingRequests());
        Session session = null;
		Object t = null;
        try {
            session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			session.save(_request);
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
            persistence.closeResourceConnection(session, t);
        }
        log.debug("created request with id = " + _request.getRequestId());
		ArrayList execParameters = new ArrayList();
		execParameters.add(_request);
		this.callPlugins(WebRequestManager.PLUGINPOINT_CREATE_REQUEST, null, execParameters );
        return _request.getRequestId();
    }
    
    /**
     * Implementing interface method
     */
    public List findWebRequests(WebRequestCriteria _criteria) throws RequestManagerException, PersistenceResourceAccessException {
        return findWebRequests(_criteria, null);
    }
    
    /**
     * Implementing interface method
     */
    public List findWebRequests(WebRequestCriteria _criteria, FetchCriteria _fetch) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
        try {
            session = (Session) persistence.openResourceConnection();
            Criteria criteria = session.createCriteria(WebRequest.class);
            // checking for owner Id
            if (_criteria.getOwnerId() > 0) {
                criteria.add(Expression.eq("ownerId", new Long(_criteria.getOwnerId())));
                log.debug("filtering search by ownerId = " + _criteria.getOwnerId());
            }
            // checking for start date 
            if (_criteria.getStartDate() != null) {
                criteria.add(Expression.ge("startDate", _criteria.getStartDate()));
                log.debug("filtering search by startDate >= " + _criteria.getStartDate());
            } 
            // checking for end date
            if (_criteria.getEndDate() != null) {
                criteria.add(Expression.le("endDate", _criteria.getEndDate()));
                log.debug("filtering search by startDate <= " + _criteria.getEndDate());
            }
            setFetchParametersInCriteria(criteria, _fetch);
            List resultList = criteria.list();
            Iterator iterator = resultList.iterator();
            while (iterator.hasNext()) {
                WebRequest web = (WebRequest) iterator.next();
                web.setCounters(getWebRequestCounters(web.getRequestId()));
            }
            return resultList;
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session);
        }
    }

    public int countWebRequests(WebRequestCriteria _criteria) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
        try {
            session = (Session) persistence.openResourceConnection();
            Query q = session.getNamedQuery("count.webrequest");
            return ((Integer)q.uniqueResult()).intValue();
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session);
        }
    }
	
	
    /**
     * Implementing interface method
     */
    public WebRequest loadWebRequestDetail(long _id) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
        try {
			session = (Session) persistence.openResourceConnection();
            Criteria criteria = session.createCriteria(WebRequest.class);
            criteria.add(Expression.eq("requestId", new Long(_id)));
            log.debug("loading details for request id =" + _id);
            WebRequest web = (WebRequest) criteria.uniqueResult();
            // loading counters
            web.setCounters(getWebRequestCounters(_id));
            // returning web request, loaded and with all counters
            return web;
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session);
        }
    }


    /**
     * Implementing interface method
     */
    public List findWebRequestProcesses(long _id, RequestCriteria _criteria) throws RequestManagerException, PersistenceResourceAccessException {
        return findWebRequestProcesses(_id, _criteria, null); 
    }
    
    /**
     * Implementing interface method
     */
    public List findWebRequestProcesses(long _id, RequestCriteria _criteria, FetchCriteria _fetch) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
        try {            
			session = (Session) persistence.openResourceConnection();
            Query q = session.getNamedQuery("procrequests.filtered.webrequest");            
            String str = q.getQueryString();
            if (_criteria != null) {
                if (_criteria.getLabel() != null) {
                    str = q.getQueryString() +  " and procRequests.label like '" + _criteria.getLabel() + "'";
                } else {
                    str = q.getQueryString() +  " and procRequests.latestStatus = " + _criteria.getStatus();
                } 
                q = session.createQuery(str);
            }
			log.debug("searching processing requests with query " + str);
            q = setFetchParametersInQuery(session, q, _fetch);
            q.setLong(0, _id);
            return q.list();
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {			
			persistence.closeResourceConnection(session);
        }
    }
	
    /**
     * Implementing interface method
     */
    public List findWebRequestProcesses(long _id, RequestCriteria _criteria, FetchCriteria _fetch, boolean _loadDetails) throws RequestManagerException, PersistenceResourceAccessException {
		// due to the way the objects are confgured in Hibernate, there is no way not to load the details of the processing requests 
		return findWebRequestProcesses(_id, _criteria, _fetch, true); 
    }
	
    /**
     * Implementing interface method
     */
	public int countWebRequestProcesses(long _id, RequestCriteria _criteria) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
        try {            
			session = (Session) persistence.openResourceConnection();
			String str = session.getNamedQuery("procrequests.count.webrequest").getQueryString();
            if (_criteria != null) {
                if (_criteria.getLabel() != null) {
					// restrict to status == 1, since TRAILS is part of the query
                    str += " and procRequests.label like '" + _criteria.getLabel() + "' and trails.status = 1";
                } else if (_criteria.getStatus() > 0) {
                    str += " and trails.status = " + _criteria.getStatus();
                } else {
					str += "and trails.status = 1";
	            }
            } else {
				str += "and trails.status = 1";
            }
			
            Query q = session.createQuery(str);
			log.debug("counting processing requests with query " + str);
            q.setLong(0, _id);
            Integer count = (Integer) q.uniqueResult();
			log.debug("found " + count + " processing requests in the database");
			return count.intValue();
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {			
			persistence.closeResourceConnection(session);
        }
    }
    
    /**
     * Implementing interface method
     */
    public Map getWebRequestCounters(long _id) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
        Map counters = new HashMap();
        try {            
            session = (Session) persistence.openResourceConnection();
            Query q = session.getNamedQuery("procrequests.countByStatus.webrequest");
            log.debug("counting processing requests with query " + q.getQueryString());
            q.setLong(0, _id);
            Iterator iterator = q.list().iterator();
            while (iterator.hasNext()) {
                Object[] row = (Object[]) iterator.next(); 
                counters.put( row[0].toString(), Integer.valueOf(row[1].toString()) );
            }
            return counters;
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {         
            persistence.closeResourceConnection(session);
        }
    }

    
    /**
     * Implementing interface method
     */
    public Request updateRequestStatus(long _requestId, Trail _trail) throws RequestManagerException, PersistenceResourceAccessException {
        return updateRequestStatus(_requestId, _trail, null);
    }
    
    /**
     * Implementing interface method
     */
    public Request updateRequestStatus(long _requestId, Trail _trail, OutputFile[] _outputFiles) throws RequestManagerException, PersistenceResourceAccessException {
        Request procRequest = super.updateRequestStatus(_requestId, _trail, _outputFiles);        
        // update web request counters & status if needed
        Session session = null;
		Object t = null;
		ArrayList execParameters = null;
        try {
			session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
            Query q = session.getNamedQuery("procrequests.findby.webrequest");
            q.setLong(0, _requestId);
            WebRequest req = (WebRequest) q.uniqueResult();
			// building list of attributes to call plugins
			execParameters = new ArrayList();
			execParameters.add(req);
			execParameters.add(procRequest);
			execParameters.add(_trail);
		} catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session, t);
        }
        this.callPlugins(WebRequestManager.PLUGINPOINT_UPDATE_STATUS_PROC_REQUEST, null, execParameters );
		return procRequest;
    }

    /**
     * Implementing interface method
     */	
	public int updateAllRequestProcessesStatus(long _requestId, Trail _trail) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
		Object t = null;
		ArrayList execParameters = null;
		WebRequest request = null;
		int total = -1;
        try {
			session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
            request = (WebRequest) session.load(WebRequest.class, new Long(_requestId), LockMode.UPGRADE);
			if (request == null) {
				log.warn("request with id " + _requestId + " does not exist");
			}
			List resultSet = findWebRequestProcesses(_requestId, null);
			total = resultSet.size();
			Iterator iterator = resultSet.iterator(); 
			log.debug("updating multiple proc. request status for web request " + _requestId);
			while (iterator.hasNext()) {
				Request procRequest = (Request) session.load(Request.class, 
						                                     new Long(((Request)iterator.next()).getRequestId()), 
						                                     LockMode.UPGRADE);
				Trail currentTrail = new Trail();
				currentTrail.setMessage(_trail.getMessage());
				currentTrail.setRequest(procRequest);
				currentTrail.setStatus(_trail.getStatus());
				currentTrail.setTrailDate(_trail.getTrailDate());
				// setting two-way relationship
				procRequest.setLatestStatus(_trail.getStatus());
				procRequest.getTrails().add(currentTrail);
				// saving trail info & request updates
	            session.update(procRequest);
			}
			log.debug("updated " + resultSet.size() + " proc. request status");
			execParameters = new ArrayList();
			execParameters.add(request);
			execParameters.add(_trail);
		} catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session, t);
        }
        this.callPlugins(WebRequestManager.PLUGINPOINT_UPDATE_MULTIPLE_STATUS_PROC_REQUEST, null, execParameters );
		return total;
	}

	
	public void updateWebRequestStatus(long _requestId, int _newStatus) throws RequestManagerException, PersistenceResourceAccessException {
		updateWebRequestStatus(_requestId, _newStatus, null);
	}
	
    /**
     * Implementing interface method
     */
    public void updateWebRequestStatus(long _requestId, int _newStatus, Date _endDate) throws RequestManagerException, PersistenceResourceAccessException {
		// setting volatile information 
		log.debug("web request [" + _requestId + "] changed status to " + _newStatus);
        Session session = null;
		Object t = null;
		WebRequest request = null;
        try {
			// loading request with WRITE lock 
			session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			request = (WebRequest) session.load(WebRequest.class, new Long(_requestId), LockMode.UPGRADE);
			request.setStatus(_newStatus);
			if (_endDate != null) {
				request.setEndDate(new Timestamp(_endDate.getTime()));
        	}
            session.update(request);
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session, t);
        }
		ArrayList execParameters = new ArrayList();
		execParameters.add(request);
        this.callPlugins(WebRequestManager.PLUGINPOINT_UPDATE_STATUS_WEB_REQUEST, String.valueOf(_newStatus), execParameters );
    }
	
    /**
     * Implementing interface method
     */
    public List resumeUnfinishedRequests(ResumeCriteria _criteria) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
		List resultList = null;
        try {
			session = (Session) persistence.openResourceConnection();
            Criteria criteria = session.createCriteria(WebRequest.class);
            criteria.add( Expression.or(Expression.eq("status", Integer.valueOf(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED)),
                                        Expression.eq("status", Integer.valueOf(WebRequestLifeCycle.REQUEST_LIFECYCLE_INPROCESS))) 
                        );
            // if resume is limited 
            if (_criteria.getLimit() > 0) {
                criteria.setMaxResults(_criteria.getLimit());
            } 
            // if resume is filtered by startDate  
            if (_criteria.getCreationDate() != null) {
//                if (_criteria.getAsPrevious()) {
//                    criteria.add(Expression.le("startDate", _criteria.getCreationDate())); 
//                } else {
                    criteria.add(Expression.ge("startDate", _criteria.getCreationDate())); 
//                }
            }
            resultList = criteria.list();
			log.debug("preparing to resume " + resultList.size() + " web requests");
        } catch (HibernateException he) {
            throw new RequestManagerException("caught persistence exception", he);
        } finally {			
			persistence.closeResourceConnection(session);
        }
		if (resultList != null) {
            this.callPlugins(WebRequestManager.PLUGINPOINT_RESUME_REQUEST, null, resultList );
		} else {
			log.info("no web requests found to resume"); 
		}
        return resultList;
    }
    
    /**
     * Implementing interface method
     */
    public Set getNotificationsForWebRequest(long _id) throws RequestManagerException, PersistenceResourceAccessException {
        WebRequest request = loadWebRequestDetail(_id);
        return request.getNotifications();
    }

	public void setAllNotificationsAsSent(long _requestId) throws RequestManagerException, PersistenceResourceAccessException {
		setAllNotificationsAsSent(_requestId, null);
	}
	
    /**
     * Implementing interface method
     */
	public void setNotificationAsSent(long _notificationId) throws RequestManagerException, PersistenceResourceAccessException {
		setNotificationAsSent(_notificationId, null);
	}
	
    /**
     * Implementing interface method
     */
	public void setAllNotificationsAsSent(long _requestId, Date _sentTime) throws RequestManagerException, PersistenceResourceAccessException { 
        Session session = null;
		Object t = null;
		WebRequest request = null;
        try {
			// loading request with WRITE lock 
			session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			request = (WebRequest) session.load(WebRequest.class, new Long(_requestId));
			Iterator notIterator = request.getNotifications().iterator();
			log.debug("updating notification status to all emails set for request " + _requestId);
			if (_sentTime == null) {
				log.warn("no sent time specified. Setting for now");
				_sentTime = Calendar.getInstance().getTime();
			}
			while (notIterator.hasNext()) {
				NotificationEmail email = (NotificationEmail) notIterator.next();
				setNotificationAsSent(email.getNotificationId(), _sentTime);
			}
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {			
			persistence.closeResourceConnection(session, t);
        }
	}
	
    /**
     * Implementing interface method
     */
	public void setNotificationAsSent(long _notificationId, Date _sentTime) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
		Object t = null;
		NotificationEmail notification = null;
        try {
			// loading request with WRITE lock 
			session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			notification = (NotificationEmail) session.load(NotificationEmail.class, new Long(_notificationId), LockMode.UPGRADE);
			if (_sentTime == null) {
				log.warn("no sent time specified. Setting for now");
				_sentTime = Calendar.getInstance().getTime();
			}
			notification.setSentDatetime(new Timestamp(_sentTime.getTime()));
            session.update(notification);
			log.debug("updated notification to " + notification.getEmailAddress() + " as sent @ " + notification.getSentDatetime());
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {			
			persistence.closeResourceConnection(session, t);
        }
	}
	
    /**
     * Implementing interface method
     */
    public void addBundleFile(long _requestId, BundleFile _file) throws RequestManagerException, PersistenceResourceAccessException {        
        addBundleFile(_requestId, Arrays.asList(new BundleFile[] { _file } ));
    }
    
    /**
     * Implementing interface method
     */
    public void addBundleFile(long _requestId, Collection _filelist) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
		Object t = null;
        try {
			session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			WebRequest req = (WebRequest) session.load(WebRequest.class, new Long(_requestId));
            log.debug("loading details for request id =" + _requestId);
            if (req != null) {
                Iterator iterator = _filelist.iterator();
                while (iterator.hasNext()) {
                    BundleFile file = (BundleFile) iterator.next();
                    file.setRequest(req);
					log.debug("adding bundle file " + file.getFilename());
                    req.getBundleFiles().add(file);
                }
                session.update(req);
				log.debug("bundle files added to web request");
            } else {
                log.warn("request with id=" + _requestId + " not found.");
            }
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session, t);
        }            
    }
    
	public List findGeneratedOutputFiles(long _requestId) throws RequestManagerException, PersistenceResourceAccessException {
		throw new UnsupportedOperationException("method not implemented in this class");
	}
	
    public void removeBundleFiles(long _requestId) throws RequestManagerException, PersistenceResourceAccessException {
        Session session = null;
		Object t = null;
        try {
			session = (Session) persistence.openResourceConnection();
			t = persistence.beginTransaction(session);
			Criteria c = session.createCriteria(BundleFile.class);
			c.add(Expression.eq("request.requestId", String.valueOf(_requestId)));
			List l  = c.list();
			for (Iterator i=l.iterator(); i.hasNext();) {
				session.delete(i.next());
			}
        } catch (HibernateException he) {
			persistence.rollbackTransaction(t);
            throw new RequestManagerException("caught persistence exception", he);
        } finally {
			persistence.closeResourceConnection(session, t);
        }            
    }
	
    
    // ------------------------
    //   Private methods
    // ------------------------
    
    private void setFetchParametersInCriteria(Criteria _criteria, FetchCriteria _fetch) {
        if (_fetch == null) {
            return;
        }
        _criteria.setFirstResult(_fetch.getOffset());
        _criteria.setMaxResults(_fetch.getSize());
        Iterator orderIterator = _fetch.orderIterator();
        while (orderIterator.hasNext()) {
            OrderClause orderClause = (OrderClause) orderIterator.next();
            if (orderClause.isAscending()) {
                _criteria.addOrder( Order.asc(orderClause.getFieldName()) );
            } else {
                _criteria.addOrder( Order.desc(orderClause.getFieldName()) );                
            }
        }
    }
    
    private Query setFetchParametersInQuery(Session _session, Query _query, FetchCriteria _fetch) throws HibernateException {
        
        if (_fetch == null) {
            return _query;
        }
        
        String queryStr = _query.getQueryString();
        Iterator orderIterator = _fetch.orderIterator();
        for (int i=0; orderIterator.hasNext(); i++) {
            if (i==0) {
                queryStr += " order by ";
            } else {
                queryStr += ", ";
            }
            OrderClause orderClause = (OrderClause) orderIterator.next();
            queryStr += orderClause.getFieldName() + " "; 
            if (orderClause.isAscending()) {
                queryStr += " asc";
            } else {
                queryStr += " desc";                
            }
        }
        Query result = _session.createQuery(queryStr);
        result.setFirstResult(_fetch.getOffset());
        result.setMaxResults(_fetch.getSize());
        return result;
    }

		/* (non-Javadoc)
     * @see br.com.auster.facelift.requests.web.interfaces.WebRequestManager#getConsequenceCounters(long)
     */
    public List getConsequenceCounters(long transactionID) throws RequestManagerException, PersistenceResourceAccessException {
    	RequestManagerException thw = new RequestManagerException();
    	thw.initCause(new UnsupportedOperationException("Hibernate Request Manager does not " +
    			"Support Consequence Group By Rules Query.TransactionID:" + transactionID));
    	throw thw;
    }

    /**
     * It responsible in 
     * <p>
     * Example :
     * <pre>
     * Create a example.
     * </pre>
     * </p>
     * 
     * @param id
     * @throws RequestManagerException
     * @throws PersistenceResourceAccessException
     * @see br.com.auster.facelift.requests.web.interfaces.WebRequestManager#deleteWebRequest(long)
     */
    public void deleteWebRequest(long id) throws RequestManagerException, PersistenceResourceAccessException {
        // TODO Auto-generated method stub
        
    }

	public void createRequestLink(long _transactionId, Request _request) throws RequestManagerException, PersistenceResourceAccessException {
		// TODO Auto-generated method stub
	}
	
}
