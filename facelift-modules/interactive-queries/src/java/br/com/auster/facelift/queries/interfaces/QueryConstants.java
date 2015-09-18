/*
 * Copyright (c) 2004 TTI Tecnologia. All Rights Reserved.
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
 * Created on Aug 5, 2005
 */
package br.com.auster.facelift.queries.interfaces;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author framos
 * @version $Id: QueryConstants.java 140 2005-08-05 21:05:09Z framos $
 */
public abstract class QueryConstants {

	
	// Map of types
	public static final Map COLUMN_TYPES;
	
	// Pre-defined types for configuring columns
	public static final String COLUMN_TYPE_CHAR     = "char";
 	public static final String COLUMN_TYPE_VARCHAR  = "varchar";
 	public static final String COLUMN_TYPE_INTEGER  = "integer";
 	public static final String COLUMN_TYPE_LONG     = "long";
 	public static final String COLUMN_TYPE_DOUBLE   = "double";
 	public static final String COLUMN_TYPE_DATE     = "date";
 	public static final String COLUMN_TYPE_TIME     = "time";
 	public static final String COLUMN_TYPE_DATETIME = "datetime";
	
	static {
		COLUMN_TYPES = new HashMap();
		COLUMN_TYPES.put(COLUMN_TYPE_CHAR    , String.valueOf(Types.CHAR));
		COLUMN_TYPES.put(COLUMN_TYPE_VARCHAR , String.valueOf(Types.VARCHAR));
		COLUMN_TYPES.put(COLUMN_TYPE_INTEGER , String.valueOf(Types.INTEGER));
		COLUMN_TYPES.put(COLUMN_TYPE_LONG    , String.valueOf(Types.BIGINT));
		COLUMN_TYPES.put(COLUMN_TYPE_DOUBLE  , String.valueOf(Types.DOUBLE));
		COLUMN_TYPES.put(COLUMN_TYPE_DATE    , String.valueOf(Types.DATE));
		COLUMN_TYPES.put(COLUMN_TYPE_TIME    , String.valueOf(Types.TIME));
		COLUMN_TYPES.put(COLUMN_TYPE_DATETIME, String.valueOf(Types.TIMESTAMP));		
	}
	
}
