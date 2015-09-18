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

import java.io.Serializable;
import java.util.Date;

/**
 * Possible values used to filter web requests or their processing requests, when reuming the process of requests. 
 * 
 * @author framos
 * @version $Id$
 */
public class ResumeCriteria implements Serializable {

    

    // -------------------------
    // Instance variables
    // -------------------------
    
    private Date creationDate;
    //private boolean asPrevious;
    private int limit;
   
   
    
    // -------------------------
    // Constructor
    // -------------------------
    
    public ResumeCriteria() {
        limit = -1;
//        asPrevious = false;
        creationDate = null;
    }



    // -------------------------
    // Public methods
    // -------------------------

    public final Date getCreationDate() {
        return creationDate;
    }

    public final void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public final int getLimit() {
        return limit;
    }
    
    public final void setLimit(int limit) {
        this.limit = limit;
    }
    
//    public final boolean getAsPrevious() {
//        return asPrevious;
//    }
//    
//    public final void setAsPrevious(boolean _bool) {
//        asPrevious = _bool;
//    }

}
