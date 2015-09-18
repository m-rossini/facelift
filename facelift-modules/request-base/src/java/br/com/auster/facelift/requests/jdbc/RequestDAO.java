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
 * Created on Jun 14, 2005
 */
package br.com.auster.facelift.requests.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;
import br.com.auster.facelift.requests.interfaces.RequestBuilder;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.model.InputFile;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;

/**
 * @author framos
 * @version $Id: RequestDAO.java 364 2007-06-06 18:10:09Z framos $
 */
public class RequestDAO {

	
	
	public static final String SQL_STMT_SELECT_REQUEST = "select request_label, latest_status from proc_request where request_id = ?";
	public static final String SQL_STMT_SELECT_REQUEST_INFO = "select info_key, info_value from proc_request_info where request_id = ?";
	public static final String SQL_STMT_SELECT_REQUEST_IDS = "select request_id from proc_request ";
	
	public static final String SQL_STMT_SELECT_REQUEST_INFILES = "select filename from proc_request_infile where request_id = ? ";
	
	public static final String SQL_STMT_INSERT_REQUEST = "insert into proc_request ( request_id, request_label, latest_status ) values ( ?, ?, ?)";
	public static final String SQL_STMT_REQUEST_SEQUENCE = "proc_request_sequence";
	public static final String SQL_STMT_INSERT_REQUEST_INFO = "insert into proc_request_info ( request_id, info_key, info_value ) values ( ? , ?, ?)";
	public static final String SQL_STMT_INSERT_REQUEST_INFILE = "insert into proc_request_infile ( filename, request_id ) values ( ? , ?)";
	
	public static final String SQL_STMT_UPDATE_REQUEST_STATUS = "update proc_request set latest_status = ? where request_id = ?";
	
	
	
	private Logger log = LogFactory.getLogger(RequestDAO.class);
	
	
	
	
	
	public Request selectRequest(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Request request = null;
		try {
			log.debug("executing sql " + SQL_STMT_SELECT_REQUEST);
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_REQUEST);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			if (rset.next()) {
				RequestBuilder builder = new RequestBuilder();
				builder.buildRequest(rset.getString(1));
				builder.setStatus(rset.getInt(2));
				
				rset.close();
				stmt.close();
				log.debug("executing sql " + SQL_STMT_SELECT_REQUEST_INFO);
				stmt = _conn.prepareStatement(SQL_STMT_SELECT_REQUEST_INFO);
				stmt.setLong(1, _requestId);
				rset = stmt.executeQuery();
				while (rset.next()) {
					builder.addAddInfo(rset.getString(1), rset.getString(2));
				}
				request = builder.getRequest();
				request.setRequestId(_requestId);
			}
			return request;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
	public List selectRequestList(Connection _conn, RequestCriteria _criteria) throws SQLException {

		String sql = SQL_STMT_SELECT_REQUEST_IDS + " where ";
		if ((_criteria.getStatus() <= 0) && (_criteria.getLabel() == null)) {
			throw new IllegalArgumentException("search criteria for requests must specify a label pattern or a status level");
		}
		 
		boolean hasLabel = false;
		if (_criteria.getLabel() != null) {
			sql += " request_label like ? ";
			hasLabel = true;
		} 
		if (_criteria.getStatus() > 0) {
			sql += (hasLabel ? " and " : "") + " status_id = ?";  
		}
		PreparedStatement stmt = null;
		ResultSet rset = null;
		List resultList = new ArrayList();
		int col=1;
		try {
			log.debug("executing sql " + sql);
			stmt = _conn.prepareStatement(sql);
			if (_criteria.getLabel() != null) { 
				stmt.setString(col++, _criteria.getLabel());
			}
			if (_criteria.getStatus() > 0) {
				stmt.setInt(col++, _criteria.getStatus());
			}
			rset = stmt.executeQuery();
			while (rset.next()) {
				resultList.add( String.valueOf(rset.getInt(1)) );
			}
			return resultList;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}

	private void createRequest(Connection _conn, Request _request, TrailDAO _trailDao,
			PreparedStatement _requestStmt, PreparedStatement _trailStmt,
			PreparedStatement _inFileStmt, PreparedStatement _requestInfoStmt)
			throws SQLException {

		// inserting proc. request
		long newRequestId = JDBCSequenceHelper.nextValue(_conn, SQL_STMT_REQUEST_SEQUENCE);
		_request.setRequestId(newRequestId);
		_requestStmt.setLong(1, newRequestId);
		_requestStmt.setString(2, _request.getLabel());
		_requestStmt.setInt(3, _request.getLatestStatus());
		log.debug("executing sql " + SQL_STMT_INSERT_REQUEST);
		_requestStmt.executeUpdate();
		_requestStmt.clearParameters();
		// inserting input files
		if (_request.getInputFiles() != null) {
			for (Iterator fileIterator = _request.getInputFiles().iterator(); fileIterator.hasNext();) {
				InputFile file = (InputFile) fileIterator.next();
				_inFileStmt.setString(1, file.getFilename());
				_inFileStmt.setLong(2, newRequestId);
				log.debug("executing sql " + SQL_STMT_INSERT_REQUEST_INFILE);
				_inFileStmt.executeUpdate();
				_inFileStmt.clearParameters();
			}
		}
		// inserting add. info
		if (_request.getAdditionalInformation() != null) {
			for (Iterator addIterator = _request.getAdditionalInformation().entrySet().iterator(); addIterator.hasNext();) {
				Map.Entry addInfo = (Map.Entry) addIterator.next();
				_requestInfoStmt.setLong(1, newRequestId);
				_requestInfoStmt.setString(2, (String)addInfo.getKey());
				_requestInfoStmt.setString(3, (String)addInfo.getValue());
				log.debug("executing sql " + SQL_STMT_INSERT_REQUEST_INFO);
				_requestInfoStmt.executeUpdate();
				_requestInfoStmt.clearParameters();
			}
		}
		// inserting trail 
		if (_request.getTrails() != null) {
			for (Iterator trailIterator = _request.getTrails().iterator(); trailIterator.hasNext();) {
				Trail trail = (Trail) trailIterator.next();
				trail.setTrailId(JDBCSequenceHelper.nextValue(_conn, TrailDAO.SQL_STMT_REQUEST_TRAIL_SEQUENCE));
				log.debug("executing sql " + TrailDAO.SQL_STMT_INSERT_REQUEST_TRAIL);
				_trailDao.insertTrail(_trailStmt, newRequestId, trail);
				_trailStmt.clearParameters();
				_trailDao.createOutputFileList(_conn, trail);
			}
		}
	}

	public void createRequest(Connection _conn, Request _request) throws SQLException {

		if (_request == null) {
			return;
		}
		TrailDAO dao = new TrailDAO();
		
		PreparedStatement requestStmt = null;
		PreparedStatement trailStmt = null;
		PreparedStatement inFileStmt = null;
		PreparedStatement requestInfoStmt = null;

		try {
			requestStmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST);
			trailStmt = _conn.prepareStatement(TrailDAO.SQL_STMT_INSERT_REQUEST_TRAIL);
			inFileStmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST_INFILE);
			requestInfoStmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST_INFO);

			this.createRequest(_conn, _request, dao, requestStmt, trailStmt, inFileStmt, requestInfoStmt);

		} finally {
			if (requestStmt != null) { requestStmt.close(); }
			if (trailStmt != null) { trailStmt.close(); }
			if (inFileStmt != null) { inFileStmt.close(); }
			if (requestInfoStmt != null) { requestInfoStmt.close(); }
		}	
	}

	public void createMultipleRequest(Connection _conn, Collection _requestList) throws SQLException {
		if (_requestList == null) {
			return;
		}
		TrailDAO dao = new TrailDAO();
		
		PreparedStatement requestStmt = null;
		PreparedStatement trailStmt = null;
		PreparedStatement inFileStmt = null;
		PreparedStatement requestInfoStmt = null;
		Iterator iterator = _requestList.iterator();
		try {
			requestStmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST);
			trailStmt = _conn.prepareStatement(TrailDAO.SQL_STMT_INSERT_REQUEST_TRAIL);
			inFileStmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST_INFILE);
			requestInfoStmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST_INFO);
			while (iterator.hasNext()) {
				// inserting proc. request
				Request request = (Request) iterator.next();
				this.createRequest(_conn, request, dao, requestStmt, trailStmt, inFileStmt, requestInfoStmt);
			}
		} finally {
			if (requestStmt != null) { requestStmt.close(); }
			if (trailStmt != null) { trailStmt.close(); }
			if (inFileStmt != null) { inFileStmt.close(); }
			if (requestInfoStmt != null) { requestInfoStmt.close(); }
		}	
	}
	
	public void updateRequestStatus(Connection _conn, long _requestId, Trail _trail) throws SQLException {

		TrailDAO dao = new TrailDAO();
		PreparedStatement stmt = null;
		try {
			dao.insertTrail(_conn, _requestId, _trail);
			dao.createOutputFileList(_conn, _trail);
			log.debug("executing sql " + SQL_STMT_UPDATE_REQUEST_STATUS);
			stmt = _conn.prepareStatement(SQL_STMT_UPDATE_REQUEST_STATUS);
			stmt.setInt(1, _trail.getStatus());
			stmt.setLong(2, _requestId);
			stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		
	}
	
	public Set selectInputFiles(Connection _conn, long _requestId) throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rset = null;
		Set resultSet = new HashSet();
		try {
			log.debug("executing sql " + SQL_STMT_SELECT_REQUEST_INFILES);
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_REQUEST_INFILES);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			while (rset.next()) {
				InputFile file = new InputFile();
				file.setFilename(rset.getString(1));
				resultSet.add(file);
			}
			return resultSet;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
}
