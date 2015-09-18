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
 * Created on Jun 13, 2005
 */
package br.com.auster.facelift.persistence.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.facelift.persistence.FetchCriteria;
import br.com.auster.facelift.persistence.OrderClause;

/**
 * @author framos
 * @version $Id: JDBCQueryHelper.java 141 2005-08-24 12:50:29Z framos $
 */
public abstract class JDBCQueryHelper {

	
	
	protected static Map offsetLimitMap;
	private static Logger log = LogFactory.getLogger(JDBCSequenceHelper.class);
	
	static { 
		offsetLimitMap = new HashMap();
		offsetLimitMap.put(JDBCHelperConstants.DBNAME_POSTGRESQL, " limit {0} offset {1} ");
	}
	

	/**
	 * Creates a new SQL statement, adding order clauses and offset/limit parameters to the specified query.  It uses   
	 * 	the connection metadata information to determine the database implemetation in use, and so define the correct
	 *  SQL syntax. 
	 * <P>
	 * The result SQL is the concatenation of the incoming sql plus any order clauses and offset/limit definitions, according
	 * 	to the values in the <code>_fetch</code> parameter. 
	 *  
	 * @param _conn the current database connection
	 * @param _sql the sql statement to be modified
	 * @param _fetch the fetching parameters
	 * 
	 * @return the modified sql, as defined above
	 * 
	 * @throws SQLException if any SQL exception is raised by the JDBC driver
	 */
	public static String applyFetchParameters(Connection _conn, String _sql, FetchCriteria _fetch) throws SQLException {
		
		if (_fetch == null) {
			log.warn("cannot set fetch parameters if fetch criteria is null");
			return _sql;
		}
		
		DatabaseMetaData meta = _conn.getMetaData();
		String dbName = meta.getDatabaseProductName();
		String pattern = (String) offsetLimitMap.get(dbName);
		if (pattern == null) {
			log.warn("JDBC sequence translator not compliant with database " + dbName);
			throw new IllegalArgumentException("JDBC sequence translator not compliant with database " + dbName);
		}
		
		Iterator iterator = _fetch.orderIterator();
		String orderBy = null;
		while (iterator.hasNext()) {
			if (orderBy == null) {
				orderBy = " ORDER BY ";
			}
			OrderClause clause = (OrderClause) iterator.next();
			orderBy += clause.getFieldName() + (clause.isAscending() ? " ASC " : " DESC ");
			orderBy += ",";
		}
		if (orderBy != null) {
			orderBy = orderBy.substring(0, orderBy.length()-1);
		}
		
		
		String offsetLimitStr = "";
		if ( (_fetch.getOffset() >= 0) && (_fetch.getSize() > 0) ) {
			offsetLimitStr = MessageFormat.format(pattern, new Object[] { String.valueOf(_fetch.getSize()), String.valueOf(_fetch.getOffset()) } );
		}
		return _sql + (orderBy == null ? "" : orderBy) + offsetLimitStr;
	}	
	
}
