/*
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import br.com.auster.facelift.queries.interfaces.QueryException;

/**
 * Holds definition information about a single database table, along with information to be used
 * 	when opening connections to such database.
 * 
 * @author framos
 * @version $Id$
 */
public class ViewObject extends VisibleDefinitionObject {

    
    // ----------------------
    // Instance variables
    // ----------------------

    private Set columns;
	private Set tables;
	private Set relations;
	private Map columnMap;
	private String dbSource;
    
    

    // ----------------------
    // Constructors
    // ----------------------

	public ViewObject(String _name) {
        super(_name);
        
        columns = new HashSet();
        tables = new HashSet();
        relations = new HashSet();
		
		columnMap = new HashMap();
    }

	
    
    // ----------------------
    // Public methods
    // ----------------------
    
    public final Set getColumns() {
        return Collections.unmodifiableSet(columns);
    }
	
    public final void addColumn(ColumnObject _col) {
        columns.add(_col);
		columnMap.put(_col.getQualifiedName(), _col);
		
		if (! tables.contains(_col.getOwnerTable())) {
			tables.add(_col.getOwnerTable());
		}
    }

	public final void setColumns(Set _cols) {
		if (_cols == null) { return; }
		tables.clear();
		columns.clear();
		columnMap.clear();
		for (Iterator iterator = _cols.iterator(); iterator.hasNext();) {
			addColumn((ColumnObject)iterator.next());
		}
	}
	
	public final ColumnObject getColumn(String _qualifiedName) {
		return (ColumnObject) columnMap.get(_qualifiedName);
	}
	
	public final Set getTables() {
		return Collections.unmodifiableSet(tables);
	}
	
	public final Set getRelationships() {
		return Collections.unmodifiableSet(relations);
	}
	
	public final void addRelation(ColumnObject _from, ColumnObject _to) throws QueryException {
		addRelation(new ViewObject.Relationship(_from, _to));
	}
	
	public final void addRelation(Relationship _relation) {
		relations.add(_relation);
	}
	
	public final void setRelations(Set _relations) {
		if (_relations == null) { return; }
		relations.clear();
		for (Iterator iterator = _relations.iterator(); iterator.hasNext();) {
			addRelation((Relationship)iterator.next());
		}
	}
	
	public final void setDataSource(String _ds) {
		dbSource = _ds;	
	}
	
	public final String getDataSource() {
		return dbSource;
	}	
	
	public static class Relationship {
		
		private ColumnObject from;
		private ColumnObject to;
		
		public Relationship(ColumnObject _from, ColumnObject _to) throws QueryException {
			if ((_from == null) || (_to == null)) {
				throw new QueryException("cannot set relationship with non-configured column");
			}
			from = _from;
			to = _to;
			
		}
		
		public final ColumnObject getFromColumn() {
			return from;
		}
		
		public final ColumnObject getToColumn() {
			return to;
		}
	}
}
