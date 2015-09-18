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
package br.com.auster.facelift.requests.web.interfaces;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.auster.persistence.FetchCriteria;
import br.com.auster.persistence.PersistenceResourceAccessException;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.interfaces.RequestManager;
import br.com.auster.facelift.requests.interfaces.RequestManagerException;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;
import br.com.auster.facelift.requests.web.model.BundleFile;
import br.com.auster.facelift.requests.web.model.WebRequest;
import br.com.auster.facelift.requests.web.model.WebRequestConsequenceVO;

/**
 * The operations a Web Request service must implement, in order to manage
 * correctly web requests and its related collections.
 * 
 * @author framos
 * @version $Id$
 */
public interface WebRequestManager extends RequestManager {

	// -------------------------
	// Class variables
	// -------------------------

	public static final String	PLUGINPOINT_CREATE_REQUEST	                    = "webrequest.created";

	public static final String	PLUGINPOINT_UPDATE_STATUS_PROC_REQUEST	        = "webrequest.updated.procRequest.status";
	public static final String	PLUGINPOINT_UPDATE_MULTIPLE_STATUS_PROC_REQUEST	= "webrequest.updated.multiple.procRequest.status";
	public static final String	PLUGINPOINT_UPDATE_STATUS_WEB_REQUEST	          = "webrequest.updated.status";
	public static final String	PLUGINPOINT_UPDATE_COUNTER_REQUEST	            = "webrequest.updated.counter";

	public static final String	PLUGINPOINT_RESUME_REQUEST	                    = "webrequest.resumed";

	// -------------------------
	// Public methods
	// -------------------------

	/**
	 * Creates a web request, with all the attributes defined : processing
	 * requests and additional information. If any information is not correctly
	 * defined, than an exception will be thrown and no request will be created.
	 * When created, the web request is automatically assigned to status
	 * <strong>'CREATED'</strong>.
	 * <p>
	 * Also, this method contains a plugin point, which can execute any operations
	 * after creating the request. The plugin point name is
	 * <code>webrequest.created</code>.
	 * 
	 * @param _request
	 *          the web request to be created
	 * 
	 * @return the request unique identifier
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public long createWebRequest(WebRequest _request) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Creates a link between proc request and web request.
	 * 
	 * @param _request
	 *          the proc request to be linked
	 * 
	 * @param _transactionId
	 * 			transactionId from web request
	 * 
	 * @return the request unique identifier
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void createRequestLink(long _transactionId, Request _request) throws RequestManagerException,
    	PersistenceResourceAccessException;

	/**
	 * Returns a list of web requests matching the specified criteria. The number
	 * of returned instances is undefined, depending on the behaviour of
	 * persistence mechanism and the database implementation. To limit the number
	 * of returned objects, use the
	 * <code>findWebRequests(Criteria, FetchCriteria)</code> version of this
	 * method.
	 * <p>
	 * Note that the list of processing requests are not loaded, since its defined
	 * with lazy-load option true.
	 * 
	 * @param _criteria
	 *          the matching criterias
	 * 
	 * @return the resulting list of requests
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public List findWebRequests(WebRequestCriteria _criteria) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Returns a list of web requests matching the specified criteria. Note that
	 * the list of processing requests are not loaded, since its defined with
	 * lazy-load option true.
	 * <p>
	 * By defining the fetching parameters, its possible not only to define the
	 * offset and lenght of the results to be returned but also a list of order-by
	 * clauses to be applyed in the query statement.
	 * 
	 * @param _criteria
	 *          the matching criterias
	 * @param _fetch
	 *          the fetching parameters
	 * 
	 * @return the resulting list of requests
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public List findWebRequests(WebRequestCriteria _criteria, FetchCriteria _fetch)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Returns the total number of web requests which can be retrieved, using the
	 * specified search criteria.
	 * 
	 * @param _criteria
	 *          the search criteria
	 * 
	 * @return the total number of web requests
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public int countWebRequests(WebRequestCriteria _criteria) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Returns the web request instance loaded with all its information data and
	 * processing requests
	 * 
	 * @param _id
	 *          the unique identifier of a web request
	 * 
	 * @return the web request instance
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public WebRequest loadWebRequestDetail(long _id) throws RequestManagerException,
	    PersistenceResourceAccessException;
    
    /**
     * 
     * Is responsible in delete a especifique WebRequest.
     * 
     * @param id the unique identifier of a web requiest
     * @throws RequestManagerException if any exception occurred
     * @throws PersistenceResourceAccessException if any exception occurred
     * @throws SQLException 
     */
    public void deleteWebRequest(long id) throws RequestManagerException,
    PersistenceResourceAccessException, SQLException;
    
	/**
	 * Returns a list of processing requests, for a specific web request, matching
	 * the criterias passed as parameter. The number of returned instances is
	 * undefined, depending on the behaviour of persistence mechanism and the
	 * database implementation. To limit the number of returned objects, use the
	 * <code>findWebRequestProcesses(long, Criteria, FetchCriteria)</code>
	 * version of this method.
	 * 
	 * @param _id
	 *          the unique identifier of a web request
	 * @param _criteria
	 *          the matching criterias
	 * 
	 * @return the resulting list of requests
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public List findWebRequestProcesses(long _id, RequestCriteria _criteria)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Returns a list of processing requests, for a specific web request, matching
	 * the criterias passed as parameter. The details of each processing request
	 * will be automatically loaded. If its not needed to load them, check out the
	 * <code>findWebRequestProcesses(long, RequestCriteria, FetchCriteria, boolean)</code>
	 * version of this method.
	 * <p>
	 * By defining the fetching parameters, its possible not only to define the
	 * offset and lenght of the results to be returned but also a list of order-by
	 * clauses to be applyed in the query statement.
	 * 
	 * @param _id
	 *          the unique identifier of a web request
	 * @param _criteria
	 *          the matching criterias
	 * @param _fetch
	 *          the fetching parameters
	 * 
	 * @return the resulting list of requests
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public List findWebRequestProcesses(long _id, RequestCriteria _criteria, FetchCriteria _fetch)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Returns a list of processing requests, for a specific web request, matching
	 * the criterias passed as parameter. It behaves exactly like
	 * <code>findWebRequestProcesses(long, RequestCriteria, FetchCriteria)</code>,
	 * except that it receives a boolean indicating wether or not it should load
	 * the details of each processing request found.
	 * <p>
	 * By defining the fetching parameters, its possible not only to define the
	 * offset and lenght of the results to be returned but also a list of order-by
	 * clauses to be applyed in the query statement.
	 * 
	 * @param _id
	 *          the unique identifier of a web request
	 * @param _criteria
	 *          the matching criterias
	 * @param _fetch
	 *          the fetching parameters
	 * @param _loadDetails
	 *          idicates if the details should be loaded
	 * 
	 * @return the resulting list of requests
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public List findWebRequestProcesses(long _id, RequestCriteria _criteria, FetchCriteria _fetch,
	    boolean loadDetails) throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Returns the total number of processing requests, for a single web request,
	 * which can be retrieved, using the specified search criteria.
	 * 
	 * @param _id
	 *          the unique identifier of the web request
	 * @param _criteria
	 *          the search criteria
	 * 
	 * @return the total number of processing requests
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public int countWebRequestProcesses(long _id, RequestCriteria _criteria)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Creates a map, where the keys are each status that processing requests for
	 * the specified web request have passed through. The values are the counts
	 * for these status.
	 * 
	 * @param _id
	 *          the web request identifier
	 * @return the map of status counters
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public Map getWebRequestCounters(long _id) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Updates the status of all processing requests of a web request to the
	 * specified <code>_status</code>.
	 * 
	 * @param _requestId
	 *          the unique identifier of the web request
	 * @param _trail
	 *          the new status information for the request
	 * 
	 * @return the number of processing requests updated
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public int updateAllRequestProcessesStatus(long _requestId, Trail _trail)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Moves the web request through the possible status of its life cycle. It
	 * <strong>does not</strong> tends to propagate modifications made to any
	 * associated objects or collections. This behavior is defined in hibernate
	 * mapping files.
	 * </p>
	 * Also, this method contains a plugin point, which can execute any operations
	 * after updating the request information. The plugin point name is
	 * <code>webrequest.updated.status</code>.
	 * 
	 * @param _request
	 *          the web request unique identifier
	 * @param _newStatus
	 *          the new status to set for this web request
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void updateWebRequestStatus(long _requestId, int _newStatus)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Updates the status, as defined in
	 * 
	 * @link #updateWebRequestStatus(long, int), and also the end date of the
	 *       specified request.
	 * 
	 * @param _requestId
	 *          the web request unique identifier
	 * @param _newStatus
	 *          the new status to set for this web request
	 * @param _endDate
	 *          the end date/time for this request. If its a non-final status,
	 *          leave it <code>null</code>
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void updateWebRequestStatus(long _requestId, int _newStatus, Date _endDate)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Returns the list of generated files, for all processing requests of the
	 * currently specified web request. These generated files will not be ordered
	 * by processing request, generation date/time or name. If no files where
	 * found, then an empty list will be returned. Also, if for some reason two
	 * processing requests generated the same file (in which case one overwritten
	 * the other) there is no guarantees that just one occurrence of this file
	 * will be returned.
	 * 
	 * @param _requestId
	 *          the web request id
	 * 
	 * @return the list of generated files, each holded in an isntance of
	 *         <code>OutputFile</code>
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public List findGeneratedOutputFiles(long _requestId) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Returns the list of <strong>unfinished</strong> web requests. The
	 * definition of an unfinished request is specified in the TRAC portal of this
	 * project.
	 * <p>
	 * Also, this method contains a plugin point, which can execute any operations
	 * prior to returning these requests. The plugin point name is
	 * <code>webrequest.resumed</code>.
	 * 
	 * @param _criteria
	 * @return
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public List resumeUnfinishedRequests(ResumeCriteria _criteria) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Returns a list of <code>NotificationEmail</code> instances, which were
	 * defined as notification address for the specified request.
	 * 
	 * @param _requestId
	 *          the identifier of the web request
	 * 
	 * @return the list of notification objects
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public Set getNotificationsForWebRequest(long _requestId) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Sets the email notification as had been sent to the destination address,
	 * assigning the current date/time.
	 * 
	 * @param _notificationId
	 *          the unique identifer of the notification
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void setNotificationAsSent(long _notificationId) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Sets all notifications as sent, to the specified web request. The sent
	 * date/time information is set to the current one.
	 * 
	 * @param _requestId
	 *          the unique identifier of the web request
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void setAllNotificationsAsSent(long _requestId) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Sets the email notification as had been sent to the destination address,
	 * assigning to it the data/time specified.
	 * 
	 * @param _notificationId
	 *          the unique identifer of the notification
	 * @param _sentTime
	 *          the moment (date & time) the notification was sent
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void setNotificationAsSent(long _notificationId, Date _sentTime)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Sets all notifications as sent, to the specified web request. The same
	 * date/time is set for all notifications as referenced by the parameter
	 * <code>_sentTime</code>.
	 * 
	 * @param _requestId
	 *          the unique identifier of the web request
	 * @param _sentTime
	 *          the moment (date & time) the notification was sent
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void setAllNotificationsAsSent(long _requestId, Date _sentTime)
	    throws RequestManagerException, PersistenceResourceAccessException;

	/**
	 * Saves a newly generated bundle file to the current web request. If the file
	 * is <code>null</code> nothing will happen.
	 * 
	 * @param _requestId
	 *          the unique identifier of the web request
	 * @param _file
	 *          the bundle file
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void addBundleFile(long _requestId, BundleFile _file) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Saves the list of newly generated bundle files to the current web request.
	 * If the list is <code>null</code> or empty, nothing will happen. This
	 * method is simply a shortcut to make easier to save several bundle files at
	 * the same time.
	 * 
	 * @param _requestId
	 *          the unique identifier of the web request
	 * @param _filelist
	 *          the list of bundle files
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void addBundleFile(long _requestId, Collection _filelist) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/**
	 * Removes all current generated bundled files for the specified request.
	 * 
	 * @param _requestId
	 *          the request unique id
	 * 
	 * @throws RequestManagerException
	 *           if any exception occurred
	 */
	public void removeBundleFiles(long _requestId) throws RequestManagerException,
	    PersistenceResourceAccessException;

	/***
	 * Given a transaction ID (Formerly known as request ID) returns a List with the {@link WebRequestConsequenceVO}:
	 * 
	 * @param transactionID The transaction ID we are gonna get the results for
	 * @return A List as indicated above or and empty List
	 * @throws RequestManagerException
	 * @throws PersistenceResourceAccessException
	 */
	public List getConsequenceCounters(long transactionID) throws RequestManagerException,
  PersistenceResourceAccessException;
}
