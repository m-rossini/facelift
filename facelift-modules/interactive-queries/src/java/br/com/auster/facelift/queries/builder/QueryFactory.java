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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import br.com.auster.facelift.queries.interfaces.QueryException;
import br.com.auster.facelift.queries.model.Query;
import br.com.auster.facelift.queries.model.ViewObject;

/**
 * @author framos
 * @version $Id: QueryFactory.java 174 2005-12-02 12:58:15Z framos $
 */
public class QueryFactory {

		

    // ----------------------
    // Class variables
    // ----------------------
	
	private static Map QUERY_IMPLEMENTATIONS;
	
	static {
		QUERY_IMPLEMENTATIONS = new HashMap();
		QUERY_IMPLEMENTATIONS.put("PostgreSQL", "br.com.auster.facelift.queries.builder.PostgresQueryImpl");
		QUERY_IMPLEMENTATIONS.put("Oracle", "br.com.auster.facelift.queries.builder.OracleQueryImpl");
	}
	
	
	
	// ----------------------
    // Factory builder method
    // ----------------------

	public static Query createQuery(Connection _conn, ViewObject _view) throws QueryException {
		String dbName = null;
		try {
			dbName = _conn.getMetaData().getDatabaseProductName();
			Class klass = Class.forName((String) QUERY_IMPLEMENTATIONS.get(dbName));
			Query query = (Query) klass.newInstance();
			query.setSourceView(_view);
			return query;
		} catch (SQLException sqle) {
			throw new QueryException("could not find database name", sqle);
		} catch (ClassNotFoundException cnfe) {
			throw new QueryException("could not find query implementation for database " + dbName, cnfe);
		} catch (InstantiationException ie) {
			throw new QueryException("could not create instance of query implementation for database " + dbName, ie);
		} catch (IllegalAccessException iae) {
			throw new QueryException("could not create instance of query implementation for database " + dbName, iae);
		}
	}

}
