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
package br.com.auster.facelift.queries.interfaces;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines the list of condinitional operators, regardless of being a boolean, artihmetic or alpha-numeric operator. 
 * 
 * @author framos
 * @version $Id: QueryFunctions.java 181 2005-12-16 11:15:30Z framos $
 */
public abstract class QueryFunctions implements Serializable {

	

    // ----------------------
    // Class variables
    // ----------------------
	
	// applicable to connect conditions 
    public static final String BOOLEAN_AND              = "AND";
    public static final String BOOLEAN_OR               = "OR";
    public static final String BOOLEAN_NOT               = "NOT";
        
	// applicable to build a condition
	public static final String COLUMN_RAW_VALUE			= "VALUE";
	
    public static final String NUMERIC_EQUAL            = "EQUAL";
    public static final String NUMERIC_NOT_EQUAL        = "NOT EQUAL";
    public static final String NUMERIC_LOWER            = "LOWER";
    public static final String NUMERIC_LOWER_EQUAL      = "LOWER OR EQUAL";
    public static final String NUMERIC_GREATER          = "GREATER";
    public static final String NUMERIC_GREATER_EQUAL    = "GREATER OR EQUAL";
    
    public static final String STRING_NULL              = "NULL";
    public static final String STRING_NOT_NULL          = "NOT NULL";
    public static final String STRING_LIKE              = "LIKE";
    public static final String STRING_NOT_LIKE          = "NOT LIKE";

	// applicable when selecting column to query
	public static final String MATH_MAX					= "MAX";
	public static final String MATH_MIN					= "MIN";
	public static final String MATH_AVERAGE				= "AVG";
	public static final String MATH_COUNT				= "COUNT";
	public static final String MATH_ABSOLUTE			= "ABS";
	public static final String MATH_SUM					= "SUM";
	
	public static final String DATE_NOW					= "NOW";
	
	
	public static final Set GROUPABLE_FUNCTIONS = new HashSet();
	
	
	static {
		GROUPABLE_FUNCTIONS.add(MATH_MAX);
		GROUPABLE_FUNCTIONS.add(MATH_MIN);
		GROUPABLE_FUNCTIONS.add(MATH_AVERAGE);
		GROUPABLE_FUNCTIONS.add(MATH_COUNT);
		GROUPABLE_FUNCTIONS.add(MATH_ABSOLUTE);
	}

	
	// applicable to order-by clauses 
    public static final String ORDER_ASCENDING          = "ASC";
    public static final String ORDER_DESCENDING         = "DESC";
	
}
