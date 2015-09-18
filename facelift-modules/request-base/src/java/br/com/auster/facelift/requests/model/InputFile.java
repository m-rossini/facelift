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

/**
 * Holds information related to a source file, for some processing request.
 * 
 * @author framos
 * @version $Id$
 */
public class InputFile implements Serializable, Comparable  {


    
    // -------------------------
    // Instance variables
    // -------------------------
    
    private Request request;
    private String filename;

    

    // -------------------------
    // Constructors 
    // -------------------------
    
    public InputFile() {
        this(null);
    }
    
    public InputFile(Request _request) {
        setRequest(_request);
        filename = null;
    }

    
    
    // -------------------------
    // Public methods 
    // -------------------------
    
    public final Request getRequest() {
        return request;
    }

    public final void setRequest(Request _request) {
        request = _request;
    }


    public final String getFilename() {
        return filename;
    }

    public final void setFilename(String _name) {
        filename = _name;
    }

    
    
    // -------------------------
    // Object overwrites
    // -------------------------
    
    public boolean equals(Object _other) {
        try {
            InputFile other = (InputFile) _other;
            if ((this.getRequest() == null) ^ (other.getRequest() == null)) {
                return false;
            } else if (this.getRequest() != null) {
                if (! this.getRequest().equals(other.getRequest()) ) {
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
    
    public int hashCode() {
        int result = 17;
        result = 37*result + (this.getRequest() != null ? this.getRequest().hashCode() : 0);
        result = 37*result + (this.getFilename() != null ? this.getFilename().hashCode() : 0);
        return result;        
    }
    
    public String toString() {
        return "[" + getClass().getName() + " : " +
               " filename=" + getFilename() +
               "]";
    }    

    
    
    // -------------------------
    // Comparable overwrites
    // -------------------------
    
    public int compareTo(Object _other) {
        try {
            InputFile other = (InputFile) _other;
            if ((this.getRequest() == null) ^ (other.getRequest() == null)) {
                return 0;
            } else if (this.getRequest() != null) {
                int compare = this.getRequest().compareTo(other.getRequest());
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
}
