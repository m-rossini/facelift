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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.facelift.requests.model.OutputFile;

/**
 * @author framos
 * @version $Id: OutputFileDAO.java 108 2005-06-18 13:23:31Z framos $
 */
public class OutputFileDAO {


	public static final String SQL_STMT_SELECT_REQUEST_OUTFILE_ATTRS = "select attr_key, attr_value from proc_outfile_attrs where file_id = ?";
	public static final String SQL_STMT_SELECT_REQUEST_OUTFILES =
       	"select proc_request_outfile.file_id, proc_request_outfile.filename " +
		"from proc_request_outfile join proc_request_trail on proc_request_trail.trail_id = proc_request_outfile.trail_id ";
	
	
	private Logger log = LogFactory.getLogger(OutputFileDAO.class);
	
	
	

	
	public List selectRequestOutputFiles(Connection _conn, List _requestIdList) throws SQLException {
		if ((_requestIdList == null) || (_requestIdList.size() <= 0)) { 
			throw new IllegalArgumentException("cannot load output files for empty list of request ids");
		}
		PreparedStatement stmt = null;
		ResultSet rset = null;
		ArrayList list = new ArrayList();
		String sql = SQL_STMT_SELECT_REQUEST_OUTFILES + " where proc_request_trail.request_id in ( ";
		for (int i=0; i < _requestIdList.size(); i++) {
			sql += "? ,";
		}
		sql = sql.substring(0, sql.length()-1) + " )";
		try {
			log.debug("executing sql " + sql);
			stmt = _conn.prepareStatement(sql);
			for (int i=0; i < _requestIdList.size(); i++) {
				stmt.setLong(i+1,  Long.parseLong((String)_requestIdList.get(i)));
			}
			rset = stmt.executeQuery();
			while (rset.next()) {
				// loading output file info
				OutputFile outFile = new OutputFile();
				outFile.setFileId(rset.getLong(1));
				outFile.setFilename(rset.getString(2));
				outFile.setAttributes(selectRequestOutputFileAttributes(_conn, outFile.getFileId()));
				list.add(outFile);
			}
 			return list;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
	public List selectRequestOutputFiles(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		ArrayList list = new ArrayList();
		String sql = SQL_STMT_SELECT_REQUEST_OUTFILES + " where proc_request_trail.request_id = ?";
		try {
			log.debug("executing sql " + sql);
			stmt = _conn.prepareStatement(sql);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			while (rset.next()) {
				// loading output file info
				OutputFile outFile = new OutputFile();
				outFile.setFileId(rset.getLong(1));
				outFile.setFilename(rset.getString(2));
				outFile.setAttributes(selectRequestOutputFileAttributes(_conn, outFile.getFileId()));
				list.add(outFile);
			}
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
