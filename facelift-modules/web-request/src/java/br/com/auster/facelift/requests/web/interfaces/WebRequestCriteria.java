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
 * Created on Apr 5, 2005
 */
package br.com.auster.facelift.requests.web.interfaces;



import java.io.Serializable;
import java.util.Date;


/**
 * Possible values used to filter when searching for web requests or one of its related collections.
 * 
 * @author framos
 * @version $Id$
 */
public class WebRequestCriteria implements Serializable {

	
    
    // -------------------------
    // Instance variables
    // -------------------------

	private long ownerId;
    private String groupName; 
    private Date startDate;
    private Date endDate;
	
	private int status;
    
    
    /** 
     * Used to store the values of  <code>statusExclude</code>.
     * This variable contains status value to exclude in sql query.
     */
    private int statusExclude;
    
    
    
    // -------------------------
    // Public methods
    // -------------------------

	public final Date getEndDate() {
        return endDate;
    }
    
    public final void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public final long getOwnerId() {
        return ownerId;
    }
    
    public final void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
    
    public final Date getStartDate() {
        return startDate;
    }
    
    public final void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
 
	public final void setStatus(int _status) {
		status = _status;
	}
	
	public final int getStatus() {
		return status;
	}
	
	public final void setGroupName(String _name) {
		groupName = _name;
	}
	
	public final String getGroupName() {
		return groupName;
	}

    /**
     * Return the value of a attribute<code>statusExclude</code>.
     * @return return the value of <code>statusExclude</code>.
     */
    public int getStatusExclude() {
        return statusExclude;
    }

    /**
     * Set a value of <code>statusExclude</code>.
     * @param statusExclude
     */
    public void setStatusExclude(int statusExclude) {
        this.statusExclude = statusExclude;
    }
}
