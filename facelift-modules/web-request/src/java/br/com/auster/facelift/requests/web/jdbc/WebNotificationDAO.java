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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;
import br.com.auster.facelift.requests.web.model.NotificationEmail;
import br.com.auster.facelift.requests.web.model.WebRequest;

/**
 * @author framos
 * @version $Id: WebNotificationDAO.java 274 2006-09-21 13:21:46Z framos $
 */
public class WebNotificationDAO {


	
	// -------------------------
    // Class constants
    // -------------------------
	
	protected static final String SQL_STMT_INSERT_WEB_NOTIFICATION = "insert into web_notification (notification_id, email_address, web_request_id) values ( ?, ? , ? )";
	protected static final String  SQL_WEB_NOTIFICATION_SEQUENCE = "web_notification_sequence";
	
	protected static final String SQL_STMT_SELECT_WEB_NOTIFICATION_LIST = "select notification_id, email_address, sent_datetime from web_notification where web_request_id = ?";
	protected static final String SQL_STMT_SELECT_WEB_NOTIFICATION = "select notification_id, email_address, sent_datetime from web_notification where notification_id = ?";

	protected static final String SQL_STMT_UPDATE_WEB_NOTIFICATION = "update web_notification set email_address = ?, sent_datetime = ? where notification_id = ?";
	
	
    // -------------------------
    // Instance variables
    // -------------------------
	
	private Logger log = LogFactory.getLogger(WebNotificationDAO.class);

	

    // -------------------------
    // public methods
    // -------------------------
	
	public int createNotification(Connection _conn, WebRequest _request) throws SQLException {
		PreparedStatement stmt = null;
		int counter = 0;
		try {
			if (_request.getNotifications() != null) {
				log.debug("creating notification for " + _request.getNotifications().size() + " email addresses.");
				stmt = _conn.prepareStatement(SQL_STMT_INSERT_WEB_NOTIFICATION);				
				for (Iterator it = _request.getNotifications().iterator(); it.hasNext(); ) {
					NotificationEmail email = (NotificationEmail) it.next();
					stmt.setLong(1, JDBCSequenceHelper.nextValue(_conn, SQL_WEB_NOTIFICATION_SEQUENCE));
					stmt.setString(2, email.getEmailAddress());
					stmt.setLong(3, _request.getRequestId());
					counter += stmt.executeUpdate();
					stmt.clearParameters();
				}
			} else {
				log.debug("not notifications set for this request");
			}
			return counter;
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}	
	
	public List selectNotificationList(Connection _conn, long _requestId) throws SQLException {
		
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_WEB_NOTIFICATION_LIST);
			stmt.setLong(1, _requestId);
			rset = stmt.executeQuery();
			List resultList = new ArrayList();
			while (rset.next()) {
				NotificationEmail email = new NotificationEmail();
				email.setNotificationId(rset.getLong(1));
				email.setEmailAddress(rset.getString(2));
				email.setSentDatetime(rset.getTimestamp(3));
				resultList.add(email);
			}
			return resultList;
        } finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
        }
		
	}	
	
	public NotificationEmail selectNotification(Connection _conn, long _notificationId) throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rset = null;
		NotificationEmail email = null;
		try {
			stmt = _conn.prepareStatement(SQL_STMT_SELECT_WEB_NOTIFICATION);
			stmt.setLong(1, _notificationId);
			rset = stmt.executeQuery();
			if (rset.next()) {
				email = new NotificationEmail();
				email.setNotificationId(rset.getLong(1));
				email.setEmailAddress(rset.getString(2));
				email.setSentDatetime(rset.getTimestamp(3));
			}
			return email;
        } finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
        }
		
	}
	
	public void updateNotification(Connection _conn, long _notificationId, NotificationEmail _newInfo) throws SQLException {

		PreparedStatement stmt = null;
		try {
			stmt = _conn.prepareStatement(SQL_STMT_UPDATE_WEB_NOTIFICATION);
			stmt.setString(1, _newInfo.getEmailAddress());
			stmt.setTimestamp(2, _newInfo.getSentDatetime());
			stmt.setLong(3, _notificationId);
			stmt.executeUpdate();
        } finally {
			if (stmt != null) { stmt.close(); }
        }
		
	}
	
	
}

