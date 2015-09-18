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
 * Created on May 2, 2005
 */
package br.com.auster.facelift.queries.dao;

import java.io.Serializable;

/**
 * Represents one single row, selected by the executed query. Each cell holds the information of a specific field, the order being
 * 	the one of the selection of the fields.
 * 
 * @author framos
 * @version $Id$
 */
public class SelectedRow implements Serializable {

	
	
    // ----------------------
    // Instance variables
    // ----------------------
	
    private Object[] cells;
    
    
    
    // ----------------------
    // Constructors
    // ----------------------
	
	public SelectedRow(int _rowLen) {
        cells = new Object[_rowLen];
    }

	
	
    // ----------------------
    // Public methods
    // ----------------------
	
    public final void setCell(int _pos, Object _value) {
        if ((_pos >= 0) && (_pos < cells.length)) {
            cells[_pos] = _value;
        } else {
            throw new ArrayIndexOutOfBoundsException("cannot access postition " + _pos);
        }
    }
    
    public final Object getCell(int _pos) {
        if ((_pos >= 0) && (_pos < cells.length)) {
            return cells[_pos];
        }      
        throw new ArrayIndexOutOfBoundsException("cannot access postition " + _pos);
    }
    
	public String toString() {
		String toString = "SelectedRow {";
		for (int i=0; i < cells.length; i++) {
			toString += cells[i] + ";";
		}
		return toString + "]";
	}
}
