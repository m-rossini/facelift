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
 * Created on Apr 14, 2005
 */
package br.com.auster.facelift.requests.web.model;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * Represents an email address and the time the email was sent to it. Notifications are sent whenever its configured to, and
 * 	a web request can have more than one emails to notify.  
 * 
 * @hibernate.class
 *          table="WEB_NOTIFICATION"
 *          
 * @author framos
 * @version $Id$
 */
public class NotificationEmail implements Serializable, Comparable {

    

    // -------------------------
    // Instance variables
    // -------------------------    
    
    private long notificationId;
    private String emailAddress;
    private Timestamp sentDatetime;
    
    private WebRequest relatedRequest;
    
    
    
    // -------------------------
    // Constructor
    // -------------------------    
    
    public NotificationEmail() {
        this(0);
    }
    
    public NotificationEmail(long _id) {
        setNotificationId(_id);
    }

    

    // -------------------------
    // Public methods
    // -------------------------

    /**
     * @hibernate.property
     *          column="EMAIL_ADDRESS"
     *          type="string"
     *          length="256"
     *          not-null="true"
     */
    public final String getEmailAddress() {
        return emailAddress;
    }
   
    public final void setEmailAddress(String _emailAddress) {
        this.emailAddress = _emailAddress;
    }   

    /**
     * @hibernate.id
     *          column="NOTIFICATION_ID"
     *          type="long"
     *          not-null="true"
     *          unsaved-value="0"
     *          generator-class="sequence"
     *          
     * @hibernate.generator-param
     *          name="sequence"
     *          value="web_notification_sequence"          
     */
    public final long getNotificationId() {
        return notificationId;
    }

    public final void setNotificationId(long _notificationId) {
        this.notificationId = _notificationId;
    }
    
    /**
     * @hibernate.many-to-one
     *          column="WEB_REQUEST_ID"
     *          not-null="true"         
     */
    public final WebRequest getRequest() {
        return relatedRequest;
    }
    
    public final void setRequest(WebRequest _request) {
        this.relatedRequest = _request;
    }
    
    /**
     * @hibernate.property
     *          column="SENT_DATETIME"
     *          type="timestamp"
     *          not-null="false"
     */
    public final Timestamp getSentDatetime() {
        return sentDatetime;
    }   

    public final void setSentDatetime(Timestamp _datetime) {
        this.sentDatetime = _datetime;
    }    
    
    
    
    // -------------------------
    // Object overwrites
    // -------------------------
    
    public boolean equals(Object _other) {
        try {
            return (((NotificationEmail)_other).getNotificationId() == this.getNotificationId()  &&
                    (this.getNotificationId() != 0));
        } catch (ClassCastException cce) {
            //TODO log
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }
    
    public int hashCode() {
        int result = 17;
        result = 37*result + (int) this.getNotificationId();
        return result;
    }
    
    public String toString() {
        return "[" + getClass().getName() + " : " +
               " id=" + getNotificationId() +
               " email=" + getEmailAddress() +
               " sent=" + getSentDatetime() +
               "]";
    }
    

    
    // -------------------------
    // Comparable overwrites
    // -------------------------
        
    public int compareTo(Object _other) {
        try {
            return (int) (this.getNotificationId() - ((NotificationEmail) _other).getNotificationId());
        } catch (ClassCastException cce) {
            // TODO log as error
            return 0;
        } catch (NullPointerException npe) {
            // TODO log as error
            return 0;
        }
    }        
}
