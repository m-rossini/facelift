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
package br.com.auster.facelift.queries.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;

/**
 * Executes the SQL statement created for some query object, and returns the resulting data into a list of <code>SelectedRow</code>
 * 
 * @author framos
 * @version $Id$
 */
public class QueryDAO {

	
    
    // ----------------------
    // Class variables
    // ----------------------

	public static final int MAX_ROWSET_SIZE = 1000;
	
	
	
    // ----------------------
    // Instance variables
    // ----------------------

	private Logger log = LogFactory.getLogger(QueryDAO.class);
    
	
    
    // ----------------------
    // Public methods
    // ----------------------

	/**
	 * Executes the SQL statment, against a previously openned database connection, and returns the
	 * 	results as indicated.
	 * 
	 *  @param _conn the database connection already openned
	 *  @param _query the SQL statement to be executed
	 *  @return the list of resulting rows
	 * 	@exception SQLException if any error was detected when executing the database operation
	 */
	public List executeSQL(Connection _conn, String _query) throws SQLException {

        Statement stmt = null;
        ResultSet rset = null;
        try {
            stmt = _conn.createStatement();
			log.debug("creating JDBC statment");
            rset = stmt.executeQuery(_query);
			log.debug("JDBC statment executed");
            return buildResult(rset);
        } finally {
            if (rset != null) { rset.close(); }
            if (stmt != null) { stmt.close(); }
        }
    }

	
	
    // ----------------------
    // Private methods
    // ----------------------
	
	/**
	 * Creates the list of <code>SelectedRow</code> objects from the result set of the executed query.
	 */
    private List buildResult(ResultSet _rset) throws SQLException {
        int len = _rset.getMetaData().getColumnCount();
        ArrayList results = new ArrayList();
        for (int counter=0; _rset.next() && (counter < MAX_ROWSET_SIZE); counter++) {
            SelectedRow row = new SelectedRow(len);
            for (int i=0; i < len; i++) {
                row.setCell(i, _rset.getObject(i+1));
            }
            results.add(row);
			log.debug("added row " + row);
        }
		log.debug("Query resulted in " + results.size() + " rows selected");
        return results;
    }
}
