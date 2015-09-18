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
package br.com.auster.facelift.requests.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.persistence.FetchCriteria;
import br.com.auster.persistence.jdbc.JDBCQueryHelper;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.model.Trail;

/**
 * @author framos
 * @version $Id: WebRequestRequestsDAO.java 273 2006-09-21 13:15:12Z framos $
 */
public class WebRequestRequestsDAO {

	
	
	public static final String SQL_STMT_UPDATE_STATUS_PROCREQUEST_ALL = "insert into proc_request_trail ( trail_id, status_id, trail_datetime, request_id ) ( {1}, ? , ? , proc_request_id from web_request_requests where web_request_id = ? )";
	public static final String SQL_STMT_UPDATE_LATESTSTATUS_PROCREQUEST_ALL = "update proc_request set latest_status = ? where request_id in ( select proc_request_id from web_request_requests where web_request_id = ? )";
	
	public static final String SQL_STMT_PROCREQUEST_TRAIL_SEQUENCE = "proc_trail_sequence";
	
	public static final String SQL_STMT_SELECT_PROCREUQEST = "select proc_request_id from web_request_requests inner join proc_request on web_request_requests.proc_request_id = proc_request.request_id where web_request_requests.web_request_id = ? ";
	
	public static final String SQL_STMT_SELECT_OUTPUTFILES = "select proc_request_outfile.filename " +
				"from proc_request_outfile join proc_request_trail on proc_request_trail.trail_id = proc_request_outfile.trail_id " +
				"join web_request_requests on web_request_requests.proc_request_id = proc_request_trail.request_id " +
				"where web_request_requests.web_request_id = ?";

	
	private Logger log = LogFactory.getLogger(WebRequestRequestsDAO.class);
	
	
	
	
	public int updateStatusForAll(Connection _conn, long _requestId, Trail _trail) throws SQLException {
		//builds sql statement
		String sequence = JDBCSequenceHelper.translate(_conn, SQL_STMT_PROCREQUEST_TRAIL_SEQUENCE);
		String sql = MessageFormat.format(SQL_STMT_UPDATE_STATUS_PROCREQUEST_ALL, new Object[] { sequence });
		// executes mass update
		PreparedStatement stmt = null;
		try {
			// creating trails
			log.debug("executing query " + sql);
			stmt = _conn.prepareStatement(sql);
			stmt.setInt(1, _trail.getStatus());
			stmt.setTimestamp(2, _trail.getTrailDate());
			stmt.setLong(3, _requestId);
			int counter = stmt.executeUpdate();
			stmt.close();
			// updating latest status
			log.debug("executing query " + SQL_STMT_UPDATE_LATESTSTATUS_PROCREQUEST_ALL);
			stmt = _conn.prepareStatement(SQL_STMT_UPDATE_LATESTSTATUS_PROCREQUEST_ALL);
			stmt.setInt(1, _trail.getStatus());
			stmt.setLong(2, _requestId);
			stmt.executeUpdate();
			return counter;
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}
	
	
	public List selectProcRequestList(Connection _conn, long _requestId, RequestCriteria _criteria, FetchCriteria _fetch) throws SQLException {
		
		String sql = SQL_STMT_SELECT_PROCREUQEST;
        if (_criteria != null) {
            if (_criteria.getLabel() != null) {
				log.debug("will search using like ? " + (_criteria.getLabel().indexOf("%")>=0) );
				if (_criteria.getLabel().indexOf("%") >= 0) {
					sql += " and request_label like ?";
				} else {
					sql += " and request_label = ?";
				}
            } else {
                sql += " and latest_status = ?";
            } 
        }
		
		
		//
		// WARNING : Using limit in this query slows it by 10 times, at least! So, to help the query optimizer, we will
		//			 use the offset and limit, OUTSIDE of the main query
		//
//		if (_fetch != null) {
//			int fetchSize = _fetch.getSize();
//			_fetch.setSize(0);
			sql = JDBCQueryHelper.applyFetchParameters(_conn, sql, _fetch);
//			_fetch.clearOrder();
//			_fetch.setSize(fetchSize);
//			sql = JDBCQueryHelper.applyFetchParameters(_conn, "select * from ( " + sql + ") as A1", _fetch);
//		}
		//
		// WARNING : end of warning
		//
		
		log.debug("executing query " + sql);
		
		PreparedStatement stmt = null;
		ResultSet rset = null;
		List resultList = new ArrayList();
		try {
			stmt = _conn.prepareStatement(sql);
			stmt.setLong(1, _requestId);
	        if (_criteria != null) {
	            if (_criteria.getLabel() != null) {
					stmt.setString(2, _criteria.getLabel()); 
	            } else {
	                stmt.setInt(2, _criteria.getStatus());
	            } 
	        }
			rset = stmt.executeQuery();
			while (rset.next()) {
				resultList.add(rset.getString(1));
			}
			return resultList;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
	public List selectGeneratedOutputFiles(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		List resultList = new ArrayList();		
		try {
			log.debug("executing sql " + SQL_STMT_SELECT_OUTPUTFILES);
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_OUTPUTFILES);			
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			while (rset.next()) {
				resultList.add(rset.getString(1));
			}
			return resultList;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
}
