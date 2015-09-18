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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;
import br.com.auster.facelift.requests.model.OutputFile;
import br.com.auster.facelift.requests.model.Trail;

/**
 * @author framos
 * @version $Id: TrailDAO.java 377 2007-08-21 21:06:36Z framos $
 */
public class TrailDAO {


	public static final String SQL_STMT_INSERT_REQUEST_TRAIL = "insert into proc_request_trail ( trail_id, request_id, trail_message, trail_datetime, status_id ) values ( ? , ?, ?, ?, ?)";
	public static final String SQL_STMT_REQUEST_TRAIL_SEQUENCE = "proc_trail_sequence";

	public static final String SQL_STMT_SELECT_REQUEST_OUTFILE_ATTRS = "select attr_key, attr_value from proc_outfile_attrs where file_id = ?";
	public static final String SQL_STMT_SELECT_REQUEST_TRAILS =
       	"select proc_request_trail.trail_id, proc_request_trail.status_id, proc_request_trail.trail_datetime, " +
		"proc_request_trail.trail_message, proc_request_outfile.file_id, proc_request_outfile.filename " +
		"from proc_request_trail left join proc_request_outfile on proc_request_trail.trail_id = proc_request_outfile.trail_id " +
		" where proc_request_trail.request_id = ?";
	
	
	public static final String SQL_STMT_INSERT_REQUEST_OUTFILE = "insert into proc_request_outfile ( file_id, trail_id, filename ) values ( ? , ?, ?)";
	public static final String SQL_STMT_REQUEST_OUTFILE_SEQUENCE = "outfile_sequence"; 
	public static final String SQL_STMT_INSERT_REQUEST_OUTFILE_ATTR = "insert into proc_outfile_attrs ( file_id, attr_key, attr_value ) values ( ? , ?, ?)";
	
	
	
	private Logger log = LogFactory.getLogger(TrailDAO.class);
	
	
	
	
	
	public void insertTrail(Connection _conn, long _requestId, Trail _trail) throws SQLException {
		PreparedStatement stmt = null;
		try {
			_trail.setTrailId(JDBCSequenceHelper.nextValue(_conn, TrailDAO.SQL_STMT_REQUEST_TRAIL_SEQUENCE));
			log.debug("executing sql " + SQL_STMT_INSERT_REQUEST_TRAIL);
			stmt = _conn.prepareStatement(TrailDAO.SQL_STMT_INSERT_REQUEST_TRAIL);
			insertTrail(stmt, _requestId, _trail);
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		
	}
	
	public void insertTrail(PreparedStatement _stmt, long _requestId, Trail _trail) throws SQLException {

		_stmt.setLong(1, _trail.getTrailId());
		_stmt.setLong(2, _requestId);
		_stmt.setString(3, _trail.getMessage());
		_stmt.setTimestamp(4, _trail.getTrailDate());
		_stmt.setInt(5, _trail.getStatus());
		_stmt.executeUpdate();
	}

	public void createOutputFileList(Connection _conn, Trail _trail) throws SQLException {
		if ((_trail == null) || (_trail.getOutputFiles() == null)) {
			log.warn("No trail or outputfile found");
			return;
		}
		PreparedStatement stmt = null;		
		try {
			log.debug("executing sql " + SQL_STMT_INSERT_REQUEST_OUTFILE);
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST_OUTFILE);
			for (Iterator iterator = _trail.getOutputFiles().iterator(); iterator.hasNext(); ) {
				OutputFile file = (OutputFile) iterator.next();
				long fileId = JDBCSequenceHelper.nextValue(_conn, SQL_STMT_REQUEST_OUTFILE_SEQUENCE);
				stmt.setLong(1, fileId);
				stmt.setLong(2, _trail.getTrailId());
				stmt.setString(3, file.getFilename());
				stmt.executeUpdate();
				stmt.clearParameters();
				
				createOutputFileAttrs(_conn, fileId, file.getAttributes());
			}
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}
	
	public void createOutputFileAttrs(Connection _conn, long _fileId, Map _attrs) throws SQLException {
		PreparedStatement stmt = null;
		if (_attrs == null) {
			return;
		}
		try {
			log.debug("executing sql " + SQL_STMT_INSERT_REQUEST_OUTFILE_ATTR);
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_REQUEST_OUTFILE_ATTR);
			for (Iterator iterator = _attrs.entrySet().iterator(); iterator.hasNext(); ) {
				stmt.setLong(1, _fileId);
				Map.Entry attr = (Map.Entry) iterator.next();
				stmt.setString(2, (String)attr.getKey());
				stmt.setString(3, (String)attr.getValue());
				stmt.executeUpdate();
				stmt.clearParameters();
			}
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}	
	
	public List selectRequestTrails(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		ArrayList list = new ArrayList();
		long previousId = -1, trailId = 0;
		Trail trail = null;
		try {
			log.debug("executing sql " + SQL_STMT_SELECT_REQUEST_TRAILS);
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_REQUEST_TRAILS);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			while (rset.next()) {
				trailId = rset.getLong(1);
				if (trailId != previousId) {
					if (trail != null) { list.add(trail); }
					trail = new Trail();
					trail.setOutputFiles(new ArrayList());
					trail.setTrailId(trailId);
					trail.setStatus(rset.getInt(2));
					trail.setTrailDate(rset.getTimestamp(3));
					trail.setMessage(rset.getString(4));
					previousId = trailId;
				}
				// loading output file info
				long fileId = rset.getLong(5);
				if (fileId > 0) {
					OutputFile outFile = new OutputFile();
					outFile.setFileId(fileId);
					outFile.setFilename(rset.getString(6));
					outFile.setAttributes(selectRequestOutputFileAttributes(_conn, fileId));
					trail.getOutputFiles().add(outFile);
				}
			}
			if (trail != null) { list.add(trail); }
 			return list;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}	
	
	private Map selectRequestOutputFileAttributes(Connection _conn, long _fileId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Map map = new HashMap();
		try {
			log.debug("executing sql " + SQL_STMT_SELECT_REQUEST_OUTFILE_ATTRS);
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_REQUEST_OUTFILE_ATTRS);
			stmt.setLong(1, _fileId);
			rset = stmt.executeQuery();
			while (rset.next()) {
				map.put(rset.getString(1), rset.getString(2));
			}
			return map;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
}
