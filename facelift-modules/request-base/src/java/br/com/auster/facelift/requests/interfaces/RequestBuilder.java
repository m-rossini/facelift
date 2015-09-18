/*
 * Copyright (c) 2004 TTI Tecnologia. All Rights Reserved.
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
 * Created on Jun 8, 2005
 */
package br.com.auster.facelift.requests.interfaces;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.auster.facelift.requests.model.InputFile;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;

/**
 * Helps on creating a new processing request. Although this class does not enforce a sequence of steps, not even mandatory ones, the business   
 * 	layer defined by the interface <code>RequestManager</code> will not allow a request to be created without a list of input files or without a 
 *  initial trail.
 * 
 * @author framos
 * @version $Id: RequestBuilder.java 93 2005-06-10 13:33:32Z etirelli $
 */
public class RequestBuilder {

	
	
    // -------------------------
    // Instance variables
    // -------------------------

	private Request request;
	private Trail trail;
	
	
	
    // -------------------------
    // Public methods
    // -------------------------
	
	/**
	 * Creates a new processing request object. The label information will be set to <code>null</code>.
	 */
	public void builRequest() {
		buildRequest(null);
	}

	/**
	 * Creates a new processing request object. The label information will be set to the incoming parameter.
	 * 
	 * @param _label the request label
	 */
	public void buildRequest(String _label) {
		request = new Request();
		setLabel(_label);
		setStatus(RequestLifeCycle.REQUEST_LIFECYCLE_CREATED);
		// always creates a default initial trail
		trail = new Trail();
		trail.setStatus(RequestLifeCycle.REQUEST_LIFECYCLE_CREATED);
		trail.setTrailDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
	}
	
	/**
	 * Deletes the current request object being built. To start all over, a new build operation needs to be issued. It will 
	 *  also remove the reference to the initial trail.
	 */
	public void clearRequest() {
		request = null;
		trail = null;
	}

	/**
	 * Set the latest status of a processing request. If no build operation was issued, or clear was just called invalidating 
	 *   the request, then an <code>IllegalStateException</code>  will be raised. 
	 * 
	 * @param _status the status information
	 */
	public void setStatus(int _status) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the request");
		}
		request.setLatestStatus(_status);
	}
	
	/**
	 * Sets the request label field. If no build operation was issued, or clear was just called invalidating the request, 
	 *   then an <code>IllegalStateException</code>  will be raised.
	 *   
	 * @param _label the label of the request
	 */
	public void setLabel(String _label) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the request");
		}
		request.setLabel(_label);
	}
	
	/**
	 * Adds a custom information to the current request. Custom informations are handles as pairs of keys and values, where keys cannot
	 * 	be duplicated. The map that handles these informations is created when the first pair of information is added.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _key the unique key of the information
	 * @param _value the value associated with the key
	 */
	public void addAddInfo(String _key, String _value) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the request");
		}
		if (request.getAdditionalInformation() == null) {
			setAddInfoMap(new HashMap());
		}
		request.getAdditionalInformation().put(_key, _value);
	}
	
	/**
	 * Resets the map of custom information, and sets it as the incoming map parameter.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _map the new map of custom information
	 */
	public void setAddInfoMap(Map _map) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the request");
		}
		request.setAdditionalInformation(_map);
	}

	/**
	 * Replaces the current intitial trail information, with the one passed as parameter. If <code>null</code> is passed, the get operation
	 * 	will not return the current request.  
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _trail the initial trail object
	 */
	public void setInitialTrail(Trail _trail) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the web request");
		}
		trail = _trail;
	}

	/**
	 * Adds an input file as the source file to read information when processing the current request. 
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 *  @param _filename the name of the input file
	 */
	public void addInputFile(String _filename) {
		InputFile file = new InputFile();
		file.setFilename(_filename);
		addInputFile(file);
	}

	/**
	 * Adds an input file as the source file to read information when processing the current request. 
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 *  @param __file the input file object
	 */
	public void addInputFile(InputFile _file) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the request");
		}
		if (request.getInputFiles() == null) {
			setInputFilesSet(new HashSet());
		}
		_file.setRequest(request);
		request.getInputFiles().add(_file);
	}

	/**
	 * Resets the set of input files, replacing it by the incoming set parameter.
	 * <P>
	 * If no build operation was issued, or clear was just called invalidating the request, then an <code>IllegalStateException</code> 
	 *  will be raised.
	 *  
	 * @param _set the set of notification email address
	 */
	public void setInputFilesSet(Set _set) {
		if (! hasStartedBuilding()) {
			throw new IllegalStateException("cannot execute add/set/get operations before start building the request");
		}
		request.setInputFiles(_set);
	}

	/**
	 * Returns the in-built request object. If no build operation was issued, or clear was just called invalidating the web request, then
	 * 	<code>null</code> is returned. If the initial trail was set to <code>null</code> it will also return <code>null</code>
	 * 
	 * @return the current request or <code>null</code>
	 */
	public Request getRequest() {
		if ((request == null) || (trail == null)) {
			return null;
		}
		
		if (request.getTrails() == null) {
			request.setTrails(new ArrayList());
		}
		trail.setRequest(request);
		request.getTrails().add(trail);
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
