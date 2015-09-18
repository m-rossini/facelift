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
package br.com.auster.facelift.requests.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Holds information related to generated files, while processing some process request. This file may handle a group of
 * 	key-value attributes to identify the situation into which it was generated.
 * 
 * @hibernate.class
 *          table="PROC_REQUEST_OUTFILE"
 *              
 * @author framos
 * @version $Id$
 */
public class OutputFile implements Serializable, Comparable  {

    
    // -------------------------
    // Instance variables 
    // -------------------------

    private long fileId;
    private Trail trail;
    private String filename;
       
    private Map attributes;
    
    
    // -------------------------
    // Constructors 
    // -------------------------
    
    public OutputFile() {
        trail = null;
        filename = null;
    }

    
    
    // -------------------------
    // Public methods 
    // -------------------------

    /**
     * @hibernate.id
     *          column="FILE_ID"
     *          type="long"
     *          not-null="true"
     *          unsaved-value="0"
     *          generator-class="sequence"
     *
     * @hibernate.generator-param
     *          name="sequence"
     *          value="outfile_sequence"          
     */
    public final long getFileId() {
        return fileId;
    }
    
    public final void setFileId(long _id) {
        fileId = _id;
    }
    
    /**
     * @hibernate.many-to-one
     *          column="TRAIL_ID"
     *          not-null="true"         
     */
    public final Trail getTrail() {
        return trail;
    }

    public final void setTrail(Trail _trail) {
        trail = _trail;
    }

    /**
     * @hibernate.property
     *          column="FILENAME"
     *          type="string"
     *          length="512"
     *          not-null="true"
     */
    public final String getFilename() {
        return filename;
    }

    public final void setFilename(String _name) {
        filename = _name;
    }

    /**
     * @hibernate.map
     *          table="PROC_OUTFILE_ATTRS"
     *          cascade="delete-orphan"
     *          inverse="false"
     *          
     * @hibernate.collection-key
     *          column="FILE_ID"
     *          
     * @hibernate.collection-index
     *          column="ATTR_KEY"
     *          type="string"
     *          length="64"
     *          
     * @hibernate.collection-element
     *          column="ATTR_VALUE"
     *          type="string"
     *          length="256"
     *          not-null="false"                              
     */    
    public final Map getAttributes() {
        return this.attributes;
    }
    
    public final void setAttributes(Map _attr) {
        attributes = _attr;
    }
    
    public final Date getFileDate() {
        if (getTrail() != null) {
            return getTrail().getTrailDate();
        }
        return null;
    }
    
    public final String getMessage() {
        if (getTrail() != null) {
            return getTrail().getMessage();
        }
        return null;
    }
    

    
    // -------------------------
    // Comparable overwrites 
    // -------------------------

    public int compareTo(Object _other) {
        try {
            OutputFile other = (OutputFile) _other;
            if ((this.getTrail() == null) ^ (other.getTrail() == null)) {
                return 0;
            } else if (this.getTrail() != null) {
                int compare = this.getTrail().compareTo(other.getTrail());
                if (compare != 0) {
                    return compare;
                }
            }
            
            if ((this.getFilename() == null) ^ (other.getFilename() == null)) {
                return 0;
            } else if (this.getFilename() != null) {
                return this.getFilename().compareTo(other.getFilename());
            } 
            return 0;
            
        } catch (ClassCastException cce) {
            return 0;
        } catch (NullPointerException npe) {
            return 0;
        }
    }
    

    
    // -------------------------
    // Object overwrites 
    // -------------------------
    
    public int hashCode() {
        int result = 17;
        result = 37*result + (this.getTrail() != null ? this.getTrail().hashCode() : 0);
        result = 37*result + (this.getFilename() != null ? this.getFilename().hashCode() : 0);
        return result;        
    }

    public boolean equals(Object _other) {
        try {
            OutputFile other = (OutputFile) _other;
            if ((this.getTrail() == null) ^ (other.getTrail() == null)) {
                return false;
            } else if (this.getTrail() != null) {
                if (! this.getTrail().equals(other.getTrail()) ) {
                    return false;
                }
            }
            
            if ((this.getFilename() == null) ^ (other.getFilename() == null)) {
                return false;
            } else if (this.getFilename() != null) {
                return this.getFilename().equals(other.getFilename());
            } 
            return false;
            
        } catch (ClassCastException cce) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }
    
    public String toString() {
        return "[" + getClass().getName() + " : " +
               " filename=" + getFilename() +
               " attributes=" + getAttributes() + 
               "]";
    }
    

}
