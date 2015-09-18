/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
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
 * Created on 02/10/2006
 */
package br.com.auster.facelift.users.security;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.auster.common.text.DateFormat;
import br.com.auster.security.base.SecurityException;
import br.com.auster.security.interfaces.SecurityPolicy;
import br.com.auster.security.model.PasswordInfo;
import br.com.auster.security.model.User;

/**
 * @author framos
 * @version $Id$
 */
public class SecurityPolicyImpl implements SecurityPolicy {


	public static final String MAXLOGINS_PROP = "auster.security.expMaxLogins";
	public static final String ERRORLIMIT_PROP = "auster.security.errorLimit";
	public static final String EXPDAYS_PROP = "auster.security.expDaysLimit";
	public static final String WARNMESG_PROP = "auster.security.warnThreshold";
	public static final String INACTIVITYLIMIT_PROP = "auster.security.inactivityLimit";
	public static final String LENIENTUNLOCK_PROP = "auster.security.lenientUnlock";
	public static final String MAXLOGINS_RECOVERED_PROP = "auster.security.recovered.expMaxLogins";
	public static final String EXPDAYS_RECOVERED_PROP = "auster.security.recovered.expDaysLimit";
	public static final String MIN_PASS_CHARS_PROP = "auster.security.minPassChars";

	protected static final String DATE_FORMAT = "dd/MM/yyyy";

	private static final Logger log = Logger.getLogger(SecurityPolicyImpl.class);


	private int maxLogins;
	private int recoveredMaxLogins;
	private int expireDays;
	private int recoveredExpireDays;
	private int warnThreshold;
	private int errorLimit;
	private int inactivityLimit;
	private int minPasswordChars;
	private boolean lenientUnlock;



	public SecurityPolicyImpl(Properties _properties) {
		// setting properties
		_properties.putAll(System.getProperties());
		// setting defaults
		this.errorLimit = 30;
		this.maxLogins = 0;
		this.recoveredMaxLogins = 1;
		this.expireDays = 30;
		this.recoveredExpireDays = 1;
		this.inactivityLimit = 0;
		this.warnThreshold = 50;
		this.minPasswordChars = 8;
		this.lenientUnlock = false;

		// loading properties
		// loading errorLimit property
		if (_properties.containsKey(ERRORLIMIT_PROP)) {
			this.errorLimit = Integer.parseInt(_properties.getProperty(ERRORLIMIT_PROP));
		}
		// loading maxLogins property
		if (_properties.containsKey(MAXLOGINS_PROP)) {
			this.maxLogins = Integer.parseInt(_properties.getProperty(MAXLOGINS_PROP));
		}
		if (_properties.containsKey(MAXLOGINS_RECOVERED_PROP)) {
			this.recoveredMaxLogins = Integer.parseInt(_properties.getProperty(MAXLOGINS_RECOVERED_PROP));
		}
		// loading exp.days property
		if (_properties.containsKey(EXPDAYS_PROP)) {
			this.expireDays = Integer.parseInt(_properties.getProperty(EXPDAYS_PROP));
		}
		if (_properties.containsKey(EXPDAYS_RECOVERED_PROP)) {
			this.recoveredExpireDays = Integer.parseInt(_properties.getProperty(EXPDAYS_RECOVERED_PROP));
		}
		// loading warning threshold property
		if (_properties.containsKey(WARNMESG_PROP)) {
			this.warnThreshold = Integer.parseInt(_properties.getProperty(WARNMESG_PROP));
		}
		// loading inactivity limit property
		if (_properties.containsKey(INACTIVITYLIMIT_PROP)) {
			this.inactivityLimit = Integer.parseInt(_properties.getProperty(INACTIVITYLIMIT_PROP));
		}
		// loading lenient unlock property
		if (_properties.containsKey(LENIENTUNLOCK_PROP)) {
			this.lenientUnlock = Boolean.valueOf(_properties.getProperty(LENIENTUNLOCK_PROP)).booleanValue();
		}
		// loading min chars in password property
		if (_properties.containsKey(MIN_PASS_CHARS_PROP)) {
			this.minPasswordChars = Integer.parseInt(_properties.getProperty(MIN_PASS_CHARS_PROP));
		}
	}


	/**
	 * @see br.com.auster.security.interfaces.SecurityPolicy#acceptAuthenticate(br.com.auster.security.model.User)
	 */
	public boolean acceptAuthenticate(User _user, PasswordInfo _password) throws SecurityException {
		// validate errorCount vs. errorLimit
		if (this.errorLimit <= _password.getErrorCount()) {
			throw new MaxIncorrectLoginAttemptsException("Max. number of attemps reached: " + this.errorLimit);
		}
		// validate maxCount vs. usedCount
		if (_password.getExpirationCount() > 0) {
			if (_password.getExpirationCount() <= _password.getUsedCount()) {
				throw new PasswordExpiredException("Max. number of logins reached: " + _password.getExpirationCount());
			} else {
				int diff = _password.getExpirationCount() - _password.getUsedCount();
				_password.setInWarningRange(diff <= this.warnThreshold);
			}
		}
		// validate expiration by date
		Calendar c = Calendar.getInstance();
		Date today = c.getTime();
		if (_password.getExpirationDate() != null) {
			if (today.after(_password.getExpirationDate())) {
				throw new PasswordExpiredException("Expiration date reached: " + _password.getExpirationCount());
			}
			if (!_password.isInWarningRange()) {
				try {
					SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
					long diff = DateFormat.difference(f.format(today), DATE_FORMAT, f.format(_password.getExpirationDate()), DATE_FORMAT,  'd');
					// validate to-warning message
						_password.setInWarningRange(diff <= this.warnThreshold);
				} catch (ParseException pe) {
					log.warn("Could not validate inactivity period", pe);
				}
			}
		}
		// validate expiration by inactivity period
		if ((this.inactivityLimit > 0) && (_password.getLastUsed() != null)) {
			SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
			c.setTime(_password.getLastUsed());
			try {
				long diff = DateFormat.difference(f.format(c.getTime()), DATE_FORMAT, f.format(today), DATE_FORMAT, 'd');
				if (this.inactivityLimit <= diff) {
					throw new PasswordInvalidatedException("Inactivity limit reached: " + this.inactivityLimit);
				}
			} catch (ParseException pe) {
				log.warn("Could not validate inactivity period", pe);
			}
		}
		return true;
	}

	/**
	 * @see br.com.auster.security.interfaces.SecurityPolicy#acceptLock(br.com.auster.security.model.User, br.com.auster.security.model.User)
	 */
	public boolean acceptLock(User _user, String _admin) throws SecurityException {
		return true;
	}

	/**
	 * @see br.com.auster.security.interfaces.SecurityPolicy#acceptUnlock(br.com.auster.security.model.User, br.com.auster.security.model.User)
	 */
	public boolean acceptUnlock(User _user, String _admin) throws SecurityException {
		return ((!this.lenientUnlock) || (_admin != null));
	}

	/**
	 * @see br.com.auster.security.interfaces.SecurityPolicy#applyExpirationRules(br.com.auster.security.model.PasswordInfo, br.com.auster.security.model.User)
	 */
	public boolean applyExpirationRules(PasswordInfo _password, String _admin) throws SecurityException {
		int mxLogs = this.maxLogins;
		int mxDate = this.expireDays;
		if (_admin == null) {
			mxLogs = this.recoveredMaxLogins;
			mxDate = this.recoveredExpireDays;
		}
		// marking exception count
		_password.setExpirationCount(mxLogs);
		// marking expiration date
		if (mxDate >= 0) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, mxDate);
			_password.setExpirationDate(new java.sql.Timestamp(c.getTimeInMillis()));
		}
		return true;
	}


	/**
	 * @see br.com.auster.security.interfaces.SecurityPolicy#acceptPassword(java.lang.String)
	 */
	public boolean acceptPassword(String _password) throws SecurityException {
		return (_password != null) && (_password.length() >= this.minPasswordChars) && checkComplexity(_password);
	}

	protected boolean checkComplexity(String _password) {
		boolean[] complexity = new boolean[4];
		for (int i = 0; i < _password.length(); i++) {
			char currChar = _password.charAt(i);
			// lower case char
			complexity[0] = complexity[0] || (Character.isLowerCase(currChar));
			// upper case char
			complexity[1] = complexity[1] || (Character.isUpperCase(currChar));
			// digit char
			complexity[2] = complexity[2] || (Character.isDigit(currChar));
			// special char
			complexity[3] = complexity[3] || (!Character.isLowerCase(currChar) &&
					                          !Character.isUpperCase(currChar) &&
					                          !Character.isDigit(currChar) &&
					                          !Character.isWhitespace(currChar));
		}
		// result expects that each position is TRUE
		return complexity[0] && complexity[1] && complexity[2] && complexity[3];
	}
}
