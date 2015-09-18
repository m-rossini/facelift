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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.web.interfaces.WebRequestBuilder;
import br.com.auster.facelift.requests.web.interfaces.WebRequestCriteria;
import br.com.auster.facelift.requests.web.interfaces.WebRequestLifeCycle;
import br.com.auster.facelift.requests.web.model.WebRequest;
import br.com.auster.facelift.requests.web.model.WebRequestConsequenceVO;
import br.com.auster.persistence.FetchCriteria;
import br.com.auster.persistence.jdbc.JDBCQueryHelper;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author framos
 * @version $Id: WebRequestDAO.java 371 2007-07-24 19:32:21Z gbrandao $
 */
public class WebRequestDAO {

	// -------------------------
	// Class constants
	// -------------------------

	protected static final String	SQL_STMT_INSERT_WEB_REQUEST	                  = "insert into web_request (request_id, owner_id, start_date, request_status) values ( ?, ?, ?, ?)";
	protected static final String	SQL_WEB_REQUEST_SEQUENCE	                    = "web_request_sequence";
	protected static final String	SQL_STMT_INSERT_WEB_REQUEST_INFO	            = "insert into web_request_info (web_request_id, info_key, info_value) values ( ? , ?, ? )";
	protected static final String	SQL_STMT_INSERT_WEB_REQUEST_REQUESTS	        = "insert into web_request_requests (web_request_id, proc_request_id) values ( ? , ? )";
	protected static final String	SQL_STMT_SELECT_WEB_REQUEST	                  = "select web_request.request_id, web_request.start_date, web_request.end_date, web_request.owner_id, web_request.request_status "
	                                                                                + "from web_request ";

	protected static final String	SQL_STMT_SELECT_WEB_REQUEST_BY_PROC_REQUEST	  = "select web_request_id from web_request_requests where proc_request_id = ?";

	protected static final String	SQL_STMT_SELECT_WEB_REQUEST_BY_SECURITY_GROUP	= SQL_STMT_SELECT_WEB_REQUEST
	                                                                                + "join web_user on web_request.owner_id = web_user.user_id "
	                                                                                + "join aut_user_group_asgm on web_user.security_id = aut_user_group_asgm.cd_user "
	                                                                                + "join aut_group on aut_user_group_asgm.cd_group = aut_group.cd_group "
	                                                                                + "where "
	                                                                                + "     aut_user_group_asgm.dt_expr is null and "
	                                                                                + "     aut_group.nm_group = ? ";

	protected static final String	SQL_STMT_COUNT_WEB_REQUEST	                  = "select count(*) from web_request";
	protected static final String	SQL_STMT_COUNT_WEB_REQUEST_PROCESSES	        = "select count(*) from web_request_requests a";

	// protected static final String SQL_STMT_COUNT_WEB_REQUEST_PROCSTATUS =
	// "select b.status_id, count(b.request_id) from proc_request_trail b where
	// b.request_id in ( select proc_request_id from web_request_requests where
	// web_request_id = ? ) group by b.status_id";
	protected static final String	SQL_STMT_COUNT_WEB_REQUEST_PROCSTATUS	        = "select b.latest_status, count(b.request_id) from proc_request b "
	                                                                                + " join web_request_requests a on a.proc_request_id = b.request_id "
	                                                                                + " where a.web_request_id = ? group by b.latest_status";
    
    protected static final String SQL_STMT_COUNT_WEB_REQUEST_INFO = "select a.*, b.* " +
                                                                    " from web_request_info a join web_request b on a.WEB_REQUEST_ID = b.REQUEST_ID " +
                                                                    " where a.WEB_REQUEST_ID = ? and a.INFO_KEY = 'request.size'";
    
    protected static final String SQL_STMT_TOTAL_COUNT_WEB_REQUEST_INFO = "SELECT INFO_VALUE FROM WEB_REQUEST_INFO "
    																	  + " WHERE WEB_REQUEST_ID = ? AND INFO_KEY = 'request.size'";
    

	protected static final String	SQL_STMT_COUNT_WEB_REQUEST_STATUSES	          = "select total_count, finish_count from web_request_counter where request_id = ?";

	protected static final String	SQL_STMT_SELECT_WEB_REQUEST_INFO	            = "select web_request_id, info_key, info_value from web_request_info where ";

	protected static final String	SQL_STMT_UPDATE_WEB_REQUEST	                  = "update web_request set start_date = ?, end_date = ?, owner_id = ?, request_status = ? where request_id = ?";
	
	protected static final String SQL_STMT_CONSEQUENCE_COUNTERS = "select r.rule_code, r.rule_name, count(*) " +
			" from bck_consequence c inner join bck_rule r on r.objid = c.rule_uid" +
			" where c.transaction_id = ? group by r.rule_code,r.rule_name order by r.rule_code, r.rule_name";
    
    
    /** 
     * Used to store the values of  <code>SQL_STMT_DELETE_WEB_REQUEST</code>.
     * Constante usada para armazenar o sql para efetuar um delete "logico" de uma web request
     * O valor de REQUEST_STATUS = 199 .
     */
    protected static final String SQL_STMT_DELETE_WEB_REQUEST = "update WEB_REQUEST set REQUEST_STATUS = 199  where REQUEST_ID = ? and request_status in (3,4)";

	// -------------------------
	// Instance variables
	// -------------------------

	private Logger	log	= LogFactory.getLogger(WebRequestDAO.class);

	// -------------------------
	// public methods
	// -------------------------

	/**
	 * 
	 * Is responsible in create a new request (insert in the web_request table)
	 * and its info (web_request_info table) and calls method create link.
	 * The method create link insert a data in web_request_request table.
	 * 
	 * @param _conn
	 * @param _request
	 * @return
	 * @throws SQLException
	 */
	public int createRequest(Connection _conn, WebRequest _request) throws SQLException {
		int counter = createTransaction(_conn, _request);
		counter += createLink(_conn, _request);
		return counter;
	}

	/**
	 * 
	 * Is responsible in create a new webRequest(insert in the web_request table)
	 * and its info (web_request_info table).
	 * 
	 * @param _conn
	 * @param _request
	 * @return int 
	 * @throws SQLException
	 */
	public int createTransaction(Connection _conn, WebRequest _request) throws SQLException {
		int counter = createRequestAndInfo(_conn, _request);
		return counter;
	}

	
	public List getConsequenceCounters(Connection conn, long transactionID) throws SQLException {
		List results = new ArrayList();
		WebRequestConsequenceVO consequenceVO = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			String strTransactionID = Long.toString(transactionID);
			log.debug("executing sql " + SQL_STMT_CONSEQUENCE_COUNTERS + " for transaction-id:[" + strTransactionID + "]");
			stmt = conn.prepareStatement(SQL_STMT_CONSEQUENCE_COUNTERS);
			stmt.setString(1, strTransactionID);
			rset = stmt.executeQuery();
			while (rset.next()) {
                
			    consequenceVO = new WebRequestConsequenceVO();
			    consequenceVO.setRuleCode(rset.getString(1));
			    consequenceVO.setRuleName(rset.getString(2));
			    consequenceVO.setCount(rset.getLong(3));
			    
				results.add(consequenceVO);
			}		
			log.debug("Results for consequence counters:" + results);
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}		
		return results;		
	}
	
	public int countWebRequest(Connection _conn, WebRequestCriteria _criteria) throws SQLException {
		List result = selectWebRequestList(_conn, _criteria, null);
		if (result != null) {
			return result.size();
		}
		return 0;
	}

	public int countWebRequestProcesses(Connection _conn, long _requestId, RequestCriteria _criteria)
	    throws SQLException {

		String sql = SQL_STMT_COUNT_WEB_REQUEST_PROCESSES;
		if (_criteria != null) {
			if (_criteria.getLabel() != null) {
				sql += " inner join proc_request b on a.proc_request_id = b.request_id where a.web_request_id = ? and b.request_label like ?";
			} else if (_criteria.getStatus() > 0) {
				sql += " inner join proc_request b on a.proc_request_id = b.request_id where a.web_request_id = ? and b.latest_status = ?";
			} else {
				sql += " where a.web_request_id = ?";
			}
		} else {
			sql += "  where a.web_request_id = ?";
		}

		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			log.debug("executing sql " + sql);
			stmt = _conn.prepareStatement(sql);
			stmt.setLong(1, _requestId);
			if (_criteria != null) {
				if (_criteria.getLabel() != null) {
					stmt.setString(2, _criteria.getLabel());
				} else if (_criteria.getStatus() > 0) {
					stmt.setInt(2, _criteria.getStatus());
				}
			}
			rset = stmt.executeQuery();
			if (rset.next()) {
				return rset.getInt(1);
			}
			return 0;
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

    /**
     * 
     * Is responsible in 
     * 
     * @param _conn
     * @param _requestId
     * @return
     * @throws SQLException
     */
    public Map selectWebRequestProcessesToCounters(Connection _conn, long _requestId)
    throws SQLException {
    PreparedStatement stmt = null;
    ResultSet rset = null;
    try {
        Map resultMap = new HashMap();
        int totalCount = 0;
        log.debug("executing sql for new web counter in list requests " + SQL_STMT_COUNT_WEB_REQUEST_INFO);
        stmt = _conn.prepareStatement(SQL_STMT_COUNT_WEB_REQUEST_INFO);
        stmt.setLong(1, _requestId);
        rset = stmt.executeQuery();
        while(rset.next()){
            int count = rset.getInt("INFO_VALUE");
            totalCount += count;
            resultMap.put(String.valueOf(rset.getInt("REQUEST_STATUS")), new Integer(count));
        }
        resultMap.put(String.valueOf(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED), new Integer(
                totalCount));
        
        
        return resultMap;
    } finally {
        if (rset != null) {
            rset.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }
}

    
    
    
    
    
    public Map selectWebRequestProcessesStatus(Connection _conn, long _requestId)
    throws SQLException {
    PreparedStatement stmt = null;
    ResultSet rset = null;
    try {
        log.debug("executing sql " + SQL_STMT_COUNT_WEB_REQUEST_PROCSTATUS);
        stmt = _conn.prepareStatement(SQL_STMT_COUNT_WEB_REQUEST_PROCSTATUS);
        // log.debug("executing sql " + SQL_STMT_COUNT_WEB_REQUEST_STATUSES);
        // stmt = _conn.prepareStatement(SQL_STMT_COUNT_WEB_REQUEST_STATUSES);
        stmt.setLong(1, _requestId);
        rset = stmt.executeQuery();
        Map resultMap = new HashMap();
         if (rset.next()) {
	         resultMap.put(String.valueOf(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED), new Integer(rset.getInt(1)));
	         resultMap.put(String.valueOf(WebRequestLifeCycle.REQUEST_LIFECYCLE_FINISHED_OK), new Integer(rset.getInt(2)));
         }
        int totalCount = 0;
            while (rset.next()) {
                int statusCount = rset.getInt(2);
                totalCount += statusCount;
                resultMap.put(String.valueOf(rset.getInt(1)), new Integer(statusCount));
            }
            if(totalCount == 0){
                log.debug("executing sql total count ==> " + SQL_STMT_COUNT_WEB_REQUEST_INFO);
                stmt = _conn.prepareStatement(SQL_STMT_COUNT_WEB_REQUEST_INFO);
                stmt.setLong(1, _requestId);
                rset = stmt.executeQuery();
                while(rset.next()){
                    int count = rset.getInt("INFO_VALUE");
                    totalCount += count;
                    resultMap.put(String.valueOf(rset.getInt("REQUEST_STATUS")), new Integer(count));
                }
        }
        resultMap.put(String.valueOf(WebRequestLifeCycle.REQUEST_LIFECYCLE_CREATED), new Integer(
                totalCount));
        
        
        return resultMap;
    } finally {
        if (rset != null) {
            rset.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }
}

    
    /**
     * Is responsible in to  delete a specific web request.
     * It´s a logic delete. This method makes one update in the REQUEST_STATUS for
     * 199 field in the table web_request.
     * 
     * @param id unique identifier for the wer request
     * @throws SQLException if a any exception
     * 
     */
    public void deleteWebRequest(Connection _conn, long id) throws SQLException {
        PreparedStatement stmt = null;

        try {
            String sql = SQL_STMT_DELETE_WEB_REQUEST;
            log.debug("Executing a logic delete web requiest sql " + sql);
            stmt = _conn.prepareStatement(sql);
            stmt.setLong(1, id);
            int status = stmt.executeUpdate();
            if(status <= 0){
                throw new SQLException("An exception occours while a web request deleting  whith status not in (3,4)");
            }
            log.debug("web request " + id + " deleted");
            log.debug("status return web request " + status);
        } catch (SQLException e) {
            log.error("An exception occours while a web request deleting  ", e);
            throw e;
        }
        
        
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    
    
	public WebRequest selectWebRequest(Connection _conn, long _requestId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			String sql = SQL_STMT_SELECT_WEB_REQUEST + " where request_id = ?";
			log.debug("executing sql " + sql);
			stmt = _conn.prepareStatement(sql);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			Map resultMap = new HashMap();
			WebRequestBuilder builder = new WebRequestBuilder();
			WebRequest request = null;
			if (rset.next()) {
				// request_id, start_date, end_date, owner_id, request_status
				builder.buildWebRequest(rset.getLong(4));
				builder.setStartDate(rset.getTimestamp(2));
				builder.setStatus(rset.getInt(5));
				request = builder.getWebRequest();
				request.setRequestId(rset.getLong(1));
				request.setEndDate(rset.getTimestamp(3));
				request.setAdditionalInformation(new HashMap());
				resultMap.put(String.valueOf(request.getRequestId()), request);
			}

			fillAdditionalInfo(_conn, resultMap);
			return request;
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

    
	public List selectWebRequestList(Connection _conn, WebRequestCriteria _criteria,
	    FetchCriteria _fetch) throws SQLException {

		String sql = SQL_STMT_SELECT_WEB_REQUEST;
		boolean whereClause = false;

		if (_criteria.getOwnerId() > 0) {
			sql += " WHERE owner_id = ?";
			whereClause = true;
			log.debug("filtering search by ownerId = " + _criteria.getOwnerId());
		} else if (_criteria.getGroupName() != null) {
			sql = SQL_STMT_SELECT_WEB_REQUEST_BY_SECURITY_GROUP;
			whereClause = true;
			log.debug("filtering search by group = " + _criteria.getGroupName());
		}
		// checking for start date
		if (_criteria.getStartDate() != null) {
			if (!whereClause) {
				sql += " WHERE ";
				whereClause = true;
			} else {
				sql += " AND ";
			}
			sql += " web_request.start_date >= ?";
			log.debug("filtering search by startDate >= " + _criteria.getStartDate());
		}
		// checking for end date
		if (_criteria.getEndDate() != null) {
			if (!whereClause) {
				sql += " WHERE ";
				whereClause = true;
			} else {
				sql += " AND ";
			}
			sql += " web_request.end_date <= ?";
			log.debug("filtering search by endDate <= " + _criteria.getEndDate());
		}
		// checking for status
		if (_criteria.getStatus() > 0) {
			if (!whereClause) {
				sql += " WHERE ";
				whereClause = true;
			} else {
				sql += " AND ";
			}
			sql += " web_request.request_status = ?";
			log.debug("filtering search by status == " + _criteria.getStatus());
		}
        // cheching status exclude
        if (_criteria.getStatusExclude() > 0) {
            if (!whereClause) {
                sql += " WHERE ";
                whereClause = true;
            } else {
                sql += " AND ";
            }
            sql += " web_request.request_status not in ( ? )";
            log.debug("filtering search by status <> " + _criteria.getStatus());
        }
        
        
		sql = JDBCQueryHelper.applyFetchParameters(_conn, sql, _fetch);

		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			log.debug("executing sql " + sql);
			stmt = _conn.prepareStatement(sql);
			int colCount = 1;
			if (_criteria.getOwnerId() > 0) {
				log.debug("OwnerID for above SQL Statement:" + _criteria.getOwnerId());
				stmt.setLong(colCount++, _criteria.getOwnerId());
			} else if (_criteria.getGroupName() != null) {
				log.debug("GroupName for above SQL Statement:" + _criteria.getGroupName());				
				stmt.setString(colCount++, _criteria.getGroupName());
			}

			if (_criteria.getStartDate() != null) {
				log.debug("StartDate for above SQL Statement:" + _criteria.getStartDate());					
				stmt.setDate(colCount++, new java.sql.Date(_criteria.getStartDate().getTime()));
			}
			if (_criteria.getEndDate() != null) {
				log.debug("EndDate for above SQL Statement:" + _criteria.getEndDate());					
				stmt.setDate(colCount++, new java.sql.Date(_criteria.getEndDate().getTime()));
			}
            if (_criteria.getStatus() > 0) {
                log.debug("Status for above SQL Statement:" + _criteria.getStatus());                   
                stmt.setInt(colCount++, _criteria.getStatus());
            }
            if (_criteria.getStatusExclude() > 0) {
                log.debug("StatusExclude for above SQL Statement:" + _criteria.getStatusExclude());                   
                stmt.setInt(colCount++, _criteria.getStatusExclude());
            }
			rset = stmt.executeQuery();
			Map resultMap = new HashMap();
			List resultList = new ArrayList();
			WebRequestBuilder builder = new WebRequestBuilder();
			while (rset.next()) {
				// request_id, start_date, end_date, owner_id, request_status
				builder.buildWebRequest(rset.getLong(4));
				builder.setStartDate(rset.getTimestamp(2));
				builder.setStatus(rset.getInt(5));
				WebRequest request = builder.getWebRequest();
				request.setRequestId(rset.getLong(1));
				request.setEndDate(rset.getTimestamp(3));
				request.setAdditionalInformation(new HashMap());
				resultMap.put(String.valueOf(request.getRequestId()), request);
				resultList.add(request);
				builder.clearWebRequest();
			}

			fillAdditionalInfo(_conn, resultMap);
			return resultList;
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public WebRequest selectWebRequestByProcRequest(Connection _conn, long _procRequestId)
	    throws SQLException {
		return selectWebRequestByProcRequest(_conn, _procRequestId, true);
	}

	public WebRequest selectWebRequestByProcRequest(Connection _conn, long _procRequestId,
	    boolean _loadInfo) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_WEB_REQUEST_BY_PROC_REQUEST);
			stmt.setLong(1, _procRequestId);
			rset = stmt.executeQuery();
			if (rset.next()) {
				if (_loadInfo) {
					return selectWebRequest(_conn, rset.getLong(1));
				} else {
					WebRequest req = new WebRequest();
					req.setRequestId(rset.getLong(1));
					return req;
				}
			}
			return null;
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void updateWebRequest(Connection _conn, long _requestId, WebRequest _newInfo)
	    throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = _conn.prepareStatement(SQL_STMT_UPDATE_WEB_REQUEST);
			stmt.setTimestamp(1, _newInfo.getStartDate());
			stmt.setTimestamp(2, _newInfo.getEndDate());
			stmt.setLong(3, _newInfo.getOwnerId());
			stmt.setInt(4, _newInfo.getStatus());
			stmt.setLong(5, _requestId);
			stmt.executeUpdate();
			log.debug("web request " + _requestId + " information updated");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	// -------------------------
	// protected methods
	// -------------------------

	protected int createRequestAndInfo(Connection _conn, WebRequest _request) throws SQLException {
		PreparedStatement stmt = null;
		int counter = 0;
		try {
			Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
			// get next value id for web request
			_request.setRequestId(JDBCSequenceHelper.nextValue(_conn, SQL_WEB_REQUEST_SEQUENCE));
			// insert into web_request
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_WEB_REQUEST);
			stmt.setLong(1, _request.getRequestId());
			stmt.setLong(2, _request.getOwnerId());
			stmt.setTimestamp(3, now);
			stmt.setInt(4, _request.getStatus());
			counter += stmt.executeUpdate();
			stmt.close();
			log.debug("web request created with id " + _request.getRequestId());
			stmt = null;
			// insert into web_request_info
			if (_request.getAdditionalInformation() != null) {
				log.debug("creating " + _request.getAdditionalInformation().size()
				    + " additional info rows.");
				stmt = _conn.prepareStatement(SQL_STMT_INSERT_WEB_REQUEST_INFO);
				for (Iterator it = _request.getAdditionalInformation().entrySet().iterator(); it.hasNext();) {
					stmt.setLong(1, _request.getRequestId());
					Map.Entry entry = (Map.Entry) it.next();
					stmt.setString(2, (String) entry.getKey());
					stmt.setString(3, (String) entry.getValue());
					counter += stmt.executeUpdate();
					stmt.clearParameters();
				}
			} else {
				log.debug("no additional info found");
			}
			return counter;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	protected int createLink(Connection _conn, WebRequest _request) throws SQLException {
		if (_request.getProcessingRequests() == null) {
			return 0;
		}
		int counter = 0;
		PreparedStatement stmt = null;
		log.debug("creating link for web request " + _request.getRequestId() + " for "
		    + _request.getProcessingRequests().size() + " proc. requests");
		try {
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_WEB_REQUEST_REQUESTS);
			Iterator iterator = _request.getProcessingRequests().iterator();
			while (iterator.hasNext()) {
				stmt.setLong(1, _request.getRequestId());
				stmt.setLong(2, ((Request) iterator.next()).getRequestId());
				stmt.executeUpdate();
				stmt.clearParameters();
				counter++;
			}
			log.debug("finished with " + counter + " links created");
			return counter;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	public int createLink(Connection _conn, long _transactionId, Request _request) throws SQLException {

		PreparedStatement stmt = null;
		log.debug("creating link between web request " + _transactionId 
				+ " and  proc request " + _request.getRequestId());
		try {
			stmt = _conn.prepareStatement(SQL_STMT_INSERT_WEB_REQUEST_REQUESTS);
			stmt.setLong(1, _transactionId);
			stmt.setLong(2, _request.getRequestId());
			stmt.executeUpdate();
			stmt.clearParameters();

			return 1;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	protected void fillAdditionalInfo(Connection _conn, Map _resultMap) throws SQLException {

		String sql = SQL_STMT_SELECT_WEB_REQUEST_INFO;
		sql += " web_request_id between ? and ? ";
//		for (int i = 0; i < _resultMap.size(); i++) {
//			sql += " web_request_id = ? or";
//		}
//		sql = sql.substring(0, sql.length() - 2);
		long minVal = Long.MAX_VALUE;
		long maxVal = Long.MIN_VALUE;
		for (Iterator it = _resultMap.keySet().iterator(); it.hasNext(); ) {
			String id = (String) it.next();
			long currentVal = Long.parseLong(id);
			if ( currentVal > maxVal) {
				maxVal = currentVal;
			} 
			if ( currentVal < minVal) {
				minVal = currentVal;
			}
//			stmt.setLong(colCount, Long.parseLong(id));
//			log.debug("setting parameter " + colCount + " to " + id);
		}
		if (minVal == Long.MAX_VALUE) {
			//Here we had no Requests on the Map.
			minVal = 0;
		}
		if (maxVal == Long.MIN_VALUE) {
			//Here we had no Requests on the map.
			maxVal = Long.MAX_VALUE;
		}
		log.debug("Parameters for below query are as follows.Min:" + minVal + " and Max:" + maxVal);
		log.debug("executing sql " + sql);

		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			stmt = _conn.prepareStatement(sql);
			stmt.setLong(1, minVal);
			stmt.setLong(2, maxVal);
//			int colCount = 1;
//			for (Iterator it = _resultMap.keySet().iterator(); it.hasNext(); colCount++) {
//				String id = (String) it.next();
//				stmt.setLong(colCount, Long.parseLong(id));
//				log.debug("setting parameter " + colCount + " to " + id);
//			}
			rset = stmt.executeQuery();
			while (rset.next()) {
				WebRequest request = (WebRequest) _resultMap.get(String.valueOf(rset.getLong(1)));
				if (request != null) {
					request.getAdditionalInformation().put(rset.getString(2), rset.getString(3));
				}
			}
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}
}
