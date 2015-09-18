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

import br.com.auster.facelift.queries.model.Query;

/**
 * Represents a composed query filtering condition. A composed condition is a specific kind of multiple condition, where all its 
 * 	inner conditions must be matched, according to the specified boolean operations, separately before matching with the rest of
 *  expression.  
 * <P>
 * So, a composed condition always encloses the inner conditions into paranthesis. For example, in the expression 
 * <code>A && B && ( C && D )</code> whe say that the ( C && D ) operation is a composed condition, since it will be evaluated
 * first, and then the result will be matched with the rest of the expression.
 * 
 * @author framos
 * @version $Id: ComposedQueryCondition.java 158 2005-11-04 13:03:09Z framos $
 */
public class ComposedQueryCondition extends MultipleQueryCondition {

	
	
	
    // ----------------------
    // Constructors
    // ----------------------
	
	public ComposedQueryCondition(QueryCondition _leftSide, String _operator, QueryCondition _rightSide) {
		super(_leftSide, _operator, _rightSide);
	}
	
	
	
    // ----------------------
    // Public methods
    // ----------------------
	
    /**
     * Inherited from <code>QueryCondition</code>
     */
    public String getSQLStatement(Query _query) {
        return "( " + getRightSide().getSQLStatement(_query) + " " + 
        		_query.formatFunction(getBooleanOperator(), null) + " " + 
        		getLeftSide().getSQLStatement(_query) + " )";
    }

}
