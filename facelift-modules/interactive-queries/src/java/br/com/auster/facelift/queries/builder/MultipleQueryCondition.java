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
package br.com.auster.facelift.queries.builder;

import br.com.auster.facelift.queries.interfaces.QueryFunctions;
import br.com.auster.facelift.queries.model.Query;

/**
 * Multiple query filtering conditions are conditions made out of a group of other conditions. It allows that more than one 
 * 	condition be defined for a query. So, as an example, we can say that the expression <code>A && B || C</code>, where A, B
 *  and C are different conditions, is a multiple condition.
 *  <P>
 * In multiple conditions, all the conditions are evaluated by themselves before matching the boolean operation that connects
 * 	them in the query. After being evaluated the conditions that are part of this multiple one, they are submitted to the boolean 
 *  operators which connects them in the order by wchich they were added. 
 * 
 * @author framos
 * @version $Id: MultipleQueryCondition.java 158 2005-11-04 13:03:09Z framos $
 */
public class MultipleQueryCondition extends QueryCondition {

	
	
    // ----------------------
    // Instance variables
    // ----------------------
	
    private QueryCondition rightSide;
    private QueryCondition leftSide;
    private String operator = QueryFunctions.BOOLEAN_OR;

    
	
    // ----------------------
    // Constructors
    // ----------------------
	
	public MultipleQueryCondition(QueryCondition _leftSide, String _operator, QueryCondition _rightSide) {
		setLeftSide(_leftSide);
		setBooleanOperator(_operator);
		setRightSide(_rightSide);
	}
	
    // ----------------------
    // Public methods
    // ----------------------
	
    public final void setRightSide(QueryCondition _condition) {
        rightSide = _condition;
    }
    public final QueryCondition getRightSide() {
        return rightSide;
    }

    public final void setLeftSide(QueryCondition _condition) {
        leftSide = _condition;
    }
    public final QueryCondition getLeftSide() {
        return leftSide;
    }
    
    public final String getBooleanOperator() {
        return operator;
    }
	
    public final void setBooleanOperator(String _condition) {
        if (QueryFunctions.BOOLEAN_OR.equals(_condition) ||
            QueryFunctions.BOOLEAN_AND.equals(_condition)) {
            operator = _condition;
        }
    }
        
    /**
     * Inherited from <code>QueryCondition</code>
     */
    public String getSQLStatement(Query _query) {
        return leftSide.getSQLStatement(_query) + " " + 
			   _query.formatFunction(getBooleanOperator(), null) + " " + 
			   rightSide.getSQLStatement(_query);
    }

}
