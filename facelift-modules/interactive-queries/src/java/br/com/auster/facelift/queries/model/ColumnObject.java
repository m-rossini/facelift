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
 * Created on Apr 29, 2005
 */
package br.com.auster.facelift.queries.model;


/**
 * Represents the definition information of a table column. It holds information of the column physical name, a 
 * 	user-friendly displayable name and its data type.
 * 
 * @author framos
 * @version $Id$
 */
public class ColumnObject extends VisibleDefinitionObject {

	
   
    // ----------------------
    // Instance variables
    // ----------------------
	
    private String type; 
	private String table; 
	private boolean visible;
    
    
	
    // ----------------------
    // Constructors
    // ----------------------

	public ColumnObject(String _name) {
        super(_name);
    }
    

	
    // ----------------------
    // Public methods
    // ----------------------
	
    public final String getType() {
        return type;
    }    
    public final void setType(String _type) {
        type = _type;
    }
	
	public final boolean isVisible() {
		return visible;
	}
	public final void setVisible(boolean _visible) {
		visible = _visible;
	}

	public final String getOwnerTable() {
		return table;
	}
	
	public final void setOwnerTable(String _table) {
		table = _table;
	}

	public final String getQualifiedName() {
		return getOwnerTable() + "." + getName();
	}
	
	public String toString() {
		return "[ColumnObject]" + super.toString() +
		       " table=" + table + 
		       " visible=" + visible +
		       " type=" + type;
	}
	
	public boolean equals(Object _other) {
		try {
			ColumnObject c = (ColumnObject) _other;
			return super.equals(_other) &&
			       this.getOwnerTable().equals(c.getOwnerTable());
        } catch (ClassCastException cce) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
	}
}
