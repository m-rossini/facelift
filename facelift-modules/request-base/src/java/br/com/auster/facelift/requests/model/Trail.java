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
 * Created on Feb 23, 2005
 */
package br.com.auster.facelift.requests.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents each status change of a processing request. Also, as optional parameters, it can have an informational message 
 * 	and a list of files generated during this status change.   
 * 
 * @hibernate.class
 *          table="PROC_REQUEST_TRAIL"
 *          
 * @author framos
 * @version $Id$
 */
public class Trail implements Serializable, Comparable  {

    

    // -------------------------
    // Instance variables
    // -------------------------
    
    private long trailId;
    private int status;
    private String msg;
    private Timestamp trailDate;
    
    private List outputFiles; 
    
    private Request parent;
    
    
    
    // -------------------------
    // Constructors
    // -------------------------
    
    public Trail() {
        this(0);
    }
    
    public Trail(long _id) {
        trailId = _id;
        trailDate = null;
        status = -1;
        outputFiles = new ArrayList();
        parent = null;
    }
    
    
    
    // -------------------------
    // Public methods
    // -------------------------
    
    /**
     * @hibernate.id
     *          column="TRAIL_ID"
     *          type="long"
     *          not-null="true"
     *          unsaved-value="0"
     *          generator-class="sequence"
     *          
     * @hibernate.generator-param
     *          name="sequence"
     *          value="proc_trail_sequence"
     */
    public final long getTrailId() {
        return trailId;
    }

    public final void setTrailId(long _id) {
        trailId = _id;
    }

    /**
     * @hibernate.property
     *          column="STATUS_ID"
     *          type="integer"
     *          not-null="true"
     */
    public final int getStatus() {
        return status;
    }

    public final void setStatus(int _status) {
        status = _status;
    }

    /**
     * @hibernate.property
     *          column="TRAIL_DATETIME"
     *          type="timestamp"
     *          not-null="true"
     */
    public final Timestamp getTrailDate() {
        return trailDate;
    }

    public final void setTrailDate(Timestamp _date) {
        trailDate = _date;
    }

    /**
     * @hibernate.property
     *          column="TRAIL_MESSAGE"
     *          type="string"
     *          length="512"
     *          not-null="false"
     */
    public final String getMessage() {
        return msg;
    }

    public final void setMessage(String _message) {
        msg = _message;
    }

    /**
     * @hibernate.bag
     *          cascade="all-delete-orphan"
     *          inverse="true"
     *          
     * @hibernate.collection-key
     *          column="TRAIL_ID"
     *          
     * @hibernate.collection-one-to-many
     *          class="br.com.auster.facelift.requests.model.OutputFile"                              
     */
    public final List getOutputFiles() {
        return outputFiles; 
    }

    public final void setOutputFiles(List _output) {
        outputFiles = _output;
    }

    /**
     * @hibernate.many-to-one
     *          column="REQUEST_ID"
     *          not-null="true"         
     */
    public final Request getRequest() {
        return parent;
    }
    
    public final void setRequest(Request _entity) {
        parent = _entity;
    }
    
    
    
    // -------------------------
    // Object overwrites
    // -------------------------
    
    public boolean equals(Object _other) {
        try {
            Trail other = (Trail) _other;
            return ((this.getTrailId() == other.getTrailId())  && (this.getTrailId() != 0));
        } catch (ClassCastException cce) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }    
    }
    
    public int hashCode() {
        int result = 17;
        result = 37*result + (int) this.getTrailId();
        return result;
    }
    
    public String toString() {
        return "[" + getClass().getName() + " : " +
               " trailId=" + getTrailId() +
               " status=" + getStatus() +
               " message=" + getMessage() +
               " date/time=" + getTrailDate() +
               " outputFiles=" + getOutputFiles() +
               " parent=" + (getRequest() != null ? getRequest().getRequestId() : -1) +
               "]";
    }     
    
    
    
    // -------------------------
    // Comparable overwrites
    // -------------------------
    
    public int compareTo(Object _other) {
        try {
            Trail other = (Trail) _other;
            return (int) (this.getTrailId() - other.getTrailId());
        } catch (ClassCastException cce) {
            return 0;
        } catch (NullPointerException npe) {
            return 0;
        }
        
    }
 
}
