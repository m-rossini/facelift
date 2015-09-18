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
 * Created on 07/03/2006
 */
package br.com.auster.facelift.cycle.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import br.com.auster.facelift.cycle.model.Cycle;
import br.com.auster.facelift.cycle.model.CycleProcessingId;
import br.com.auster.facelift.persistence.FetchCriteria;
import br.com.auster.facelift.persistence.jdbc.JDBCQueryHelper;
import br.com.auster.facelift.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author framos
 * @version $Id$
 */
public class CycleDAO {


	public static final String SQL_CYCLEINFO_SEQUENCE = "cycleinfo_sequence";
	
	public static final String SQL_STMT_INSERT_CYCLECODE = "insert into cycle_info ( uid, cycle_code, cycle_description, cycle_date ) values ( ?, ?, ?, ?)";
	public static final String SQL_STMT_SELECT_CYCLECODE = "select cc.uid, cc.cycle_code, cc.cycle_description, cc.cycle_date, cc.insert_date as insert_date, cc.last_insert, id.cycle_id, id.processing_id, id.insert_date as id_insert_date" +
			                                               " from cycle_info cc" +
			                                               " left outer join cycle_processing_ids id on cc.uid = id.cycle_id";
	public static final String SQL_STMT_FILTERBY_UID = "cc.uid = ?";
	public static final String SQL_STMT_FILTERBY_CYCLECODE = "cc.cycle_code = ?";
	public static final String SQL_STMT_FILTERBY_CYCLECODE_AND_DATE = SQL_STMT_FILTERBY_CYCLECODE + " and cc.cycle_date = ?";
	public static final String SQL_STMT_UPDATE_CYCLEINFO_LASTINSERT = "update cycle_info set last_insert = 'now' where uid = ?";
	
	public static final String SQL_STMT_INSERT_PROCESSING_IDS = "insert into cycle_processing_ids ( cycle_id, processing_id ) values (?, ?)";
	public static final String SQL_STMT_SELECT_PROCESSING_IDS = "select processing_id, insert_date from cycle_processing_ids where cycle_id = ?";
	
	
	
	public Collection selectCycles(Connection _conn, String _code, FetchCriteria _criteria) throws SQLException {
		ResultSet rset = null;
		PreparedStatement stmt = null;
		try {
			String sql = SQL_STMT_SELECT_CYCLECODE;
			if (_code != null) {
				sql += " where " + SQL_STMT_FILTERBY_CYCLECODE;
			}
			sql = JDBCQueryHelper.applyFetchParameters(_conn, sql, _criteria);
			// TODO : log
			stmt = _conn.prepareStatement(sql);
			if (_code != null) {
				stmt.setString(1, _code);
			}
			rset = stmt.executeQuery();
			if (rset.next()) {
				return loadCycleFromResultset(rset);
			}
			return Collections.EMPTY_LIST;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}

	public Cycle selectUniqueCycle(Connection _conn, long _id, String _code, Date _date) throws SQLException {
		ResultSet rset = null;
		PreparedStatement stmt = null;
		Cycle c = null;
		try {
			String sql = SQL_STMT_SELECT_CYCLECODE;
			if ((_code != null) && (_date != null)) {
				sql += " where " + SQL_STMT_FILTERBY_CYCLECODE_AND_DATE;
			} else {
				sql += " where " +SQL_STMT_FILTERBY_UID;
			}
			// TODO : log
			stmt = _conn.prepareStatement(sql);
			if ((_code != null) && (_date != null)) {
				stmt.setString(1, _code);
				stmt.setDate(2, _date);
			} else {
				stmt.setLong(1, _id);
			}
			rset = stmt.executeQuery();
			if (rset.next()) {
				Collection collect = loadCycleFromResultset(rset);
				if (collect != null) {
					c = (Cycle) collect.iterator().next();
				}
			}
			return c;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
	
	public void insertCycle(Connection _conn, Cycle _cycle) throws SQLException {
		ResultSet rset = null;
		PreparedStatement stmt = null;
		try {
			
			long uid = JDBCSequenceHelper.nextValue(_conn, SQL_CYCLEINFO_SEQUENCE);
			
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_CYCLECODE);
			stmt.setLong(1, uid);
			stmt.setString(2, _cycle.getCycleCode());
			stmt.setString(3, _cycle.getCycleDescription());
			stmt.setDate(4, new Date(_cycle.getCycleDate().getTime()));
			stmt.executeUpdate();
			rset = stmt.getGeneratedKeys();
			if (rset.next()) {
				_cycle.setCycleId(uid);
			}			
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}

	public void insertProcessingIds(Connection _conn, Cycle _cycle, Collection _ids) throws SQLException {
		PreparedStatement stmt = null;
		try {
			Collection existingIds = selectProcessingIds(_conn, _cycle.getCycleId());
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_PROCESSING_IDS);
			for (Iterator it=_ids.iterator();it.hasNext();) {
				CycleProcessingId id = (CycleProcessingId)it.next();
				if (!existingIds.contains(id)) {
					stmt.setLong(1, _cycle.getCycleId());
					stmt.setString(2, id.getProcessingId());
					stmt.executeUpdate();
					stmt.clearParameters();
				}
			}
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}
	
	public void updateCycleLastInsert(Connection _conn, Cycle _cycle) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = _conn.prepareStatement(SQL_STMT_UPDATE_CYCLEINFO_LASTINSERT);
			stmt.setLong(1, _cycle.getCycleId());
			stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}
	
	public Collection selectProcessingIds(Connection _conn, long _id) throws SQLException {
		ResultSet rset = null;
		PreparedStatement stmt = null;
		Collection result = new ArrayList();
		try {
			String sql = SQL_STMT_SELECT_PROCESSING_IDS;
			// TODO : log
			stmt = _conn.prepareStatement(sql);
			stmt.setLong(1, _id);
			rset = stmt.executeQuery();
			while (rset.next()) {
				CycleProcessingId c = new CycleProcessingId();
				c.setCycleId(_id);
				c.setProcessingId(rset.getString("processing_id"));
				c.setInsertDate(rset.getTimestamp("insert_date"));
				result.add(c);
			}
			return result;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}

	
	private Collection loadCycleFromResultset(ResultSet _rset) throws SQLException {
		Collection listofcycles = new ArrayList();
		do {
			Cycle c = new Cycle(_rset.getLong("uid"));
			c.setCycleCode(_rset.getString("cycle_code"));
			c.setCycleDate(_rset.getDate("cycle_date"));
			c.setCycleDescription(_rset.getString("cycle_description"));
			c.setInsertDate(_rset.getTimestamp("insert_date"));
			c.setLastInsert(_rset.getTimestamp("last_insert"));
			listofcycles.add(c);
			
			CycleProcessingId id = null;
			do {
				id = new CycleProcessingId(c.getCycleId(), _rset.getString("processing_id"));
				// if processing id is null, then there are not ids for this cycle
				if (id.getProcessingId() != null) {
					id.setInsertDate(_rset.getTimestamp("id_insert_date"));
					c.addProcessingId(id);
				}
				if (! _rset.next()) {
					return listofcycles;
				} else if (_rset.getLong("uid") != c.getCycleId()) {
					break;
				}
			} while (true);
		} while (true);
	}
	
}
