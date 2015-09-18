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
 * Created on 07/03/2006
 */
package br.com.auster.facelift.cycle.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

/**
 * 
 * @hibernate.class
 *          table="CYCLE_INFO"
 *          
 * 
 * @author framos
 * @version $Id$
 */
public class Cycle implements Serializable, Comparable {


	
    // -------------------------
    // Instance variables
    // -------------------------    
	
	private long id;
	private String cycleCode;
	private Date cycleDate;
	private String cycleDescription;
	private Timestamp insertDate;
	private Timestamp lastInsert;

	private Collection processingIds;

	
	
    // -------------------------
    // Constructors
    // -------------------------    
	
	public Cycle() {
		this(0);
	}
	
	public Cycle(long _id) {
		this.id = _id;
		this.processingIds = new HashSet();
	}

	

    // -------------------------
    // Public methods
    // -------------------------    
    
    /**
     * @hibernate.id
     *          column="UID"
     *          type="long"
     *          not-null="true"
     *          unsaved-value="0"
     *          generator-class="sequence"
     *          
     * @hibernate.generator-param
     *          name="sequence"
     *          value="cycleinfo_sequence"          
     */	
	public long getCycleId() {
		return this.id;
	}
	
	public void setCycleId(long _id) {
		this.id = _id;
	}
	
    /**
     * @hibernate.property
     *          column="CYCLE_CODE"
     *          type="string"
     *          length="10"
     *          not-null="true"
     */
	public String getCycleCode() {
		return this.cycleCode;
	}
	
	public void setCycleCode(String _code) {
		this.cycleCode = _code;
	}
	
    /**
     * @hibernate.property
     *          column="CYCLE_DESCRIPTION"
     *          type="string"
     *          length="50"
     *          not-null="false"
     */
	public String getCycleDescription() {
		return this.cycleDescription;
	}
	
	public void setCycleDescription(String _desc) {
		this.cycleDescription = _desc;
	}

    /**
     * @hibernate.property
     *          column="CYCLE_DATE"
     *          type="timestamp"
     *          not-null="true"
     */	
	public Date getCycleDate() {
		return this.cycleDate;		
	}
	
	public void setCycleDate(Date _date) {
		this.cycleDate = _date;
	}

    /**
     * @hibernate.set
     *          cascade="all-delete-orphan"
     *          inverse="false"
     *          lazy="true"
     *          table="CYCLE_PROCESSING_IDS"
     *          
     * @hibernate.collection-key
     *          column="CYCLE_ID"
     *          
     * @hibernate.collection-one-to-many
     * 			class="br.com.auster.facelift.cycle.model.CycleProcessingId"
     */	
	public Collection getProcessingIds() {
		return this.processingIds;
	}
	
	public void addProcessingId(String _processingId) {
		addProcessingId(new CycleProcessingId(this.getCycleId(), _processingId));
	}

	public void addProcessingId(CycleProcessingId _processingInfo) {
		this.processingIds.add(_processingInfo);
	}
		
	public void setProcessingIds(Collection _ids) {
		this.processingIds = _ids;		
	}

    /**
     * @hibernate.property
     *          column="INSERT_DATE"
     *          type="timestamp"
     *          not-null="true"
     */
	public Timestamp getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Timestamp _insertDate) {
		this.insertDate = _insertDate;
	}
	
    /**
     * @hibernate.property
     *          column="LAST_INSERT"
     *          type="timestamp"
     *          not-null="true"
     */
	public Timestamp getLastInsert() {
		return lastInsert;
	}

	public void setLastInsert(Timestamp _insertDate) {
		this.lastInsert = _insertDate;
	}	
	
	
    // -------------------------
    // Object overwrites
    // -------------------------
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object _obj) {
		try {
			Cycle c = (Cycle)_obj;
			return c.getCycleId() == this.getCycleId();
		} catch (ClassCastException cce) {
			return false;
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode() {
        int result = 17;
        result = 37*result + (int) this.getCycleId();
        return result;
    }

    
    
    // -------------------------
    // Comparable overwrites
    // -------------------------
    
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object _obj) {
		try {
			Cycle c = (Cycle)_obj;
			return (int) (this.getCycleId()-c.getCycleId());
		} catch (ClassCastException cce) {
			return 0;
		}
	}

}
