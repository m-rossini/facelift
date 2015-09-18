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
 * @version $Id: ColumnComparisonQueryCondition.java 158 2005-11-04 13:03:09Z framos $
 */
public class ColumnComparisonQueryCondition extends QueryCondition {


	
    // ----------------------
    // Instance variables
    // ----------------------
	
    private String leftFunctionCode;
    private ColumnObject leftColumn;
    private String operator;
    private String rightFunctionCode;
    private ColumnObject rightColumn;

    
    
    // ----------------------
    // Constructors
    // ----------------------
	
	public ColumnComparisonQueryCondition(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, String _rightFunctionCode, ColumnObject _rightColumn) {
		setLeftFunctionCode(_leftFunctionCode);
		setLeftColumn(_leftColumn);
		setOperator(_operator);
		setRightFunctionCode(_rightFunctionCode);
		setRightColumn(_rightColumn);
	}
	
	
	
	// ----------------------
    // Public methods
    // ----------------------
	
    public final ColumnObject getLeftColumn() {
        return leftColumn;
    }
	
    public final void setLeftColumn(ColumnObject column) {
        this.leftColumn = column;
    }
	
	public final String getLeftFunctionCode() {
		return leftFunctionCode;
	}
	
	public final void setLeftFunctionCode(String _code) {
		leftFunctionCode = _code;
	}
	
	public final String getOperator() {
        return operator;
    }
	
    public final void setOperator(String operator) {
        this.operator = operator;
    }

    public final ColumnObject getRightColumn() {
        return rightColumn;
    }
	
    public final void setRightColumn(ColumnObject column) {
        this.rightColumn = column;
    }
	
	public final String getRightFunctionCode() {
		return rightFunctionCode;
	}
	
	public final void setRightFunctionCode(String _code) {
		rightFunctionCode = _code;
	}
	
	public String getSQLStatement(Query _query) {
		String rightSide = _query.formatFunction(rightFunctionCode, rightColumn.getQualifiedName());
		rightSide = _query.formatFunction(operator, rightSide);
		return _query.formatFunction(leftFunctionCode, leftColumn.getQualifiedName()) + " " + rightSide;
	}
}
