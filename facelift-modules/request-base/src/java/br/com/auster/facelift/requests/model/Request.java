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
 * Created on Feb 22, 2005
 */
package br.com.auster.facelift.requests.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.auster.facelift.requests.interfaces.RequestLifeCycle;

/**
 * Holds information about a single processing request. A processing request is composed of some source files, 
 * 	a list of objects representing the changes of status, the lastest status of the request and some label to
 *  identify this request.
 * 
 * @hibernate.class
 *          table="PROC_REQUEST"
 *          
 * @author framos
 * @version $Id$
 */
public class Request implements Serializable, Comparable {


    
    // -------------------------
    // Instance variables
    // -------------------------
    
    private long id;
    private String label;

    private Set inFiles;
    private List trails;
    private Map additionalInfo;
    
    private int latestStatus;
    
    
    
    // -------------------------
    // Constructors
    // -------------------------

    public Request() {
        this(0);
    }
    
    public Request(long _id) {
        id = _id;
        this.latestStatus = RequestLifeCycle.REQUEST_LIFECYCLE_CREATED;
    }
    
    
    
    // -------------------------
    // Public methods
    // -------------------------
    
    /**
     * @hibernate.id
     *          column="REQUEST_ID"
     *          type="long"
     *          not-null="true"
     *          unsaved-value="0"
     *          generator-class="sequence"
     *          
     * @hibernate.generator-param
     *          name="sequence"
     *          value="proc_request_sequence"          
     */
    public final long getRequestId() {
        return id;
    }

    public final void setRequestId(long _id) {
        id = _id;
    }

    /**
     * @hibernate.property
     *          column="REQUEST_LABEL"
     *          type="string"
     *          length="256"
     *          not-null="false"
     */
    public final String getLabel() {
        return label;
    }

    public final void setLabel(String _label) {
        label = _label;
    }

    /**
     * @hibernate.map
     *          table="PROC_REQUEST_INFO"
     *          cascade="delete-orphan"
     *          inverse="false"
     *          
     * @hibernate.collection-key
     *          column="REQUEST_ID"
     *          
     * @hibernate.collection-index
     *          column="INFO_KEY"
     *          type="string"
     *          length="64"
     *          
     * @hibernate.collection-element
     *          column="INFO_VALUE"
     *          type="string"
     *          length="256"
     *          not-null="false"                              
     */
    public final Map getAdditionalInformation() {
        if (additionalInfo == null) {
        	this.additionalInfo = new HashMap();
        }
        return this.additionalInfo;
    }

    public final void setAdditionalInformation(Map _additionalInfo) {
        additionalInfo = _additionalInfo;
    }

    /**
     * @hibernate.bag
     *          cascade="all-delete-orphan"
     *          inverse="true"
     *          order-by="TRAIL_DATETIME DESC" 
     *          
     * @hibernate.collection-key
     *          column="REQUEST_ID"
     *          
     * @hibernate.collection-one-to-many
     *          class="br.com.auster.facelift.requests.model.Trail"                              
     */
    public final List getTrails() {
        if (trails == null) {
        	this.trails = new ArrayList();
        }
        return this.trails;
    }

    public final void setTrails(List _trail) {
        trails = _trail;
    }
    
    /**
     * @hibernate.set
     *          cascade="delete-orphan"
     *          inverse="true"
     *          
     * @hibernate.collection-key
     *          column="REQUEST_ID"
     *          
     * @hibernate.collection-one-to-many
     *          class="br.com.auster.facelift.requests.model.InputFile"                              
     */
    public final Set getInputFiles() {
        return inFiles;
    }

    public final void setInputFiles(Set _inputFiles) {
        inFiles = _inputFiles;
    }
       
	/**
	 * Returns a list of <code>OutputFile</code> objects, that were generated during the process
	 * 	of this request.
	 * 
	 * @return the list of generated objects
	 */
    public List getRelatedGeneratedFiles() {
        if (getTrails() == null) { 
            return Collections.EMPTY_LIST;
        }
        List relatedFiles = new ArrayList();
        for (Iterator iterator = getTrails().iterator(); iterator.hasNext(); ) {
            Trail trail = (Trail) iterator.next();
            if (trail.getOutputFiles() != null) {
                relatedFiles.addAll(trail.getOutputFiles());
            }
        }
        return relatedFiles;
    }  
    
    /**
     * @hibernate.property
     *          column="LATEST_STATUS"
     *          type="integer"
     *          not-null="true"
     */
    public final int getLatestStatus() {
        return latestStatus;
    }

    public final void setLatestStatus(int _status) {
        latestStatus = _status;
    }    
    
    
    // -------------------------
    // Object overwrites
    // -------------------------

    public boolean equals(Object _other) {
        try {
            return (((Request) _other).getRequestId() == this.getRequestId() && (this.getRequestId() != 0));
        } catch (ClassCastException cce) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        result = 37*result + (int) this.getRequestId();
        return result;
    }

    public String toString() {
        return "[" + getClass().getName() + " : " +
               " id=" + getRequestId() +
               " label=" + getLabel() +
               " addInfo=" + getAdditionalInformation() +
               " trails=" + getTrails() + 
               " inputFiles=" + getInputFiles() +
               " latestStatus=" + latestStatus +
               "]";
    }        
    
    
    
    // -------------------------
    // Comparable overwrites
    // -------------------------
        
    public int compareTo(Object _other) {
        try {
            return (int) (this.getRequestId() - ((Request) _other).getRequestId());
        } catch (ClassCastException cce) {
            return 0;
        } catch (NullPointerException npe) {
            return 0;
        }
    }    
}
