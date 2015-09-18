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
 * Created on 09/03/2006
 */
package br.com.auster.facelift.cycle.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**

 *          
 * @author framos
 * @version $Id$
 */
public class CycleProcessingId implements Serializable, Comparable {

	
	private long cycleId;
	private String procId;
	private Timestamp insertDate;
	
	
	
	public CycleProcessingId() {
	}
	
	public CycleProcessingId(long _cycleId, String _procId) {
		setCycleId(_cycleId);
		setProcessingId(_procId);
	}

	
	public String getProcessingId() {
		return procId;
	}

	public void setProcessingId(String _procId) {
		this.procId = _procId;
	}
	
	public long getCycleId() {
		return cycleId;
	}

	public void setCycleId(long _cycleId) {
		this.cycleId = _cycleId;
	}

	public Timestamp getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Timestamp _insertDate) {
		this.insertDate = _insertDate;
	}

	public boolean equals(Object _obj) {
		try {
			CycleProcessingId c = (CycleProcessingId)_obj;
			return this.getCycleId() == c.getCycleId() && 
			       this.getProcessingId().equals(c.getProcessingId());
		} catch (ClassCastException cce) {
			return false;
		}
	}
	
    public int hashCode() {
        int result = 17;
        result = 37*result + (int)this.getCycleId();
        result = 37*result + this.getProcessingId().hashCode();
        return result;
    }
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object _obj) {
		try {
			CycleProcessingId c = (CycleProcessingId)_obj;
			if (this.getCycleId() == c.getCycleId()) {
				return this.getProcessingId().compareTo(c.getProcessingId());
			}
			return (int) (this.getCycleId() - c.getCycleId());
		} catch (ClassCastException cce) {
			return 0;
		}
	}
}
