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
 * Created on Sep 05, 2005
 */
package br.com.auster.facelift.queries.builder;


import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.auster.facelift.queries.interfaces.QueryFunctions;
import br.com.auster.facelift.queries.model.ColumnObject;
import br.com.auster.facelift.queries.model.Query;
import br.com.auster.facelift.queries.model.ViewObject;

/**
 * 
 * @author framos
 * @version $Id: AbstractQueryImpl.java 181 2005-12-16 11:15:30Z framos $
 */
public abstract class AbstractQueryImpl implements Query {

	
	
	
    // ----------------------
    // Class variables
    // ----------------------
    
    protected static Map FUNCTION_PATTERNS;    

	static {
		
		FUNCTION_PATTERNS = new HashMap(20);
		
		FUNCTION_PATTERNS.put(QueryFunctions.BOOLEAN_AND, "and");
		FUNCTION_PATTERNS.put(QueryFunctions.BOOLEAN_OR, "or");
		FUNCTION_PATTERNS.put(QueryFunctions.BOOLEAN_NOT, "not");
		
		FUNCTION_PATTERNS.put(QueryFunctions.COLUMN_RAW_VALUE, "{0}");

		FUNCTION_PATTERNS.put(QueryFunctions.NUMERIC_EQUAL, "= {0}");
		FUNCTION_PATTERNS.put(QueryFunctions.NUMERIC_GREATER, "> {0}");
		FUNCTION_PATTERNS.put(QueryFunctions.NUMERIC_GREATER_EQUAL, ">= {0}");
		FUNCTION_PATTERNS.put(QueryFunctions.NUMERIC_LOWER, "< {0}");
		FUNCTION_PATTERNS.put(QueryFunctions.NUMERIC_LOWER_EQUAL, "<= {0}");
		FUNCTION_PATTERNS.put(QueryFunctions.NUMERIC_NOT_EQUAL, "<> {0}");
		
		FUNCTION_PATTERNS.put(QueryFunctions.STRING_LIKE, "like {0}");
		FUNCTION_PATTERNS.put(QueryFunctions.STRING_NOT_LIKE, "not like {0}");
		FUNCTION_PATTERNS.put(QueryFunctions.STRING_NOT_NULL, "is not null");
		FUNCTION_PATTERNS.put(QueryFunctions.STRING_NULL, "is null");

		FUNCTION_PATTERNS.put(QueryFunctions.MATH_ABSOLUTE, "abs({0})");
		FUNCTION_PATTERNS.put(QueryFunctions.MATH_COUNT, "count({0})");
		FUNCTION_PATTERNS.put(QueryFunctions.MATH_AVERAGE, "avg({0})");
		FUNCTION_PATTERNS.put(QueryFunctions.MATH_MAX, "max({0})");
		FUNCTION_PATTERNS.put(QueryFunctions.MATH_MIN, "min({0})");
		
		FUNCTION_PATTERNS.put(QueryFunctions.DATE_NOW, "not-defined");
		
	}
	

	
    // ----------------------
    // Instance variables
    // ----------------------
	
	protected List queriedColumns;
	protected ViewObject sourceView;
	protected QueryCondition condition;
	protected List orderList;
	protected Set columnCache;
	
	
	
    // ----------------------
    // Constructors
    // ----------------------
	
	protected AbstractQueryImpl() {
		// force keeping order of elements
		queriedColumns = new LinkedList();
		condition = null;
		orderList = new LinkedList();
		columnCache = new HashSet();
	}

	
	
    // ----------------------
    // Public methods
    // ----------------------
	
	public String getSQLStatement() {
		String conditionSQL = buildRelationshipList() + " " + buildConditions();
		String querySQL = "SELECT " + buildFieldsSelection() + " FROM "  + buildTablesList();
		querySQL += (conditionSQL.trim().length() > 0 ? " WHERE " + conditionSQL : "");		
		querySQL += buildGroupBy();
		querySQL += buildOrderBy();
		return querySQL;
 	}

	public void addColumn(String _functionCode, ColumnObject _column) {
		queriedColumns.add(new QueryField(_functionCode, _column));
		columnCache.add(_column);
	}
	
	public Collection getQueriedColumns() {
		return queriedColumns;
	}
	
	public void setSourceView(ViewObject _view) {
		sourceView = _view;
	}
	
	public ViewObject getSourceView() {
		return sourceView;
	}
	
	public void and(String _leftFunctionCode, ColumnObject _leftColumn, String _operator) {
		and(_leftFunctionCode, _leftColumn, _operator, null);
	}
	
	public void and(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, Object _value) {
		SimpleQueryCondition c = new SimpleQueryCondition(_leftFunctionCode, _leftColumn, _operator, _value);
		rebuildCurrentCondition(c, QueryFunctions.BOOLEAN_AND);
	}
	
	public void and(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, String _rightFunctionCode, ColumnObject _rightColumn) {
		ColumnComparisonQueryCondition c = new ColumnComparisonQueryCondition(_leftFunctionCode, _leftColumn, _operator, _rightFunctionCode, _rightColumn);
		rebuildCurrentCondition(c, QueryFunctions.BOOLEAN_AND);
	}

	public void or(String _leftFunctionCode, ColumnObject _leftColumn, String _operator) {
		or(_leftFunctionCode, _leftColumn, _operator, null);
	}
	
	public void or(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, Object _value) {
		SimpleQueryCondition c = new SimpleQueryCondition(_leftFunctionCode, _leftColumn, _operator, _value);
		rebuildCurrentCondition(c, QueryFunctions.BOOLEAN_OR);
	}
	public void or(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, String _rightFunctionCode, ColumnObject _rightColumn) {
		ColumnComparisonQueryCondition c = new ColumnComparisonQueryCondition(_leftFunctionCode, _leftColumn, _operator, _rightFunctionCode, _rightColumn);
		rebuildCurrentCondition(c, QueryFunctions.BOOLEAN_OR);
	}

	public String formatFunction(String _functioncode, Object _value) {
		String pattern = (String) FUNCTION_PATTERNS.get(_functioncode);
		if (pattern == null) {
			if (_value != null) {
				return _value.toString();
			}
			return "";
		}
		return MessageFormat.format(pattern, new Object[] { _value } );
	}
	
	public void clearOrderList() {
		orderList.clear();
	}
	
	public void orderBy(ColumnObject _column, boolean _isAscending) {
		orderList.add( new OrderByElement(_column, _isAscending));
	}
	
	
	
    // ----------------------
    // Protected methods
    // ----------------------
	
	protected void rebuildCurrentCondition(QueryCondition _newCondition, String _booleanOperator) {
		if (condition == null) {
			condition = _newCondition;
		} else {
			condition = new MultipleQueryCondition(condition, _booleanOperator, _newCondition);
		}
	}
	
	protected String buildFieldsSelection() {
		String colListSql = "";
		Object[] params = new Object[1];
		for (Iterator iterator=queriedColumns.iterator(); iterator.hasNext();) {
			QueryField col = (QueryField) iterator.next();
			params[0] = null;
			if (col.getColumn() != null) {
				params[0] = col.getColumn().getQualifiedName();
			}
			colListSql += MessageFormat.format((String)FUNCTION_PATTERNS.get(col.getQueryFunction()), params) + ", "; 
		}
		//removes last coma
		return colListSql.substring(0, colListSql.length()-2);
	}
	
	protected String buildTablesList() {
		String tableListSql = "";
		for (Iterator iterator=sourceView.getTables().iterator(); iterator.hasNext();) {
			String table = (String) iterator.next();
			tableListSql += table + ", ";
		}
		//removes last coma
		return tableListSql.substring(0, tableListSql.length()-2);
	}
	
	protected String buildRelationshipList() {
		String relatListSql = "";
		String andString = " " + MessageFormat.format((String)FUNCTION_PATTERNS.get(QueryFunctions.BOOLEAN_AND), null) + " ";
		int counter=1;
		for (Iterator iterator=sourceView.getRelationships().iterator(); iterator.hasNext(); counter++) {
			ViewObject.Relationship relation = (ViewObject.Relationship) iterator.next();
			ColumnObject from = relation.getFromColumn();
			ColumnObject to = relation.getToColumn();
			relatListSql += from.getQualifiedName() + " = " + to.getQualifiedName();
			if (counter < sourceView.getRelationships().size()) {
				relatListSql += andString;
			}
		}
		//removes last coma
		return relatListSql;
	}
	
	protected String buildConditions() {
		if (condition == null) { return ""; }
		String resultCondition = condition.getSQLStatement(this);
		if ((sourceView.getRelationships() != null) && (sourceView.getRelationships().size() > 0)) {
			resultCondition = MessageFormat.format((String)FUNCTION_PATTERNS.get(QueryFunctions.BOOLEAN_AND), null) + " " + resultCondition; 
		}
		return resultCondition;
	}
		
	protected String buildGroupBy() {
		String groupSql = "";
		int counter = 0;
		int counterNull = 0;
		for (Iterator iterator=queriedColumns.iterator(); iterator.hasNext();) {
			QueryField col = (QueryField) iterator.next();
			if (! QueryFunctions.GROUPABLE_FUNCTIONS.contains(col.getQueryFunction())) {
				counter++;
				if (col.getColumn() != null) {
					groupSql += col.getColumn().getQualifiedName()  + ", ";
				} else {
					counter--;
					counterNull++;
				}
			}			 
		}
		// meaning there is no column with groupable function associated 
		if ((counter == (queriedColumns.size()-counterNull)) || (counter == 0)) {
			return "";
		}
		//removes last coma
		return (" GROUP BY " + groupSql.substring(0, groupSql.length()-2));
	}

	/**
	 * If order column is not in the selected field list, it wont be generated in the SQL statement
	 */
	protected String buildOrderBy() {
		String orderSQL = "";
		for (Iterator it=orderList.iterator(); it.hasNext();) {
			OrderByElement order = (OrderByElement) it.next();
			if (!columnCache.contains(order.getColumn())) {
				// discard column
				continue;
			}
			orderSQL += order.getColumn().getQualifiedName() + (order.isAscendingOrder() ? " ASC" : " DESC") + ",";
		}
		if (orderSQL.trim().length() > 0) { 
			orderSQL = " ORDER BY " + orderSQL.substring(0, orderSQL.length()-1);
		}
		return orderSQL;
	}

	protected static class OrderByElement {
		private ColumnObject col;
		private boolean ascending;
		
		OrderByElement(ColumnObject _col, boolean _ascending) {
			col = _col;
			ascending = _ascending;
		}
		
		public ColumnObject getColumn() {
			return col;
		}
		
		public boolean isAscendingOrder() {
			return ascending;
		}
	}
}
