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
package br.com.auster.facelift.queries.builder;

import br.com.auster.facelift.queries.model.ColumnObject;
import br.com.auster.facelift.queries.model.Query;

/**
 * A simple query filtering condition, where one field is specified along with a comparison operator and a value to be
 *  compared with. This is the basic representation of a condition, and will be the ultimate class that will compound a
 *  more complex condition for a query.
 *  
 * @author framos
 * @version $Id: SimpleQueryCondition.java 158 2005-11-04 13:03:09Z framos $
 */
public class SimpleQueryCondition extends QueryCondition {


	
    // ----------------------
    // Instance variables
    // ----------------------
	
    private String functionCode;
    private ColumnObject column;
    private String operator;
    private Object value;
    
    
	
    // ----------------------
    // Constructors
    // ----------------------
	
	public SimpleQueryCondition(String _functionCode, ColumnObject _column, String _operator, Object _value) {
		setFunctionCode(_functionCode);
		setColumn(_column);
		setOperator(_operator);
		setValue(_value);
	}
	
	
	
    // ----------------------
    // Public methods
    // ----------------------
	
    public final ColumnObject getColumn() {
        return column;
    }
	
    public final void setColumn(ColumnObject column) {
        this.column = column;
    }
	
    public final String getOperator() {
        return operator;
    }
	
    public final void setOperator(String operator) {
        this.operator = operator;
    }
	
    public final Object getValue() {
        return value;
    }
	
    public final void setValue(Object value) {
        this.value = value;
    }
	
	public final String getFunctionCode() {
		return functionCode;
	}
	
	public final void setFunctionCode(String _code) {
		functionCode = _code;
	}
	
	public String getSQLStatement(Query _query) {
		return _query.formatFunction(functionCode, column.getQualifiedName()) + " " +
		 	   _query.formatFunction(operator, "'" + value + "'");
	}
}
