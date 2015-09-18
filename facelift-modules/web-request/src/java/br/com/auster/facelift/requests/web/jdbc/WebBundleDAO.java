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
 * Created on Jun 11, 2005
 */
package br.com.auster.facelift.requests.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.facelift.requests.web.model.BundleFile;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author framos
 * @version $Id: WebBundleDAO.java 401 2008-02-08 19:41:49Z framos $
 */
public class WebBundleDAO {



	// -------------------------
    // Class constants
    // -------------------------

	protected static final String SQL_STMT_INSERT_WEB_BUNDLEFILE = "insert into web_bundlefile (file_id, web_request_id, filename, create_datetime, message) values ( ?, ? , ?, ?, ? )";
	protected static final String  SQL_WEB_BUNDLEFILE_SEQUENCE = "bundlefile_sequence";

	protected static final String SQL_STMT_SELECT_WEB_BUNDLEFILE = "select filename, create_datetime, message from web_bundlefile where web_request_id = ?";
	protected static final String SQL_STMT_REMOVE_WEB_BUNDLEFILE = "delete from web_bundlefile where web_request_id = ?";


	// Query uisada para listar os relatórios com as consequencias e o numero de ocnsequencias de cada
//	protected static final String SQL_STMT_SELECT_WEB_BUNDLEFILE_COUNT = " SELECT a.filename, a.create_datetime, a.message, "
//																		 + " ( SELECT COUNT(bck_consequence.objid) "
//																		 + "        FROM bck_consequence "
//																		 + "        JOIN bck_rule ON bck_rule.objid = bck_consequence.rule_uid "
//																		 + "        WHERE transaction_id = a.web_request_id AND "
//																		 + "        INSTR(a.message, rule_name) > 0 ) CNT "
//																		 + " FROM web_bundlefile a "
//																		 + " where a.web_request_id = ? "
//																		 + " order by CNT DESC ";

	protected static final String SQL_STMT_SELECT_WEB_BUNDLEFILE_COUNT =
		  "SELECT a.filename, a.create_datetime, a.message, a.record_count " +
		  "FROM web_bundlefile a " +
		  "WHERE a.web_request_id = ?";

    // -------------------------
    // Instance variables
    // -------------------------

	private Logger log = LogFactory.getLogger(WebBundleDAO.class);



    // -------------------------
    // public methods
    // -------------------------

	public List selectBundleFilesCount(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		List list = new ArrayList();
		try {
			log.debug("executing sql (counting file bundle):" + SQL_STMT_SELECT_WEB_BUNDLEFILE_COUNT);
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_WEB_BUNDLEFILE_COUNT);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			while (rset.next()) {
				BundleFile file = new BundleFile();
				file.setFilename(rset.getString(1));
				file.setFileDatetime(rset.getTimestamp(2));
				file.setMessage(rset.getString(3));
				file.setConsequencesCount(rset.getInt(4));
				list.add(file);
			}
			// sort, descending, by consequence count
			Collections.sort(list, new Comparator() {
				public int compare(Object _file1, Object _file2) {
					try {
						BundleFile f1 = (BundleFile) _file1;
						BundleFile f2 = (BundleFile) _file2;
						return (f2.getConsequencesCount() - f1.getConsequencesCount());
					} catch (ClassCastException cce) {}
					return 0;
				}

			});
			return list;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}

	public List selectBundleFiles(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		List list = new ArrayList();
		try {
			log.debug("executing sql (building file bundle):" + SQL_STMT_SELECT_WEB_BUNDLEFILE);
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_WEB_BUNDLEFILE);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			while (rset.next()) {
				BundleFile file = new BundleFile();
				file.setFilename(rset.getString(1));
				file.setFileDatetime(rset.getTimestamp(2));
				file.setMessage(rset.getString(3));
				list.add(file);
			}
			return list;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}

	public int createBundlefiles(Connection _conn, long _requestId, Collection _files) throws SQLException {
		if (_files != null) {
			for (Iterator iterator = _files.iterator(); iterator.hasNext(); ) {
				createBundleFile(_conn, _requestId, (BundleFile) iterator.next());
			}
			return _files.size();
		}
		return 0;
	}

	public int createBundleFile(Connection _conn, long _requestId, BundleFile _file) throws SQLException {
		PreparedStatement stmt = null;
		try {
			log.debug("executing sql " + SQL_STMT_INSERT_WEB_BUNDLEFILE);
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_WEB_BUNDLEFILE);
			stmt.setLong(1, JDBCSequenceHelper.nextValue(_conn, SQL_WEB_BUNDLEFILE_SEQUENCE));
			stmt.setLong(2, _requestId);
			stmt.setString(3, _file.getFilename());
			stmt.setTimestamp(4, _file.getFileDatetime());
			stmt.setString(5, _file.getMessage());
			return stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}

	public int removeFiles(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		try {
			log.debug("executing sql " + SQL_STMT_REMOVE_WEB_BUNDLEFILE);
			stmt = _conn.prepareStatement(SQL_STMT_REMOVE_WEB_BUNDLEFILE);
			stmt.setLong(1, _requestId);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}
}

