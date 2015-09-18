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
 * Created on Feb 21, 2005
 */
package br.com.auster.facelift.requests.interfaces;

import java.util.List;
import java.util.Set;

import br.com.auster.persistence.PersistenceResourceAccessException;
import br.com.auster.facelift.requests.model.OutputFile;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;
import br.com.auster.facelift.services.Service;



/**
 * @author framos
 * @version $Id$
 */
public interface RequestManager extends Service {


    
    public static final String PLUGINPOINT_CREATE_SINGLE_REQUEST = "procrequest.create.single";
    public static final String PLUGINPOINT_CREATE_MULTIPLE_REQUEST = "procrequest.create.multiple";
    public static final String PLUGINPOINT_UPDATE_STATUS_REQUEST = "procrequest.update.status";
    
    
    /**
     * Returns a list of <code>Request</code> objects, loaded with all its information. All trails, its generated 
     *  files when they exist and input files are loaded into the defined Java object structure.
     *  <p>
     *   
     * @param _criteria the list of criteria to be evaluated
     * 
     * @return the list of loaded requests
     *  
     * @throws RequestManagerException if any exception occurred
     */
    public List findRequests(RequestCriteria _criteria) throws RequestManagerException, PersistenceResourceAccessException;

    /**
     * Loads the information related to a specific request, and returns it. All trails, its generated files when
     *  they exist and input files are loaded into the defined Java object structure.
     *   
     * @param _requestId the unique identifier of the request
     * 
     * @return the loaded <code>Request</code> object
     * 
     * @throws RequestManagerException if any exception occurred
     */
    public Request loadRequestDetail(long _requestId) throws RequestManagerException, PersistenceResourceAccessException;
	
    /**
     * Creates a new request using the <code>_request</code> parameter, and returns the track id for that request. If 
     *  the request could not be created due to invalid information in the parameter, a runtime exception will be thrown.
     *  If -1 is returned, than some internal problem was encountered, and admin. intervention is necessary. 
     * <P>
     *  The process of creating a new request requires that :
     *  <ul> at least one section is defined
     * 
     * @param _request the request to be created
     * 
     * @return the track id for the newly created request
     */
    public long createRequest(Request _request) throws RequestManagerException, PersistenceResourceAccessException;

    /**
     * Iterates over the list of requests, creating each one of them.
     * <P>
     * The creation process works similarly to a single process object creation.
     * 
     * @param _requests the list of requests
     * 
     * @return the number of requests created
     */
    public int createMultipleRequests(Set _requests) throws RequestManagerException, PersistenceResourceAccessException;
	
    /**
     * Creates a new entry in the trail list for a specific request. This can be some info sent by the server
     *  or a final status situation like finished or error.
     * <P>
     * 
     * @return the request updated object
     * 
     * @param _requestId the unique identifier of the processing request
     * @param _trail the new trail information
     */
    public Request updateRequestStatus(long _requestId, Trail _trail) throws RequestManagerException, PersistenceResourceAccessException;

    /**
     * This method operates just like the <code>updateRequestStatus(Request, Trail)</code> version, adding 
     *  the ability to inform that files where generated. If the list of files is empty or <code>null</code> than
     *  the method will behave exactly as the previously mentioned version.
     * <P>
     * 
     * @return the request updated object
     * 
     * @param _requestId the unique identifier of the processing request
     * @param _trail the new trail information
     * @param _generatedFile the list of files generated  
     */
    public Request updateRequestStatus(long _requestId, Trail _trail, OutputFile[] _generatedFile) throws RequestManagerException, PersistenceResourceAccessException;   
    
	/**
	 * Returns a list of <code>OutputFile</code> objects, each containing the name of a file generated when the specified request id was 
	 * 	processed. If no files were found, then an empty list is returned.
	 * 
	 * @param _requestId the request id to search for
	 * 
	 * @return the list of generated files
	 */
	public List findOutputFiles(long _requestId) throws RequestManagerException, PersistenceResourceAccessException;
	
	/**
	 * Returns a list of <code>OutputFile</code> objects, each containing the name of a file generated when processing one of the request ids 
	 * 	in the <code>_requestIdList</code> parameter. If no file were found, then an empty list is returned. Note that no order in the resulting
	 *  file list is garanteed, so do not assume that its in the <code>file_id</code> order not even in order of the ids of the requests.
	 * 
	 * @param _requestIdList the request id to search for, as a list of String
	 * 
	 * @return the list of generated files
	 */
	public List findOutputFiles(List _requestIdList) throws RequestManagerException, PersistenceResourceAccessException;
	
}
