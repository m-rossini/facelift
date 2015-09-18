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
package br.com.auster.facelift.requests.interfaces;

import java.io.Serializable;
import java.util.Date;

/**
 * Possible values used as filter when selecting lists of processing request.
 * 
 * @author framos
 * @version $Id$
 */
public class RequestCriteria implements Serializable {

	
	
    // -------------------------
    // Instance variables
    // -------------------------
	
    private String label;
    
    private Date startDate;
    private Date endDate;
    
    private int status;

    
    
    // -------------------------
    // Constructors
    // -------------------------

	public RequestCriteria() {
        this.label = null;
        this.startDate = null;
        this.endDate = null;
        this.status = -1;
    }

	

    // -------------------------
    // Public methods
    // -------------------------
	
    public final Date getEndDate() {
        return endDate;
    }
    
    public final void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public final Date getStartDate() {
        return startDate;
    }

    public final void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public final String getLabel() {
        return label;
    }
    public final void setLabel(String label) {
        this.label = label;
    }
    
    public final int getStatus() {
        return this.status;
    }
    
    public final void setStatus(int _status) {
        this.status = _status;
    }
}
