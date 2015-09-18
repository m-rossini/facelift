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

import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.users.interfaces.UserManager;
import br.com.auster.persistence.PersistenceResourceAccessException;
import br.com.auster.persistence.PersistenceService;
import br.com.auster.security.base.BaseSecurityFacadeImpl;
import br.com.auster.security.base.SecurityException;
import br.com.auster.security.model.PasswordInfo;

/**
 * @author framos
 * @version $Id$
 */
public class UserManagerImpl extends BaseSecurityFacadeImpl implements UserManager {


	private static final Logger log = Logger.getLogger(UserManagerImpl.class);



	public UserManagerImpl() {
		// persistence will remaing null while not initialized
		super(null);
	}

	/**
	 * @see br.com.auster.facelift.services.Service#init(org.w3c.dom.Element)
	 */
	public void init(Element _configuration) throws ConfigurationException {
		try {
			// getting persistence configuration
			log.info("Configuring User Manager service");
			String klass = DOMUtils.getAttribute(_configuration, "persistence", true);
			this.persistence = (PersistenceService) Class.forName(klass).newInstance();
			this.persistence.init(DOMUtils.getElement(_configuration, "persistence-configuration", true));
			log.info("persistence class = " + klass);
			// init. security policy
			String filename = DOMUtils.getAttribute(_configuration, "policy", false);
			if ((filename != null) && (filename.length() > 1)) {
				Properties p = new Properties();
				try {
					p.load(IOUtils.openFileForRead(filename));
				} catch (IOException ioe) {
					log.warn("Policy file " + filename + " not found. Defaulting all policies.");
				}
				this.setPolicies(new SecurityPolicyImpl(p));
				log.info("security policies initialized.");
			}

        } catch (br.com.auster.persistence.ConfigurationException ce) {
            throw new ConfigurationException("could not initialize persistence service", ce);
		} catch (InstantiationException ie) {
			throw new ConfigurationException("could not initialize persistence service", ie);
		} catch (IllegalAccessException iae) {
			throw new ConfigurationException("could not create persistence service", iae);
		} catch (ClassNotFoundException cnfe) {
			throw new ConfigurationException("could not find persistence service class", cnfe);
        }
	}


	/**
	 * This method will return if the user should be warned about password expiration
	 */
	public boolean inWarnRange(String _userId) throws SecurityException {
		Connection conn = null;
		try {
			conn = (Connection) this.persistence.openResourceConnection();
			PasswordInfo passwd = this.passwordDAO.getCurrentPassword(conn, _userId);
			if (this.policy != null) {
				this.policy.acceptAuthenticate(null, passwd);
			}
			return passwd.isInWarningRange();
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			try {
				this.persistence.closeResourceConnection(conn);
			} catch (PersistenceResourceAccessException prae) {
				throw new SecurityException(prae);
			}
		}
	}
}
