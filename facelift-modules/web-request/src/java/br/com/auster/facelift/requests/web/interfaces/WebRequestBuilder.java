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
 * Created on Apr 14, 2005
 */
package br.com.auster.facelift.requests.web.interfaces;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.web.model.NotificationEmail;
import br.com.auster.facelift.requests.web.model.WebRequest;

/**
 * Helps on creating a new web request. Although this class does not enforce a sequence of steps, not even mandatory ones, the business layer  
 * 	defined by the interface <code>WebRequestManager</code> will not allow a web request to be created without a list of processing requests. 
 * 
 * @author framos
 * @version $Id: WebRequestBuilder.java 352 2007-04-19 21:16:44Z pvieira $
 */
public class WebRequestBuilder {

	
	
    // -------------------------
    // Instance variables
    // -------------------------

	private WebRequest request;
	
	
	
    // -------------------------
    // Public methods
    // -------------------------
	
	/**
	 * Creates a new web request object, with the owner id specified.The start date will be the current date/time and the request
	 * 	status will be set to <strong>created</strong>.
	 * 
	 * @param _ownerId the identifier of the request owner
	 */
	public void buildWebRequest(long _ownerId) {
		buildWebRequest(_ownerId, Calendar.getInstance().getTime());
	}

	/**
	 * Creates a new web request object, whith the owner id and start date/time as specified. The status of this new request will be set
	 *   to <strong>created</strong>.
	 *     
	 * @param _ownerId the identifier of the request owner
	 * @param _startDate the date/time the request was created 
	 */
	public void buildWebRequest(long _ownerId, Date _startDate) {
		request = new WebRequest();
		request.setOwnerId(_ownerId);
		setStartDate(_startDate);
		setStatus(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED);
	}
	
	/**
	 * Deletes the current web request object being built. To start all over, a new build operation needs to be issued.
	 */
	public void clearWebRequest() {
		request = null;
	}

	/**
	 * Sets the status information of the in-built web request object. If no build operation was issued, or clear was just called invalidating
	 * 	the web request, then an <code>IllegalStateException</code> will be raised.
	 * 
	 * @param _status the status information
	 */
	public void setStatus(int _status) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		request.setStatus(_status);
	}
	
	/**
	 * Sets the web request creation date information. If no build operation was issued, or clear was just called invalidating
	 *   the web request, then an <code>IllegalStateException</code> will be raised.
	 *   
	 * @param _startDate the date/time the request was created
	 */
	public void setStartDate(Date _startDate) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		request.setStartDate(new Timestamp(_startDate.getTime()));
	}

	/**
	 * Adds a custom information to the current request. Custom informations are handles as pairs of keys and values, where keys cannot
	 * 	be duplicated. The map that handles these informations is created when the first pair of information is added.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _key the unique key of the information
	 * @param _value the value associated with the key
	 */
	public void addAddInfo(String _key, String _value) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		if (request.getAdditionalInformation() == null) {
			setAddInfoMap(new HashMap());
		}
		request.getAdditionalInformation().put(_key, _value);
	}
	
	/**
	 * Resets the map of custom information, and sets it as the incoming map parameter.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _map the new map of custom information
	 */
	public void setAddInfoMap(Map _map) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		request.setAdditionalInformation(_map);
	}
	
	/**
	 * Adds a single processing request to the set of processing requests of the current web request. Processing request is the unit of work for 
	 * 	processing servers. The set that handles these processing requests is created when the first request is added.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _request a newly created processing request
	 */
	public void addProcessingRequest(Request _request) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		if (request.getProcessingRequests() == null) {
			setProcessingRequestSet(new HashSet());
		}
		request.getProcessingRequests().add(_request);
	}

	/**
	 * Resets the set of processing requests, replacing it by the incoming set parameter.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _set the set of processing requests
	 */
	public void setProcessingRequestSet(Set _set) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		request.setProcessingRequests(_set);
	}
	
	/**
	 * Adds a new email address to be notified when the request reaches the pre-defined statuses. The set that handles these email 
	 *  addresses is created when the first request is added.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _notification a new email address
	 */
	public void addEmailNotification(NotificationEmail _notification) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		if (request.getNotifications() == null) {
			setEmailNotificationSet(new HashSet());
		}
		_notification.setRequest(request);
		request.getNotifications().add(_notification);
	}

	/**
	 * Resets the set of email address notitication, replacing it by the incoming set parameter.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _set the set of notification email address
	 */
	public void setEmailNotificationSet(Set _set) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		if (_set != null) {
			Iterator iterator = _set.iterator();
			while (iterator.hasNext()) {
				NotificationEmail email = (NotificationEmail) iterator.next();
				email.setRequest(request);
			}
			request.setNotifications(_set);
		}
	}

	/**
	 * Adds a single input file to the set of input files of the current web request. 
	 * The set that handles these input files is created when the first input file is added.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _inputFile a newly input file
	 */
	public void addInputFile(String _inputFile) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		if (request.getInputFiles() == null) {
			setInputFilesSet(new HashSet());
		}
		request.getInputFiles().add(_inputFile);
	}

	/**
	 * Resets the set of input files, replacing it by the incoming set parameter.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _set the set of input files
	 */
	public void setInputFilesSet(Set _set) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		request.setInputFiles(_set);
	}

	/**
	 * Adds a output format. The map that handles these informations is created when the first pair of information is added.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _key the unique key of the information
	 * @param _value the value associated with the key
	 */
	public void addFormats(String _key, String _value) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		if (request.getAdditionalInformation() == null) {
			setFormatsMap(new HashMap());
		}
		request.getFormats().put(_key, _value);
	}
	
	/**
	 * Resets the map of output formats, and sets it as the incoming map parameter.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the web request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _map the new map of custom information
	 */
	public void setFormatsMap(Map _map) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		request.setFormats(_map);
	}

	/**
	 * Returns the in-built web request object. If no build operation was issued, or clear was just called invalidating the web request, then
	 * 	<code>null</code> is returned.
	 * 
	 * @return the current web request or <code>null</code>
	 */
	public WebRequest getWebRequest() {
		return request;
	}
	
	
	
    // -------------------------
    // Private methods
    // -------------------------	
	
	/**
	 * Validates if a build operation was previously executed.
	 */
	private boolean hasStartedBuilding() {
		return (request != null);
	}
}
